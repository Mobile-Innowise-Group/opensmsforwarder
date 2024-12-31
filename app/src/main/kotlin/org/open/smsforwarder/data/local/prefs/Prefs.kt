package org.open.smsforwarder.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject

class Prefs @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    var isOnboardingCompleted: Boolean
        get() = sharedPreferences.getBoolean(ONBOARDING_COMPLETED, false)
        set(value) =
            sharedPreferences
                .edit()
                .putBoolean(ONBOARDING_COMPLETED, value)
                .apply()

    private companion object {
        const val ONBOARDING_COMPLETED = "ONBOARDING_COMPLETED"
    }
}
