package com.example.data.model

data class VpnSettings(
    val selectedProtocol: String = "WireGuard", // WireGuard, OpenVPN UDP, OpenVPN TCP, IKEv2, Shadowsocks
    val killSwitchEnabled: Boolean = true,
    val autoConnectOnWifi: Boolean = true,
    val splitTunnelingEnabled: Boolean = false,
    val adAndMalwareBlockerEnabled: Boolean = true,
    val dnsLeakProtectionEnabled: Boolean = true,
    val bypassAppsCount: Int = 3
)
