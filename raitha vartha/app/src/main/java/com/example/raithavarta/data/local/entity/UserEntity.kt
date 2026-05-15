package com.example.raithavarta.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Offline user credentials stored in Room (demo / lab use — not for production secrets).
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val displayName: String,
    /** Demo field: real apps must use Argon2/bcrypt server-side only */
    val passwordPlain: String
)
