package com.deliciachef.ui.feature.onboarding.register

data class PersonalInfoState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)