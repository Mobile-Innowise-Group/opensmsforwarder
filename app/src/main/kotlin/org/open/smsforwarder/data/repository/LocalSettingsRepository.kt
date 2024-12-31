package org.open.smsforwarder.data.repository

import org.open.smsforwarder.data.local.prefs.Prefs
import javax.inject.Inject

class LocalSettingsRepository @Inject constructor(
    private val prefs: Prefs
) {

    fun isOnboardingCompleted() = prefs.isOnboardingCompleted

    fun setOnboardingCompleted() {
        prefs.isOnboardingCompleted = true
    }
}
