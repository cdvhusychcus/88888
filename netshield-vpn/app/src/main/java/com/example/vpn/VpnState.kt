package com.example.vpn

enum class VpnConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

data class LiveVpnTraffic(
    val downloadSpeedKbps: Float = 0f,
    val uploadSpeedKbps: Float = 0f,
    val totalBytesDownloaded: Long = 0L,
    val totalBytesUploaded: Long = 0L,
    val durationSeconds: Long = 0L,
    val currentIp: String = "185.220.101.42"
)
