package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ConnectionLog
import com.example.data.model.SpeedTestLog
import com.example.data.model.VpnServer
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnDao {

    // Servers
    @Query("SELECT * FROM vpn_servers ORDER BY isFavorite DESC, pingMs ASC")
    fun getAllServers(): Flow<List<VpnServer>>

    @Query("SELECT * FROM vpn_servers WHERE category = :category ORDER BY pingMs ASC")
    fun getServersByCategory(category: String): Flow<List<VpnServer>>

    @Query("SELECT * FROM vpn_servers WHERE isFavorite = 1 ORDER BY pingMs ASC")
    fun getFavoriteServers(): Flow<List<VpnServer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServers(servers: List<VpnServer>)

    @Update
    suspend fun updateServer(server: VpnServer)

    @Query("SELECT COUNT(*) FROM vpn_servers")
    suspend fun getServerCount(): Int

    // Connection Logs
    @Query("SELECT * FROM connection_logs ORDER BY timestamp DESC")
    fun getAllConnectionLogs(): Flow<List<ConnectionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnectionLog(log: ConnectionLog)

    @Query("DELETE FROM connection_logs")
    suspend fun clearConnectionLogs()

    // Speed Test Logs
    @Query("SELECT * FROM speed_test_logs ORDER BY timestamp DESC")
    fun getAllSpeedTestLogs(): Flow<List<SpeedTestLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeedTestLog(log: SpeedTestLog)
}
