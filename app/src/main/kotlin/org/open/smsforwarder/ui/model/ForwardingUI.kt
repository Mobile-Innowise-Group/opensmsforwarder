package org.open.smsforwarder.ui.model

import org.open.smsforwarder.domain.model.ForwardingType

data class ForwardingUI(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val senderEmail: String? = null,
    val recipientEmail: String = "",
    val telegramApiToken: String = "",
    val telegramChatId: String = "",
    val error: String = "",
    val atLeastOneRuleAdded: Boolean = true
) {

    fun isEmailBlockCompleted() =
        forwardingType == ForwardingType.EMAIL && recipientEmail.isNotEmpty() && !senderEmail.isNullOrEmpty()

    val allStepsCompleted: Boolean
        get() = (isEmailBlockCompleted()
                || isTelegramBlockCompleted())
                && atLeastOneRuleAdded

    private fun isTelegramBlockCompleted(): Boolean =
        forwardingType == ForwardingType.TELEGRAM
                && telegramApiToken.isNotBlank() && telegramChatId.isNotBlank()
}
