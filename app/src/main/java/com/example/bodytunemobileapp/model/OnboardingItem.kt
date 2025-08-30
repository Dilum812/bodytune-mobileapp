package com.example.bodytunemobileapp.model

enum class ScreenType {
    ONBOARDING,
    SIGN_IN,
    SIGN_UP,
    PROFILE_SETUP
}

data class OnboardingItem(
    val image: Int,
    val logo: Int? = null,
    val title: String,
    val description: String,
    val screenType: ScreenType = ScreenType.ONBOARDING
)
