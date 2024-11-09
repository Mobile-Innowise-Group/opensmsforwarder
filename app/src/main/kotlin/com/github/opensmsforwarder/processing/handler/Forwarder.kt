package com.github.opensmsforwarder.processing.handler

import com.github.opensmsforwarder.model.Recipient

interface Forwarder {

    suspend fun execute(recipient: Recipient, message: String): Result<Unit>
}
