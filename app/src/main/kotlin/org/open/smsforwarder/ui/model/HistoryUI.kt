package org.open.smsforwarder.ui.model

data class HistoryUI (
    val id: Long = 0,
    val date: Long? = 0L,
    val message: String? = "",
    val isForwardingSuccessful: Boolean? = true
)
