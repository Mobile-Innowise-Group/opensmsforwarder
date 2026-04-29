package org.open.smsforwarder.processing.model

data class IncomingSms(
    val sender: String? = null,
    val message: String,
)
