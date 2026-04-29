package org.open.smsforwarder.processing.forwarder

import org.open.smsforwarder.data.remote.dto.ChatMessage
import org.open.smsforwarder.data.remote.service.GoogleChatService
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.utils.runSuspendCatching
import javax.inject.Inject

class GoogleChatForwarder @Inject constructor(
    private val googleChatService: GoogleChatService
) : Forwarder {

    override suspend fun execute(forwarding: Forwarding, message: String): Result<Unit> =
        runSuspendCatching {
            googleChatService.sendMessage(
                webHookUrl = forwarding.googleChatWebHook,
                message = ChatMessage(message)
            )
        }
}
