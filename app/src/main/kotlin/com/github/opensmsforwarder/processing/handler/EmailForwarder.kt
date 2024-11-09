package com.github.opensmsforwarder.processing.handler

import com.github.opensmsforwarder.data.remote.service.EmailService
import com.github.opensmsforwarder.model.Recipient
import com.github.opensmsforwarder.processing.composer.EmailComposer
import javax.inject.Inject

class EmailForwarder @Inject constructor(
    private val emailComposer: EmailComposer,
    private val emailService: EmailService
) : Forwarder {

    override suspend fun execute(recipient: Recipient, message: String): Result<Unit> =
        runCatching {
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
