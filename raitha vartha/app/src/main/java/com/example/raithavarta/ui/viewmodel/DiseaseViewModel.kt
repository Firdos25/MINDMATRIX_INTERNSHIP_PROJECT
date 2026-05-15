package com.example.raithavarta.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.raithavarta.di.AppContainer
import com.example.raithavarta.domain.model.DiseaseInsight
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DiseaseUiState(
    val plantName: String = "",
    val imageUri: Uri? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val result: DiseaseInsight? = null
)

class DiseaseViewModel(private val container: AppContainer) : ViewModel() {

    private val _ui = MutableStateFlow(DiseaseUiState())
    val uiState: StateFlow<DiseaseUiState> = _ui.asStateFlow()

    fun setPlantName(value: String) {
        _ui.value = _ui.value.copy(plantName = value, error = null)
    }

    fun setImage(uri: Uri?) {
        _ui.value = _ui.value.copy(imageUri = uri, error = null)
    }

    fun clearResult() {
        _ui.value = _ui.value.copy(result = null, error = null)
    }

    /** Simulates AI processing delay then fills multilingual mock result */
    fun submitAnalysis(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val plant = _ui.value.plantName.trim()
            if (plant.isBlank()) {
                _ui.value = _ui.value.copy(error = "Enter plant name (e.g., Coconut, Tomato)")
                return@launch
            }
            if (_ui.value.imageUri == null) {
                _ui.value = _ui.value.copy(error = "Please attach a leaf image from gallery or camera")
                return@launch
            }
            _ui.value = _ui.value.copy(loading = true, error = null, result = null)
            delay(900)
            val insight = container.diseaseRepository.analyze(plant)
            _ui.value = _ui.value.copy(loading = false, result = insight)
            onSuccess()
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(DiseaseViewModel::class.java))
                    return DiseaseViewModel(container) as T
                }
            }
    }
}
