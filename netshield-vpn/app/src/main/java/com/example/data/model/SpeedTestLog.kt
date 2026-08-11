package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "speed_test_logs")
data class SpeedTestLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val downloadMbps: Float,
    val uploadMbps: Float,
    val pingMs: Int,
    val jitterMs: Int,
    val serverName: String
)
