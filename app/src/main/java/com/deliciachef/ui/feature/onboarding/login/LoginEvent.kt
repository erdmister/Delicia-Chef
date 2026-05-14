package com.deliciachef.ui.feature.onboarding.login

sealed class LoginEvent {
    data class SubmitLogin(val email: String, val pass: String) : LoginEvent()
    object NavigateToRegisterClicked : LoginEvent()
    object NavigateToRecoveryClicked : LoginEvent()
}