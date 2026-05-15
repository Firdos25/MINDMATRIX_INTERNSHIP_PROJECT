package com.example.raithavarta.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.raithavarta.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel(private val container: AppContainer) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _ui.asStateFlow()

    fun reset() {
        _ui.value = RegisterUiState()
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _ui.value = RegisterUiState(loading = true)
            try {
                if (name.isBlank() || email.isBlank() || password.length < 4) {
                    throw IllegalArgumentException("Fill all fields; password min 4 characters")
                }
                if (!email.contains("@")) {
                    throw IllegalArgumentException("Enter a valid email")
                }
                container.authRepository.registerOffline(
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    password = password
                )
                _ui.value = RegisterUiState(loading = false, success = true)
            } catch (e: Exception) {
                _ui.value = RegisterUiState(loading = false, errorMessage = e.message)
            }
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(RegisterViewModel::class.java))
                    return RegisterViewModel(container) as T
                }
            }
    }
}
