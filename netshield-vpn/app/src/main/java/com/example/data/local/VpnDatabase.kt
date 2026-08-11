package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ConnectionLog
import com.example.data.model.SpeedTestLog
import com.example.data.model.VpnServer

@Database(
    entities = [VpnServer::class, ConnectionLog::class, SpeedTestLog::class],
    version = 1,
    exportSchema = false
)
abstract class VpnDatabase : RoomDatabase() {

    abstract fun vpnDao(): VpnDao

    companion object {
        @Volatile
        private var INSTANCE: VpnDatabase? = null

        fun getDatabase(context: Context): VpnDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VpnDatabase::class.java,
                    "netshield_vpn_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
