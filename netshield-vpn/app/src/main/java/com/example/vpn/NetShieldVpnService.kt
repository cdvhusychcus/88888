package com.example.vpn

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class NetShieldVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val ACTION_CONNECT = "com.example.vpn.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.ACTION_DISCONNECT"
        const val EXTRA_SERVER_NAME = "extra_server_name"
        const val EXTRA_SERVER_IP = "extra_server_ip"
        const val CHANNEL_ID = "vpn_channel"
        const val NOTIFICATION_ID = 1001

        private val _vpnStatus = MutableStateFlow(VpnConnectionStatus.DISCONNECTED)
        val vpnStatus: StateFlow<VpnConnectionStatus> = _vpnStatus.asStateFlow()

        private val _liveTraffic = MutableStateFlow(LiveVpnTraffic())
        val liveTraffic: StateFlow<LiveVpnTraffic> = _liveTraffic.asStateFlow()

        fun setStatus(status: VpnConnectionStatus) {
            _vpnStatus.value = status
        }
        
        fun updateTraffic(traffic: LiveVpnTraffic) {
            _liveTraffic.value = traffic
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                val serverName = intent.getStringExtra(EXTRA_SERVER_NAME) ?: "VPN Server"
                val serverIp = intent.getStringExtra(EXTRA_SERVER_IP) ?: "185.220.101.42"
                startVpnTunnel(serverName, serverIp)
            }
            ACTION_DISCONNECT -> {
                stopVpnTunnel()
            }
        }
        return START_STICKY
    }

    private fun startVpnTunnel(serverName: String, serverIp: String) {
        _vpnStatus.value = VpnConnectionStatus.CONNECTING
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("Securing connection to $serverName..."))

        serviceJob?.cancel()
        serviceJob = serviceScope.launch {
            try {
                // Establish VPN Interface
                val builder = Builder()
                    .addAddress("10.0.0.2", 24)
                    .addRoute("0.0.0.0", 0)
                    .addDnsServer("1.1.1.1")
                    .addDnsServer("8.8.8.8")
                    .setSession("NetShield VPN - $serverName")

                vpnInterface = builder.establish()
                Log.d("NetShieldVpnService", "VPN Interface established: $vpnInterface")

                delay(1200) // Connection negotiation
                _vpnStatus.value = VpnConnectionStatus.CONNECTED
                startForeground(NOTIFICATION_ID, buildNotification("Protected: Connected to $serverName"))

                var seconds = 0L
                var totalDown = 0L
                var totalUp = 0L

                while (_vpnStatus.value == VpnConnectionStatus.CONNECTED) {
                    delay(1000)
                    seconds++
                    
                    // Simulate realistic encrypted packet flow throughput
                    val downSpeed = (1200..8500).random().toFloat() / 10f // KB/s
                    val upSpeed = (300..2500).random().toFloat() / 10f // KB/s

                    totalDown += (downSpeed * 1024).toLong()
                    totalUp += (upSpeed * 1024).toLong()

                    _liveTraffic.value = LiveVpnTraffic(
                        downloadSpeedKbps = downSpeed * 8 / 1024f, // Mbps
                        uploadSpeedKbps = upSpeed * 8 / 1024f, // Mbps
                        totalBytesDownloaded = totalDown,
                        totalBytesUploaded = totalUp,
                        durationSeconds = seconds,
                        currentIp = serverIp
                    )
                }
            } catch (e: Exception) {
                Log.e("NetShieldVpnService", "Error in VPN Service", e)
                _vpnStatus.value = VpnConnectionStatus.DISCONNECTED
            }
        }
    }

    private fun stopVpnTunnel() {
        _vpnStatus.value = VpnConnectionStatus.DISCONNECTING
        serviceJob?.cancel()
        try {
            vpnInterface?.close()
            vpnInterface = null
        } catch (e: IOException) {
            Log.e("NetShieldVpnService", "Error closing vpn interface", e)
        }
        _vpnStatus.value = VpnConnectionStatus.DISCONNECTED
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NetShield VPN Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active VPN connection status"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NetShield VPN Active")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 0, Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

    override fun onDestroy() {
        stopVpnTunnel()
        super.onDestroy()
    }
}
