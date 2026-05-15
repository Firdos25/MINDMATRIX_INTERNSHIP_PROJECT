package com.example.raithavarta.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "raitha_user_prefs")

/**
 * User-facing session flags using DataStore (remember me, logged-in snapshot).
 */
class UserPreferencesRepository(context: Context) {

    private val ds = context.applicationContext.dataStore

    private object Keys {
        val LOGGED_IN = booleanPreferencesKey("logged_in")
        val EMAIL = stringPreferencesKey("email")
        val NAME = stringPreferencesKey("name")
        val REMEMBER_ME = booleanPreferencesKey("remember_me")
    }

    val isLoggedIn: Flow<Boolean> = ds.data.map { it[Keys.LOGGED_IN] == true }
    val userEmail: Flow<String?> = ds.data.map { it[Keys.EMAIL] }
    val userName: Flow<String?> = ds.data.map { it[Keys.NAME] }
    val rememberMe: Flow<Boolean> = ds.data.map { it[Keys.REMEMBER_ME] == true }

    suspend fun saveSession(email: String, name: String, rememberMe: Boolean) {
        ds.edit { prefs ->
            prefs[Keys.LOGGED_IN] = true
            prefs[Keys.EMAIL] = email
            prefs[Keys.NAME] = name
            prefs[Keys.REMEMBER_ME] = rememberMe
        }
    }

    suspend fun clearSession() {
        ds.edit { prefs ->
            prefs.remove(Keys.LOGGED_IN)
            prefs.remove(Keys.EMAIL)
            prefs.remove(Keys.NAME)
            prefs.remove(Keys.REMEMBER_ME)
        }
    }
}
