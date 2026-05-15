package com.example.raithavarta.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.raithavarta.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    /** One-shot: true after successful login until consumed */
    val authenticated: Boolean = false
)

class AuthViewModel(private val container: AppContainer) : ViewModel() {

    private val _ui = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _ui.asStateFlow()

    fun login(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _ui.value = AuthUiState(
                    loading = false,
                    errorMessage = "Email and password are required",
                    authenticated = false
                )
                return@launch
            }
            _ui.value = AuthUiState(loading = true, errorMessage = null, authenticated = false)
            val result = container.authRepository.login(
                email = email,
                password = password,
                rememberMe = rememberMe
            )
            _ui.value = if (result.isSuccess) {
                AuthUiState(loading = false, errorMessage = null, authenticated = true)
            } else {
                AuthUiState(
                    loading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Login failed",
                    authenticated = false
                )
            }
        }
    }

    fun consumeError() {
        _ui.value = _ui.value.copy(errorMessage = null)
    }

    fun consumeAuthenticated() {
        _ui.value = _ui.value.copy(authenticated = false)
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(AuthViewModel::class.java))
                    return AuthViewModel(container) as T
                }
            }
    }
}
