package org.open.smsforwarder.processing.forwarder

import org.open.smsforwarder.data.remote.service.EmailService
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.processing.composer.EmailComposer
import org.open.smsforwarder.utils.runSuspendCatching
import javax.inject.Inject

class EmailForwarder @Inject constructor(
    private val emailComposer: EmailComposer,
    private val emailService: EmailService,
    private val errorMapper: ErrorMapper,
) : Forwarder {

    override suspend fun execute(forwarding: Forwarding, message: String): ForwardingResult =
        runSuspendCatching {
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
            .fold(
                onSuccess = { ForwardingResult.Success },
                onFailure = { errorMapper.map(it) }
            )

    private companion object {
        const val DEFAULT_SUBJECT = "Forwarded SMS"
        const val SEND_FORMAT = "raw"
    }
}
