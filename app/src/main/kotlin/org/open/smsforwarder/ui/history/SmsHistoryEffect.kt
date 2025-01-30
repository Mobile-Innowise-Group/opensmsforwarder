package org.open.smsforwarder.ui.history

sealed interface SmsHistoryEffect {

    data class RetryEffect(val id: Long) : SmsHistoryEffect
}
