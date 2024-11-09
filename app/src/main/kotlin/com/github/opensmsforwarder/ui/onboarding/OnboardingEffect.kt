package com.github.opensmsforwarder.ui.onboarding

sealed interface OnboardingEffect

data object WarningEffect : OnboardingEffect

data object NextEffect: OnboardingEffect

data object PreviousEffect: OnboardingEffect

data object SkipAllEffect: OnboardingEffect
