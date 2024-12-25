package com.github.opensmsforwarder.processing.forwarder

import com.github.opensmsforwarder.data.remote.service.EmailService
import com.github.opensmsforwarder.domain.model.Forwarding
import com.github.opensmsforwarder.processing.composer.EmailComposer
import javax.inject.Inject

class EmailForwarder @Inject constructor(
    private val emailComposer: EmailComposer,
    private val emailService: EmailService
) : Forwarder {

    override suspend fun execute(forwarding: Forwarding, message: String): Result<Unit> =
        runCatching {
            val emailMessage = emailComposer.composeMessage(
                toEmailAddress = forwarding.recipientEmail,
                subject = DEFAULT_SUBJECT,
                messageBody = message
            )
            emailService.sendEmail(
                id = forwarding.id,
                rawBody = hashMapOf(SEND_FORMAT to emailMessage)
            )
        }

    private companion object {
        const val DEFAULT_SUBJECT = "Forwarded SMS"
        const val SEND_FORMAT = "raw"
    }
}
