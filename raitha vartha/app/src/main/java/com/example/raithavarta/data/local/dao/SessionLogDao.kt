package com.example.raithavarta.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.raithavarta.data.local.entity.SessionLogEntity

@Dao
interface SessionLogDao {
    @Insert
    suspend fun insert(log: SessionLogEntity): Long

    @Query(
        """
        SELECT * FROM session_logs 
        WHERE userEmail = :email AND logoutTimeEpochMillis IS NULL 
        ORDER BY loginTimeEpochMillis DESC LIMIT 1
        """
    )
    suspend fun getOpenSession(email: String): SessionLogEntity?

    @Query("UPDATE session_logs SET logoutTimeEpochMillis = :logoutMillis WHERE id = :id")
    suspend fun markLogout(id: Long, logoutMillis: Long)

    @Query(
        """
        SELECT * FROM session_logs 
        WHERE userEmail = :email 
        ORDER BY loginTimeEpochMillis DESC LIMIT 1
        """
    )
    suspend fun getLatestSession(email: String): SessionLogEntity?
}
