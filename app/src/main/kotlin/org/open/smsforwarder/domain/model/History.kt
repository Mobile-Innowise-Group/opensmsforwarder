package org.open.smsforwarder.domain.model

data class History(
    val id: Long = 0,
    val date: Long? = 0,
    val forwardingId: Long = 0,
    val message: String = "",
    val isForwardingSuccessful: Boolean = false
)
