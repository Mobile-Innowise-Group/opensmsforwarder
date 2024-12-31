package org.open.smsforwarder.processing.forwarder

import org.open.smsforwarder.domain.model.Forwarding

interface Forwarder {

    suspend fun execute(forwarding: Forwarding, message: String): Result<Unit>
}
