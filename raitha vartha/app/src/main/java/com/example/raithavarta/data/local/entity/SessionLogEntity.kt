package com.example.raithavarta.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local session audit: login and logout timestamps per user.
 */
@Entity(tableName = "session_logs")
data class SessionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val loginTimeEpochMillis: Long,
    val logoutTimeEpochMillis: Long? = null
)
