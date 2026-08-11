package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ConnectionLog
import com.example.data.model.SpeedTestLog
import com.example.data.model.VpnServer
import com.example.data.model.VpnSettings
import com.example.data.repository.VpnRepository
import com.example.vpn.NetShieldVpnService
import com.example.vpn.VpnConnectionStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SpeedTestUiState(
    val isRunning: Boolean = false,
    val progress: Float = 0f, // 0.0 to 1.0
    val phase: String = "Ready", // "Ping", "Download", "Upload", "Completed"
    val pingMs: Int = 0,
    val jitterMs: Int = 0,
    val downloadMbps: Float = 0f,
    val uploadMbps: Float = 0f
)

class VpnViewModel(private val repository: VpnRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeDefaultServersIfEmpty()
        }
    }

    val vpnStatus = NetShieldVpnService.vpnStatus
    val liveTraffic = NetShieldVpnService.liveTraffic

    val allServers = repository.allServers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val connectionLogs = repository.connectionLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val speedTestLogs = repository.speedTestLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedServer = MutableStateFlow<VpnServer?>(null)
    val selectedServer: StateFlow<VpnServer?> = _selectedServer.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredServers: StateFlow<List<VpnServer>> = combine(
        allServers,
        _searchQuery,
        _selectedCategory
    ) { servers, query, category ->
        servers.filter { server ->
            val matchesCategory = when (category) {
                "Favorites" -> server.isFavorite
                "All" -> true
                else -> server.category.equals(category, ignoreCase = true)
            }
            val matchesQuery = query.isBlank() ||
                    server.country.contains(query, ignoreCase = true) ||
                    server.city.contains(query, ignoreCase = true) ||
                    server.ipAddress.contains(query)

            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _settings = MutableStateFlow(VpnSettings())
    val settings: StateFlow<VpnSettings> = _settings.asStateFlow()

    private val _speedTestState = MutableStateFlow(SpeedTestUiState())
    val speedTestState: StateFlow<SpeedTestUiState> = _speedTestState.asStateFlow()

    private var speedTestJob: Job? = null

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(server: VpnServer) {
        viewModelScope.launch {
            repository.toggleFavorite(server)
        }
    }

    fun toggleConnect(context: Context) {
        val currentStatus = vpnStatus.value
        val server = _selectedServer.value ?: allServers.value.firstOrNull() ?: VpnServer(
            country = "Japan",
            city = "Tokyo - Fast 01",
            countryCode = "JP",
            flagEmoji = "🇯🇵",
            ipAddress = "139.162.88.42",
            pingMs = 28,
            loadPercent = 32
        )

        if (currentStatus == VpnConnectionStatus.CONNECTED || currentStatus == VpnConnectionStatus.CONNECTING) {
            // Disconnect
            val intent = Intent(context, NetShieldVpnService::class.java).apply {
                action = NetShieldVpnService.ACTION_DISCONNECT
            }
            context.startService(intent)

            // Save log
            val currentTraffic = liveTraffic.value
            viewModelScope.launch {
                repository.logConnection(
                    ConnectionLog(
                        serverName = server.city,
                        country = server.country,
                        flagEmoji = server.flagEmoji,
                        durationSeconds = currentTraffic.durationSeconds,
                        bytesDownloaded = currentTraffic.totalBytesDownloaded,
                        bytesUploaded = currentTraffic.totalBytesUploaded,
                        protocol = _settings.value.selectedProtocol
                    )
                )
            }
        } else {
            // Connect
            _selectedServer.value = server
            val intent = Intent(context, NetShieldVpnService::class.java).apply {
                action = NetShieldVpnService.ACTION_CONNECT
                putExtra(NetShieldVpnService.EXTRA_SERVER_NAME, "${server.country} (${server.city})")
                putExtra(NetShieldVpnService.EXTRA_SERVER_IP, server.ipAddress)
            }
            context.startService(intent)
        }
    }

    fun runSpeedTest() {
        if (_speedTestState.value.isRunning) return

        speedTestJob?.cancel()
        speedTestJob = viewModelScope.launch {
            _speedTestState.value = SpeedTestUiState(isRunning = true, phase = "Testing Ping...")
            
            // Phase 1: Ping Test
            var currentPing = 0
            for (i in 1..10) {
                delay(100)
                currentPing = (20..38).random()
                _speedTestState.value = _speedTestState.value.copy(
                    progress = i * 0.02f,
                    pingMs = currentPing,
                    jitterMs = (1..5).random()
                )
            }

            // Phase 2: Download Speed
            _speedTestState.value = _speedTestState.value.copy(phase = "Testing Download Speed...")
            val targetDownload = (85..145).random().toFloat()
            var currentDownload = 0f
            for (i in 1..25) {
                delay(80)
                currentDownload = (targetDownload * (i / 25f)) + (-5..5).random()
                _speedTestState.value = _speedTestState.value.copy(
                    progress = 0.2f + (i / 25f) * 0.4f,
                    downloadMbps = currentDownload.coerceAtLeast(0f)
                )
            }

            // Phase 3: Upload Speed
            _speedTestState.value = _speedTestState.value.copy(phase = "Testing Upload Speed...")
            val targetUpload = (35..75).random().toFloat()
            var currentUpload = 0f
            for (i in 1..20) {
                delay(80)
                currentUpload = (targetUpload * (i / 20f)) + (-3..3).random()
                _speedTestState.value = _speedTestState.value.copy(
                    progress = 0.6f + (i / 20f) * 0.4f,
                    uploadMbps = currentUpload.coerceAtLeast(0f)
                )
            }

            // Phase 4: Complete & Log
            val finalState = SpeedTestUiState(
                isRunning = false,
                progress = 1.0f,
                phase = "Speed Test Completed",
                pingMs = currentPing,
                jitterMs = (2..6).random(),
                downloadMbps = currentDownload,
                uploadMbps = currentUpload
            )
            _speedTestState.value = finalState

            val activeServer = _selectedServer.value?.city ?: "Auto Optimal Server"
            repository.logSpeedTest(
                SpeedTestLog(
                    downloadMbps = currentDownload,
                    uploadMbps = currentUpload,
                    pingMs = currentPing,
                    jitterMs = finalState.jitterMs,
                    serverName = activeServer
                )
            )
        }
    }

    fun updateSettings(newSettings: VpnSettings) {
        _settings.value = newSettings
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }
}

class VpnViewModelFactory(private val repository: VpnRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VpnViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VpnViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
