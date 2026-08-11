package com.example.data.repository

import com.example.data.local.VpnDao
import com.example.data.model.ConnectionLog
import com.example.data.model.SpeedTestLog
import com.example.data.model.VpnServer
import kotlinx.coroutines.flow.Flow

class VpnRepository(private val vpnDao: VpnDao) {

    val allServers: Flow<List<VpnServer>> = vpnDao.getAllServers()
    val favoriteServers: Flow<List<VpnServer>> = vpnDao.getFavoriteServers()
    val connectionLogs: Flow<List<ConnectionLog>> = vpnDao.getAllConnectionLogs()
    val speedTestLogs: Flow<List<SpeedTestLog>> = vpnDao.getAllSpeedTestLogs()

    suspend fun initializeDefaultServersIfEmpty() {
        if (vpnDao.getServerCount() == 0) {
            val initialServers = listOf(
                VpnServer(
                    country = "Japan",
                    city = "Tokyo - Node 01",
                    countryCode = "JP",
                    flagEmoji = "🇯🇵",
                    ipAddress = "139.162.88.42",
                    pingMs = 28,
                    loadPercent = 32,
                    isFavorite = true,
                    category = "Fastest",
                    protocol = "WireGuard",
                    rating = 4.9f
                ),
                VpnServer(
                    country = "Singapore",
                    city = "Singapore - Express 02",
                    countryCode = "SG",
                    flagEmoji = "🇸🇬",
                    ipAddress = "128.199.201.15",
                    pingMs = 35,
                    loadPercent = 41,
                    isFavorite = true,
                    category = "Fastest",
                    protocol = "WireGuard",
                    rating = 4.8f
                ),
                VpnServer(
                    country = "United States",
                    city = "Los Angeles - Stream 01",
                    countryCode = "US",
                    flagEmoji = "🇺🇸",
                    ipAddress = "104.237.135.22",
                    pingMs = 110,
                    loadPercent = 58,
                    isFavorite = false,
                    category = "Streaming",
                    protocol = "WireGuard",
                    rating = 4.9f
                ),
                VpnServer(
                    country = "United States",
                    city = "New York - Node 04",
                    countryCode = "US",
                    flagEmoji = "🇺🇸",
                    ipAddress = "45.79.182.10",
                    pingMs = 135,
                    loadPercent = 45,
                    isFavorite = false,
                    category = "All",
                    protocol = "OpenVPN UDP",
                    rating = 4.7f
                ),
                VpnServer(
                    country = "Germany",
                    city = "Frankfurt - Secure 01",
                    countryCode = "DE",
                    flagEmoji = "🇩🇪",
                    ipAddress = "139.59.208.99",
                    pingMs = 165,
                    loadPercent = 29,
                    isFavorite = false,
                    category = "P2P",
                    protocol = "WireGuard",
                    rating = 4.8f
                ),
                VpnServer(
                    country = "United Kingdom",
                    city = "London - Media 03",
                    countryCode = "GB",
                    flagEmoji = "🇬🇧",
                    ipAddress = "178.62.88.112",
                    pingMs = 175,
                    loadPercent = 64,
                    isFavorite = false,
                    category = "Streaming",
                    protocol = "WireGuard",
                    rating = 4.7f
                ),
                VpnServer(
                    country = "South Korea",
                    city = "Seoul - Low Ping Gaming",
                    countryCode = "KR",
                    flagEmoji = "🇰🇷",
                    ipAddress = "118.67.130.8",
                    pingMs = 42,
                    loadPercent = 38,
                    isFavorite = true,
                    category = "Gaming",
                    protocol = "WireGuard",
                    rating = 4.9f
                ),
                VpnServer(
                    country = "Hong Kong",
                    city = "Hong Kong - Cyber 01",
                    countryCode = "HK",
                    flagEmoji = "🇭🇰",
                    ipAddress = "103.224.80.5",
                    pingMs = 48,
                    loadPercent = 52,
                    isFavorite = false,
                    category = "Fastest",
                    protocol = "Shadowsocks",
                    rating = 4.8f
                ),
                VpnServer(
                    country = "Australia",
                    city = "Sydney - Pacific 01",
                    countryCode = "AU",
                    flagEmoji = "🇦🇺",
                    ipAddress = "139.99.160.2",
                    pingMs = 150,
                    loadPercent = 30,
                    isFavorite = false,
                    category = "All",
                    protocol = "WireGuard",
                    rating = 4.6f
                ),
                VpnServer(
                    country = "Canada",
                    city = "Toronto - Shield 02",
                    countryCode = "CA",
                    flagEmoji = "🇨🇦",
                    ipAddress = "159.203.22.4",
                    pingMs = 140,
                    loadPercent = 33,
                    isFavorite = false,
                    category = "P2P",
                    protocol = "OpenVPN UDP",
                    rating = 4.7f
                ),
                VpnServer(
                    country = "Switzerland",
                    city = "Zurich - Ultra Privacy",
                    countryCode = "CH",
                    flagEmoji = "🇨🇭",
                    ipAddress = "185.220.101.5",
                    pingMs = 180,
                    loadPercent = 22,
                    isFavorite = false,
                    category = "All",
                    protocol = "WireGuard",
                    rating = 5.0f
                )
            )
            vpnDao.insertServers(initialServers)
        }
    }

    suspend fun toggleFavorite(server: VpnServer) {
        vpnDao.updateServer(server.copy(isFavorite = !server.isFavorite))
    }

    suspend fun logConnection(log: ConnectionLog) {
        vpnDao.insertConnectionLog(log)
    }

    suspend fun logSpeedTest(log: SpeedTestLog) {
        vpnDao.insertSpeedTestLog(log)
    }

    suspend fun clearLogs() {
        vpnDao.clearConnectionLogs()
    }
}
