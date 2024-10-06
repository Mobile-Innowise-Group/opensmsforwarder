package com.github.opensmsforwarder.processing.forwarding.handler

import com.github.opensmsforwarder.data.remote.service.EmailService
import com.github.opensmsforwarder.model.Recipient
import com.github.opensmsforwarder.processing.composer.EmailComposer
import javax.inject.Inject

class EmailForwardingHandler @Inject constructor(
    private val emailComposer: EmailComposer,
    private val emailService: EmailService
) : ForwardingHandler {

    override suspend fun run(recipient: Recipient, message: String): Result<Unit> =
        runCatching {
            forwardSmsViaEmail(recipient, message)
        }

    private suspend fun forwardSmsViaEmail(recipient: Recipient, message: String) {
        recipient.senderEmail ?: return

        val emailMessage = emailComposer.composeMessage(
            toEmailAddress = recipient.recipientEmail,
            subject = DEFAULT_SUBJECT,
            messageBody = message
        )
        emailService.sendEmail(
            id = recipient.id,
            rawBody = hashMapOf(SEND_FORMAT to emailMessage)
        )
    }

    private companion object {
        const val DEFAULT_SUBJECT = "Forwarded SMS"
        const val SEND_FORMAT = "raw"
    }
}