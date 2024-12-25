package com.github.opensmsforwarder.domain.model

data class Rule(
    val id: Long = 0,
    val forwardingId: Long,
    val textRule: String,
)
