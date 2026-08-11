package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpeedTestLog
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VpnViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedTestScreen(
    viewModel: VpnViewModel
) {
    val speedTestState by viewModel.speedTestState.collectAsState()
    val speedLogs by viewModel.speedTestLogs.collectAsState()

    val animatedProgress by animateFloatAsState(
        targetValue = speedTestState.progress,
        animationSpec = tween(durationMillis = 300),
        label = "gaugeProgress"
    )

    val displayedValue = when {
        speedTestState.phase.contains("Download", ignoreCase = true) -> speedTestState.downloadMbps
        speedTestState.phase.contains("Upload", ignoreCase = true) -> speedTestState.uploadMbps
        else -> speedTestState.downloadMbps
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Header
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "VPN Network Speed Test",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Measure real-time bandwidth latency & throughput",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gauge Display Canvas
            SpeedometerGauge(
                progress = animatedProgress,
                displayMbps = displayedValue,
                phaseText = speedTestState.phase
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Start Test Button
            Button(
                onClick = { viewModel.runSpeedTest() },
                enabled = !speedTestState.isRunning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black,
                    disabledContainerColor = CyberCard
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("run_speed_test_button")
            ) {
                Icon(
                    imageVector = if (speedTestState.isRunning) Icons.Filled.Speed else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (speedTestState.isRunning) "TESTING IN PROGRESS..." else "START SPEED TEST",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Realtime Metrics Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "PING",
                    value = "${speedTestState.pingMs} ms",
                    subtitle = "Jitter: ${speedTestState.jitterMs} ms",
                    tint = NeonGreen,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "DOWNLOAD",
                    value = String.format("%.1f Mbps", speedTestState.downloadMbps),
                    subtitle = "Peak Speed",
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "UPLOAD",
                    value = String.format("%.1f Mbps", speedTestState.uploadMbps),
                    subtitle = "Peak Speed",
                    tint = NeonBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Speed Test History Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SPEED TEST HISTORY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${speedLogs.size} Records",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (speedLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No speed test records yet. Tap Start Speed Test above.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(speedLogs) { log ->
                SpeedHistoryLogCard(log = log)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SpeedometerGauge(
    progress: Float,
    displayMbps: Float,
    phaseText: String
) {
    Box(
        modifier = Modifier
            .size(240.dp)
            .testTag("speedometer_gauge"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)
            val radius = (width / 2f) - 24.dp.toPx()

            val startAngle = 135f
            val sweepAngle = 270f

            // Background Arc Track
            drawArc(
                color = CyberCardBorder,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )

            // Active Progress Arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(NeonCyan, NeonBlue, NeonGreen, NeonCyan)
                ),
                startAngle = startAngle,
                sweepAngle = sweepAngle * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            )

            // Pointer Needle
            val needleAngle = Math.toRadians((startAngle + sweepAngle * progress).toDouble())
            val needleLength = radius - 20.dp.toPx()
            val needleEnd = Offset(
                x = (center.x + needleLength * cos(needleAngle)).toFloat(),
                y = (center.y + needleLength * sin(needleAngle)).toFloat()
            )

            drawLine(
                color = NeonCyan,
                start = center,
                end = needleEnd,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(
                color = NeonCyan,
                radius = 6.dp.toPx(),
                center = center
            )
        }

        // Center Value Text Overlay
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%.1f", displayMbps),
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Mbps",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = phaseText,
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CyberCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = tint
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SpeedHistoryLogCard(log: SpeedTestLog) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(log.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = log.serverName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ArrowDownward, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${String.format("%.1f", log.downloadMbps)} Mbps",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ArrowUpward, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${String.format("%.1f", log.uploadMbps)} Mbps",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(NeonGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${log.pingMs} ms",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                }
            }
        }
    }
}
