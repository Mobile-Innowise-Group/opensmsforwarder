package org.open.smsforwarder.ui.model

data class HistoryUI (
    val id: Long = 0,
    val date: Long? = null,
    val message: String? = "",
    val isForwardingSuccessful: Boolean = true
)
