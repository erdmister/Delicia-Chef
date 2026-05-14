package com.deliciachef.ui.feature.onboarding.register

sealed class RegisterEvent {
    data class SubmitRegister(val name: String, val email: String, val pass: String) : RegisterEvent()
}