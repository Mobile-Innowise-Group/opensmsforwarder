package com.github.opensmsforwarder.ui.onboarding


sealed interface OnboardingEffect

data object WarningEffect : OnboardingEffect
