package com.github.opensmsforwarder.data.local.prefs

import android.content.SharedPreferences
import javax.inject.Inject

class Prefs @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    var currentRecipientId: Long
        get() = sharedPreferences.getLong(CURRENT_RECIPIENT_ID, -1)
        set(value) =
            sharedPreferences
                .edit()
                .putLong(CURRENT_RECIPIENT_ID, value)
                .apply()

    private companion object {
        const val CURRENT_RECIPIENT_ID = "CURRENT_RECIPIENT_ID"
    }
}
