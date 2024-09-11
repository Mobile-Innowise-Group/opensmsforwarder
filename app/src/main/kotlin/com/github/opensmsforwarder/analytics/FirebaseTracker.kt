package com.github.opensmsforwarder.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsTracker {

    override fun trackEvent(event: String) {
        val params = Bundle()
        logEventWithTimestamp(event, params)
    }

    override fun trackEvent(event: String, params: Bundle) {
        logEventWithTimestamp(event, params)
    }

    override fun trackEvent(event: String, paramKey: String, paramValue: String) {
        val params = Bundle().apply {
            putString(paramKey, paramValue)
        }
        logEventWithTimestamp(event, params)
    }

    private fun logEventWithTimestamp(event: String, params: Bundle) {
        params.putLong(TIMESTAMP_KEY, System.currentTimeMillis())
        firebaseAnalytics.logEvent(event, params)
    }

    private companion object {
        const val TIMESTAMP_KEY = "timestamp"
    }
}

