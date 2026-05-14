package com.deliciachef.ui.feature.onboarding.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonalInfoViewModel : ViewModel() {

    private val _state = MutableStateFlow(PersonalInfoState())
    val state: StateFlow<PersonalInfoState> = _state.asStateFlow()

    fun onEvent(event: PersonalInfoEvent) {
        when (event) {
            is PersonalInfoEvent.SubmitInfo -> savePersonalInfo(event)
        }
    }

    private fun savePersonalInfo(data: PersonalInfoEvent.SubmitInfo) {
        // Validaciones básicas
        if (data.firstName.isEmpty() || data.username.isEmpty()) {
            _state.update { it.copy(errorMessage = "El nombre y usuario son obligatorios") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        // Aquí conectarías con Firebase Firestore (ej. db.collection("users").document(userId).set(data))
        // Simulamos una llamada de red de 1.5 segundos
        viewModelScope.launch {
            try {
                delay(1500) // Simulación de carga
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al guardar el perfil") }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}