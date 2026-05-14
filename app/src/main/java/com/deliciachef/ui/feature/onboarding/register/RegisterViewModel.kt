package com.deliciachef.ui.feature.onboarding.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliciachef.core.common.Resource
import com.deliciachef.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Inyectamos el repositorio en el constructor
class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.SubmitRegister -> performRegistration(event.name, event.email, event.pass)
        }
    }

    private fun performRegistration(name: String, email: String, pass: String) {
        // Validaciones de negocio
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            _state.update { it.copy(errorMessage = "Completa todos los campos") }
            return
        }

        if (pass.length < 6) {
            _state.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        // Llamada limpia al Repositorio usando Corrutinas
        viewModelScope.launch {
            when (val result = repository.register(email, pass)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Resource.Loading -> { } // Opcional
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}