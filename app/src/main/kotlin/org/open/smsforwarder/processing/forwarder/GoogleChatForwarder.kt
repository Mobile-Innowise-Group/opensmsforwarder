package org.open.smsforwarder.processing.forwarder

import org.open.smsforwarder.data.remote.dto.ChatMessage
import org.open.smsforwarder.data.remote.service.GoogleChatService
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.utils.runSuspendCatching
import javax.inject.Inject

class GoogleChatForwarder @Inject constructor(
    private val googleChatService: GoogleChatService,
    private val forwardingErrorMapper: ForwardingErrorMapper,
) : Forwarder {

    override suspend fun execute(forwarding: Forwarding, message: String): ForwardingResult =
        runSuspendCatching {
            googleChatService.sendMessage(
                webHookUrl = forwarding.googleChatWebHook,
                message = ChatMessage(message)
            )
        }
            .fold(
                onSuccess = { ForwardingResult.Success },
                onFailure = { forwardingErrorMapper.map(it) }
            )
}
