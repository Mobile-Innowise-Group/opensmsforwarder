package org.open.smsforwarder.domain.model

data class Forwarding(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val senderEmail: String? = null,
    val recipientEmail: String = "",
    val telegramApiToken: String = "",
    val telegramChatId: String = "",
    val error: String = "",
) {

    val isEmailForwardingType: Boolean get() = forwardingType != null && forwardingType == ForwardingType.EMAIL

    val isTelegramForwardingType: Boolean get() = forwardingType != null && forwardingType == ForwardingType.TELEGRAM
}
