package com.github.opensmsforwarder.data

import com.github.opensmsforwarder.data.local.prefs.Prefs
import javax.inject.Inject

class LocalSettingsRepository @Inject constructor(
    private val prefs: Prefs
) {
    fun getOnboardingCompleteFlag() = prefs.onboardingCompleted

    fun setOnboardingCompleteFlag(onboardingCompleteFlag: Boolean) {
        prefs.onboardingCompleted = onboardingCompleteFlag
    }
}
