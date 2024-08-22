package com.github.opensmsforwarder.data

import com.github.opensmsforwarder.data.local.prefs.Prefs
import javax.inject.Inject

class LocalSettingsRepository @Inject constructor(
    private val prefs: Prefs
) {
    fun getOnboardingCompleteFlag() = prefs.isOnboardingCompleted

    fun setOnboardingCompleteFlag(isOnboardingCompleted: Boolean) {
        prefs.isOnboardingCompleted = isOnboardingCompleted
    }
}
