package com.example.raithavarta.di

import android.content.Context
import com.example.raithavarta.data.local.DatabaseProvider
import com.example.raithavarta.data.preferences.UserPreferencesRepository
import com.example.raithavarta.data.remote.MockAuthApiImpl
import com.example.raithavarta.data.repository.AuthRepository
import com.example.raithavarta.data.repository.DiseaseRepository

/**
 * Simple manual DI container (no Hilt) for ViewModels and composables.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val database = DatabaseProvider.get(appContext)
    val userPreferences = UserPreferencesRepository(appContext)
    val authRepository = AuthRepository(
        mockAuthApi = MockAuthApiImpl(),
        userDao = database.userDao(),
        sessionLogDao = database.sessionLogDao(),
        userPreferencesRepository = userPreferences
    )
    val diseaseRepository = DiseaseRepository()
}
