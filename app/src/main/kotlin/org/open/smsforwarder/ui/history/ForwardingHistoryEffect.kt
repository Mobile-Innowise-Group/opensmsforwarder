package org.open.smsforwarder.ui.history

sealed interface ForwardingHistoryEffect {

    data class RetryEffect(val id: Long) : ForwardingHistoryEffect
}
