package com.example.raithavarta.data.repository

import com.example.raithavarta.data.local.dao.SessionLogDao
import com.example.raithavarta.data.local.dao.UserDao
import com.example.raithavarta.data.local.entity.SessionLogEntity
import com.example.raithavarta.data.local.entity.UserEntity
import com.example.raithavarta.data.preferences.UserPreferencesRepository
import com.example.raithavarta.data.remote.MockAuthApi

/**
 * MVVM repository: tries online mock API first, then Room offline credentials.
 */
class AuthRepository(
    private val mockAuthApi: MockAuthApi,
    private val userDao: UserDao,
    private val sessionLogDao: SessionLogDao,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    suspend fun registerOffline(name: String, email: String, password: String) {
        userDao.upsert(
            UserEntity(
                email = email.lowercase(),
                displayName = name,
                passwordPlain = password
            )
        )
    }

    suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean
    ): Result<Unit> {
        val normalized = email.trim().lowercase()
        val pwd = password.trim()

        val online = mockAuthApi.login(normalized, pwd)
        if (online.isSuccess) {
            val user = online.getOrThrow()
            onLoginSuccess(user.email, user.displayName, rememberMe)
            return Result.success(Unit)
        }

        val local = userDao.getByEmail(normalized)
        if (local != null && local.passwordPlain == pwd) {
            onLoginSuccess(local.email, local.displayName, rememberMe)
            return Result.success(Unit)
        }

        return Result.failure(
            online.exceptionOrNull() ?: IllegalArgumentException("Wrong email or password")
        )
    }

    private suspend fun onLoginSuccess(email: String, name: String, rememberMe: Boolean) {
        userPreferencesRepository.saveSession(email = email, name = name, rememberMe = rememberMe)
        sessionLogDao.insert(
            SessionLogEntity(
                userEmail = email,
                loginTimeEpochMillis = System.currentTimeMillis(),
                logoutTimeEpochMillis = null
            )
        )
    }

    suspend fun logout(email: String) {
        val open = sessionLogDao.getOpenSession(email)
        if (open != null) {
            sessionLogDao.markLogout(open.id, System.currentTimeMillis())
        }
        userPreferencesRepository.clearSession()
    }
}
