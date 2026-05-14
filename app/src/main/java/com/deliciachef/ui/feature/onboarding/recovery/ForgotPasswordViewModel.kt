package com.deliciachef.ui.feature.onboarding.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliciachef.core.common.Resource
import com.deliciachef.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Inyectamos el repositorio
class ForgotPasswordViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.SubmitEmail -> sendRecoveryEmail(event.email)
        }
    }

    private fun sendRecoveryEmail(email: String) {
        if (email.isEmpty()) {
            _state.update { it.copy(errorMessage = "Por favor, ingresa tu correo electrónico") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repository.sendPasswordReset(email)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Resource.Loading -> { }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}