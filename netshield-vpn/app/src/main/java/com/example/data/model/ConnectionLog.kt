package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connection_logs")
data class ConnectionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverName: String,
    val country: String,
    val flagEmoji: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Long,
    val bytesDownloaded: Long,
    val bytesUploaded: Long,
    val protocol: String
)
