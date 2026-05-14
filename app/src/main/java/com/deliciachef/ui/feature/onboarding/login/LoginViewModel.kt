package com.deliciachef.ui.feature.onboarding.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliciachef.core.common.Resource
import com.deliciachef.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ¡Ahora inyectamos el repositorio en el constructor!
class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SubmitLogin -> performLogin(event.email, event.pass)
            is LoginEvent.NavigateToRegisterClicked -> { /* Solo navegación */ }
            is LoginEvent.NavigateToRecoveryClicked -> { /* Solo navegación */ }
        }
    }

    private fun performLogin(email: String, pass: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // Llamamos a nuestro repositorio limpio
            when (val result = repository.login(email, pass)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Resource.Loading -> {
                    // Opcional, ya lo manejamos arriba
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}