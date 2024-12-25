package com.github.opensmsforwarder.processing.forwarder

import com.github.opensmsforwarder.domain.model.Forwarding

interface Forwarder {

    suspend fun execute(forwarding: Forwarding, message: String): Result<Unit>
}
