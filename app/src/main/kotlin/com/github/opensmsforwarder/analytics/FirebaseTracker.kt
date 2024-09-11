package com.github.opensmsforwarder.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsTracker {

    override fun trackEvent(event: String) {
        firebaseAnalytics.logEvent(event, null)
    }

    override fun trackEvent(event: String, params: Bundle) {
        firebaseAnalytics.logEvent(event, params)
    }

    override fun trackEvent(event: String, paramKey: String, paramValue: String) {
        val params = Bundle().apply {
            putString(paramKey, paramValue)
        }
        firebaseAnalytics.logEvent(event, params)
    }
}
