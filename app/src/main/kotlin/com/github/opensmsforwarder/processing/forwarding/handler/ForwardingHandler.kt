package com.github.opensmsforwarder.processing.forwarding.handler

import com.github.opensmsforwarder.model.Recipient

interface ForwardingHandler {

    suspend fun run(recipient: Recipient, message: String): Result<Unit>
}
