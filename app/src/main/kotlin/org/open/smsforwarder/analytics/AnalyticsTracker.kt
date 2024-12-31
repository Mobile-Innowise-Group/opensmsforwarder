package org.open.smsforwarder.analytics

import android.os.Bundle

interface AnalyticsTracker {
    fun trackEvent(event: String)
    fun trackEvent(event: String, params: Bundle)
    fun trackEvent(event: String, paramKey: String, paramValue: String)
}
