package com.deliciachef.ui.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.deliciachef.data.repository.AuthRepositoryImpl
import com.deliciachef.domain.repository.AuthRepository
import com.deliciachef.ui.feature.onboarding.login.LoginViewModel
import com.deliciachef.ui.feature.onboarding.recovery.ForgotPasswordViewModel
import com.deliciachef.ui.feature.onboarding.register.RegisterViewModel

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory : ViewModelProvider.Factory {

    // Instanciamos el repositorio una sola vez
    private val authRepository: AuthRepository = AuthRepositoryImpl()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java) -> {
                ForgotPasswordViewModel(authRepository) as T
            }
            else -> throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}