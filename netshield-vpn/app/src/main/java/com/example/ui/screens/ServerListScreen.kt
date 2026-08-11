package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VpnServer
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
fun ServerListScreen(
    viewModel: VpnViewModel,
    onServerSelected: (VpnServer) -> Unit
) {
    val servers by viewModel.filteredServers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()

    val categories = listOf("All", "Fastest", "Streaming", "Gaming", "P2P", "Favorites")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp)
    ) {
        // Title
        Text(
            text = "Global Server Locations",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Text(
            text = "Select an encrypted server node worldwide",
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search country, city, or IP...", color = TextMuted, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = TextSecondary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear search", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberCard,
                unfocusedContainerColor = CyberCard,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("server_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Horizontal Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSelectedCategory(category) },
                    label = { Text(category, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = CyberCard,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = CyberCardBorder,
                        selectedBorderColor = NeonCyan,
                        enabled = true,
                        selected = isSelected
                    ),
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Server List
        if (servers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No server nodes found", fontSize = 16.sp, color = TextPrimary)
                    Text("Try searching another location or category", fontSize = 12.sp, color = TextMuted)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Auto Optimal Smart Location Header Item
                item {
                    AutoOptimalServerCard(
                        isSelected = selectedServer == null,
                        onClick = {
                            val fastServer = servers.minByOrNull { it.pingMs } ?: servers.first()
                            viewModel.selectServer(fastServer)
                            onServerSelected(fastServer)
                        }
                    )
                }

                items(servers, key = { it.id }) { server ->
                    ServerListItem(
                        server = server,
                        isSelected = selectedServer?.id == server.id,
                        onClick = {
                            viewModel.selectServer(server)
                            onServerSelected(server)
                        },
                        onFavoriteToggle = {
                            viewModel.toggleFavorite(server)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AutoOptimalServerCard(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("auto_optimal_server_card"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NeonCyan.copy(alpha = 0.12f) else CyberCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) NeonCyan else CyberCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FlashOn,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Auto-Optimal Smart Location",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Automatically routes to lowest latency node",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ServerListItem(
    server: VpnServer,
    isSelected: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("server_item_${server.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CyberCard.copy(alpha = 0.9f) else CyberCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) NeonCyan else CyberCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = server.flagEmoji,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${server.country} - ${server.city}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (server.category != "All") {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonPurple.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = server.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPurple
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ping indicator dot
                        val pingColor = when {
                            server.pingMs < 60 -> NeonGreen
                            server.pingMs < 120 -> NeonCyan
                            else -> TextMuted
                        }

                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(pingColor)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "${server.pingMs} ms",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = pingColor
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Load ${server.loadPercent}%",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        LinearProgressIndicator(
                            progress = { server.loadPercent / 100f },
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (server.loadPercent > 70) TextMuted else NeonCyan,
                            trackColor = CyberCardBorder,
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (server.isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (server.isFavorite) Color(0xFFFFD700) else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isSelected) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        tint = NeonCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
