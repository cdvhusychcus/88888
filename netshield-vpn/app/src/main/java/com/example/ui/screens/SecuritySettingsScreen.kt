package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VpnViewModel

@Composable
fun SecuritySettingsScreen(
    viewModel: VpnViewModel,
    onNavigateToLogs: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    var showProtocolDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Text(
            text = "Security & Encryption Settings",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Configure protocols, kill switch & privacy features",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Protocol Selection Card
        Text(
            text = "VPN TUNNEL PROTOCOL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val protocols = listOf(
                    Triple("WireGuard", "Ultra-fast, state-of-the-art modern protocol (Recommended)", "UDP / ChaCha20"),
                    Triple("OpenVPN UDP", "High-security open-source standard with top stability", "UDP / AES-256"),
                    Triple("OpenVPN TCP", "Bypasses restrictive firewalls and network blocks", "TCP / AES-256"),
                    Triple("IKEv2 / IPsec", "Ideal for mobile switching between Wi-Fi and Cellular", "UDP / AES-256"),
                    Triple("Shadowsocks", "Stealth proxy protocol for severe network censorship", "Encrypted Tunnel")
                )

                protocols.forEach { (name, desc, detail) ->
                    val isSelected = settings.selectedProtocol == name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.updateSettings(settings.copy(selectedProtocol = name))
                            }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.updateSettings(settings.copy(selectedProtocol = name))
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = NeonCyan,
                                    unselectedColor = TextMuted
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) NeonCyan else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (isSelected) NeonCyan.copy(alpha = 0.15f) else CyberCardBorder
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = detail,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) NeonCyan else TextMuted
                                        )
                                    }
                                }
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Security Features Toggles
        Text(
            text = "PRIVACY & PROTECTION TOGGLES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // Kill Switch
                SettingToggleRow(
                    icon = Icons.Filled.Lock,
                    title = "System Kill Switch",
                    description = "Block internet access automatically if VPN connection drops",
                    checked = settings.killSwitchEnabled,
                    onCheckedChange = {
                        viewModel.updateSettings(settings.copy(killSwitchEnabled = it))
                    },
                    testTag = "kill_switch_toggle"
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CyberCardBorder))

                // Auto Connect Wi-Fi
                SettingToggleRow(
                    icon = Icons.Filled.Wifi,
                    title = "Auto-Connect on Unsafe Wi-Fi",
                    description = "Enable VPN protection on public or untrusted Wi-Fi hotspots",
                    checked = settings.autoConnectOnWifi,
                    onCheckedChange = {
                        viewModel.updateSettings(settings.copy(autoConnectOnWifi = it))
                    },
                    testTag = "auto_wifi_toggle"
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CyberCardBorder))

                // Ad & Malware Blocker
                SettingToggleRow(
                    icon = Icons.Filled.Block,
                    title = "Ad & Cyber Threat Shield",
                    description = "Block malicious ads, malware domains, and tracker scripts via DNS",
                    checked = settings.adAndMalwareBlockerEnabled,
                    onCheckedChange = {
                        viewModel.updateSettings(settings.copy(adAndMalwareBlockerEnabled = it))
                    },
                    testTag = "ad_blocker_toggle"
                )

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CyberCardBorder))

                // Split Tunneling
                SettingToggleRow(
                    icon = Icons.Filled.Shield,
                    title = "Split Tunneling",
                    description = "Select specific Android apps to bypass or route through VPN (${settings.bypassAppsCount} apps bypass)",
                    checked = settings.splitTunnelingEnabled,
                    onCheckedChange = {
                        viewModel.updateSettings(settings.copy(splitTunnelingEnabled = it))
                    },
                    testTag = "split_tunneling_toggle"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logs & History Section Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onNavigateToLogs() }
                .testTag("view_connection_logs_card"),
            colors = CardDefaults.cardColors(containerColor = CyberCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Connection Audit Logs",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "View session history, protocol stats & duration",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "View Logs",
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (checked) NeonCyan.copy(alpha = 0.15f) else CyberCardBorder),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) NeonCyan else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = CyberCardBorder
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
