package com.deliciachef.ui.feature.onboarding.recovery

sealed class ForgotPasswordEvent {
    data class SubmitEmail(val email: String) : ForgotPasswordEvent()
}