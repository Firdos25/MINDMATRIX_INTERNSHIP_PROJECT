package com.example.raithavarta.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.raithavarta.data.local.dao.SessionLogDao
import com.example.raithavarta.data.local.dao.UserDao
import com.example.raithavarta.data.local.entity.SessionLogEntity
import com.example.raithavarta.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, SessionLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RaithavartaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionLogDao(): SessionLogDao
}
