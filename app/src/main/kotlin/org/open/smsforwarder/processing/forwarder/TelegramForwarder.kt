package org.open.smsforwarder.processing.forwarder

import org.open.smsforwarder.data.remote.service.TelegramService
import org.open.smsforwarder.domain.model.Forwarding
import javax.inject.Inject

class TelegramForwarder @Inject constructor(
    private val telegramService: TelegramService
) : Forwarder {

    override suspend fun execute(forwarding: Forwarding, message: String): Result<Unit> =
        runCatching {
            telegramService.sendMessage(
                apiToken = forwarding.telegramApiToken,
                chatId = forwarding.telegramChatId,
                text = message
            )
        }
}
