package com.example.raithavarta.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.raithavarta.di.AppContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val lastLoginText: String = "—",
    val loading: Boolean = false
)

class ProfileViewModel(private val container: AppContainer) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState(loading = true))
    val uiState: StateFlow<ProfileUiState> = _ui.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true)
            val email = container.userPreferences.userEmail.first() ?: ""
            val name = container.userPreferences.userName.first() ?: ""
            val session = if (email.isNotBlank()) {
                container.database.sessionLogDao().getLatestSession(email)
            } else {
                null
            }
            val fmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val last = session?.let { fmt.format(Date(it.loginTimeEpochMillis)) } ?: "—"
            _ui.value = ProfileUiState(
                name = name,
                email = email,
                lastLoginText = last,
                loading = false
            )
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            val email = container.userPreferences.userEmail.first()
            if (!email.isNullOrBlank()) {
                container.authRepository.logout(email)
            }
            onDone()
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(ProfileViewModel::class.java))
                    return ProfileViewModel(container) as T
                }
            }
    }
}
