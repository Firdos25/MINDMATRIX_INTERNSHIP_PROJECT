package com.example.raithavarta.data.local

import android.content.Context
import androidx.room.Room

/**
 * Single Room database instance for the app process.
 */
object DatabaseProvider {
    @Volatile
    private var instance: RaithavartaDatabase? = null

    fun get(context: Context): RaithavartaDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                RaithavartaDatabase::class.java,
                "raitha_compose.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
        }
    }
}
