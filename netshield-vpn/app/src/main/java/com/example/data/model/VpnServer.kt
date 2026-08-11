package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_servers")
data class VpnServer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val country: String,
    val city: String,
    val countryCode: String,
    val flagEmoji: String,
    val ipAddress: String,
    val pingMs: Int,
    val loadPercent: Int,
    val isFavorite: Boolean = false,
    val category: String = "All", // "Fastest", "Streaming", "Gaming", "P2P", "All"
    val protocol: String = "WireGuard",
    val rating: Float = 4.8f,
    val isPremium: Boolean = false
)
