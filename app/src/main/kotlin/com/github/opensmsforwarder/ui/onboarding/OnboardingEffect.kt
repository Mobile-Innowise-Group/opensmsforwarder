package com.github.opensmsforwarder.ui.onboarding

sealed interface OnboardingEffect

data object WarningEffect : OnboardingEffect

data object NextPageEffect: OnboardingEffect

data object PreviousPageEffect: OnboardingEffect

data object SkipAllEffect: OnboardingEffect
