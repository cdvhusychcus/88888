package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VpnServer
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.DisconnectRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VpnViewModel
import com.example.vpn.VpnConnectionStatus

@Composable
fun MainDashboardScreen(
    viewModel: VpnViewModel,
    onNavigateToServers: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val status by viewModel.vpnStatus.collectAsState()
    val traffic by viewModel.liveTraffic.collectAsState()
    val allServers by viewModel.allServers.collectAsState()
    val selectedServerState by viewModel.selectedServer.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val currentServer = selectedServerState ?: allServers.firstOrNull() ?: VpnServer(
        country = "Japan",
        city = "Tokyo - Node 01",
        countryCode = "JP",
        flagEmoji = "🇯🇵",
        ipAddress = "139.162.88.42",
        pingMs = 28,
        loadPercent = 32
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        TopDashboardHeader(
            protocolName = settings.selectedProtocol,
            onSettingsClick = onNavigateToSettings
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Connection Glow Circle & Connect Toggle
        ConnectToggleButton(
            status = status,
            onClick = { viewModel.toggleConnect(context) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Status Banner Text
        ConnectionStatusText(status = status, durationSeconds = traffic.durationSeconds)

        Spacer(modifier = Modifier.height(24.dp))

        // Selected Server Card
        SelectedServerCard(
            server = currentServer,
            onClick = onNavigateToServers
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Traffic Speed & Data Meter Card
        TrafficMeterCard(
            status = status,
            downloadSpeedMbps = traffic.downloadSpeedKbps,
            uploadSpeedMbps = traffic.uploadSpeedKbps,
            totalDownloadedBytes = traffic.totalBytesDownloaded,
            totalUploadedBytes = traffic.totalBytesUploaded
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Security Features Quick Overview
        SecurityBadgesCard(
            currentIp = if (status == VpnConnectionStatus.CONNECTED) traffic.currentIp else "122.116.48.102 (Unprotected)",
            isProtected = status == VpnConnectionStatus.CONNECTED
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TopDashboardHeader(
    protocolName: String,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "NetShield VPN",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Cyber Security & Encryption",
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onSettingsClick() },
            color = CyberCard,
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = protocolName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeonCyan
                )
            }
        }
    }
}

@Composable
private fun ConnectToggleButton(
    status: VpnConnectionStatus,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val glowColor = when (status) {
        VpnConnectionStatus.CONNECTED -> NeonGreen
        VpnConnectionStatus.CONNECTING -> NeonCyan
        VpnConnectionStatus.DISCONNECTING -> DisconnectRed
        VpnConnectionStatus.DISCONNECTED -> NeonBlue
    }

    Box(
        modifier = Modifier
            .size(220.dp)
            .testTag("vpn_connect_button_container"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring
        if (status == VpnConnectionStatus.CONNECTED || status == VpnConnectionStatus.CONNECTING) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(if (status == VpnConnectionStatus.CONNECTED) pulseScale else 1.0f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = 0.35f),
                                glowColor.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Rotating Accent Arc for connecting state
        if (status == VpnConnectionStatus.CONNECTING) {
            Canvas(modifier = Modifier.size(190.dp).rotate(rotationAngle)) {
                drawArc(
                    brush = Brush.sweepGradient(listOf(NeonCyan, Color.Transparent, NeonCyan)),
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }

        // Main Circular Button
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CyberCard, CyberCard.copy(alpha = 0.8f))
                    )
                )
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        listOf(glowColor, glowColor.copy(alpha = 0.3f))
                    ),
                    shape = CircleShape
                )
                .clickable { onClick() }
                .testTag("vpn_connect_toggle_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = when (status) {
                        VpnConnectionStatus.CONNECTED -> Icons.Filled.VerifiedUser
                        VpnConnectionStatus.CONNECTING -> Icons.Filled.Shield
                        else -> Icons.Filled.Lock
                    },
                    contentDescription = "VPN Shield Connect",
                    tint = glowColor,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (status) {
                        VpnConnectionStatus.CONNECTED -> "DISCONNECT"
                        VpnConnectionStatus.CONNECTING -> "CONNECTING"
                        VpnConnectionStatus.DISCONNECTING -> "STOPPING"
                        VpnConnectionStatus.DISCONNECTED -> "CONNECT"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ConnectionStatusText(
    status: VpnConnectionStatus,
    durationSeconds: Long
) {
    val statusText = when (status) {
        VpnConnectionStatus.CONNECTED -> "PROTECTED & ENCRYPTED"
        VpnConnectionStatus.CONNECTING -> "SECURING VPN TUNNEL..."
        VpnConnectionStatus.DISCONNECTING -> "DISCONNECTING..."
        VpnConnectionStatus.DISCONNECTED -> "UNPROTECTED"
    }

    val statusColor = when (status) {
        VpnConnectionStatus.CONNECTED -> NeonGreen
        VpnConnectionStatus.CONNECTING -> NeonCyan
        else -> TextMuted
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                letterSpacing = 1.sp
            )
        }

        if (status == VpnConnectionStatus.CONNECTED) {
            Spacer(modifier = Modifier.height(4.dp))
            val hours = durationSeconds / 3600
            val minutes = (durationSeconds % 3600) / 60
            val secs = durationSeconds % 60
            val timerString = String.format("%02d:%02d:%02d", hours, minutes, secs)

            Text(
                text = "Connected Duration: $timerString",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SelectedServerCard(
    server: VpnServer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("selected_server_card"),
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = server.flagEmoji,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "${server.country} (${server.city})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "IP: ${server.ipAddress}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${server.pingMs} ms",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Change Server Location",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun TrafficMeterCard(
    status: VpnConnectionStatus,
    downloadSpeedMbps: Float,
    uploadSpeedMbps: Float,
    totalDownloadedBytes: Long,
    totalUploadedBytes: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE TRAFFIC MONITOR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (status == VpnConnectionStatus.CONNECTED) NeonGreen.copy(alpha = 0.15f)
                            else TextMuted.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (status == VpnConnectionStatus.CONNECTED) "LIVE" else "IDLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (status == VpnConnectionStatus.CONNECTED) NeonGreen else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Download Speed
                SpeedStatBox(
                    title = "DOWNLOAD",
                    speedValue = String.format("%.2f Mbps", downloadSpeedMbps),
                    totalValue = formatDataBytes(totalDownloadedBytes),
                    icon = Icons.Filled.ArrowDownward,
                    tint = NeonCyan
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(50.dp)
                        .background(CyberCardBorder)
                )

                // Upload Speed
                SpeedStatBox(
                    title = "UPLOAD",
                    speedValue = String.format("%.2f Mbps", uploadSpeedMbps),
                    totalValue = formatDataBytes(totalUploadedBytes),
                    icon = Icons.Filled.ArrowUpward,
                    tint = NeonGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Real-Time Traffic Wave Canvas
            TrafficWaveCanvas(
                isActive = status == VpnConnectionStatus.CONNECTED,
                speedFactor = downloadSpeedMbps
            )
        }
    }
}

@Composable
private fun SpeedStatBox(
    title: String,
    speedValue: String,
    totalValue: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = speedValue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Total: $totalValue",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TrafficWaveCanvas(
    isActive: Boolean,
    speedFactor: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "trafficGraph")
    val phaseShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0D131F))
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f

        val path = Path()
        path.moveTo(0f, centerY)

        if (isActive) {
            val amplitude = (10f + speedFactor * 3f).coerceAtMost(height / 2f - 4f)
            val wavelength = width / 2.5f

            var x = 0f
            while (x <= width) {
                val radians = ((x + phaseShift * 2) / wavelength) * (2 * Math.PI)
                val y = centerY + Math.sin(radians).toFloat() * amplitude
                path.lineTo(x, y)
                x += 10f
            }
        } else {
            path.lineTo(width, centerY)
        }

        drawPath(
            path = path,
            color = if (isActive) NeonCyan else TextMuted.copy(alpha = 0.4f),
            style = Stroke(width = 2.5.dp.toPx())
        )
    }
}

@Composable
private fun SecurityBadgesCard(
    currentIp: String,
    isProtected: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "SECURITY STATUS & ENCRYPTION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecurityCheckRow(
                label = "Virtual IP Masking",
                detail = currentIp,
                isCheck = isProtected
            )

            Spacer(modifier = Modifier.height(8.dp))

            SecurityCheckRow(
                label = "Encryption Protocol",
                detail = if (isProtected) "ChaCha20 / AES-256-GCM" else "None (Exposed)",
                isCheck = isProtected
            )

            Spacer(modifier = Modifier.height(8.dp))

            SecurityCheckRow(
                label = "DNS Leak Shield",
                detail = if (isProtected) "Private Zero-Log DNS Active" else "Default ISP DNS",
                isCheck = isProtected
            )
        }
    }
}

@Composable
private fun SecurityCheckRow(
    label: String,
    detail: String,
    isCheck: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                tint = if (isCheck) NeonGreen else DisconnectRed,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }

        Text(
            text = detail,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isCheck) NeonCyan else DisconnectRed
        )
    }
}

private fun formatDataBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return String.format("%.1f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format("%.1f MB", mb)
    val gb = mb / 1024.0
    return String.format("%.2f GB", gb)
}
