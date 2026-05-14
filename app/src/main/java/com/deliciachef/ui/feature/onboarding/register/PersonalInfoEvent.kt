package com.deliciachef.ui.feature.onboarding.register

sealed class PersonalInfoEvent {
    data class SubmitInfo(
        val firstName: String,
        val lastName: String,
        val username: String,
        val phone: String,
        val birthdate: String
    ) : PersonalInfoEvent()
}