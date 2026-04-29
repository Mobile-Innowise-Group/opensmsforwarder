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
    val googleChatWebHook: String = "",
    val error: String = "",
    val atLeastOneRuleAdded: Boolean = true
) {

    fun isEmailBlockCompleted() =
        forwardingType == ForwardingType.EMAIL && recipientEmail.isNotEmpty() && !senderEmail.isNullOrEmpty()

    private fun isTelegramBlockCompleted(): Boolean =
        forwardingType == ForwardingType.TELEGRAM
                && telegramApiToken.isNotBlank() && telegramChatId.isNotBlank()

    private fun isGoogleChatWebHookBlockCompleted(): Boolean =
        forwardingType == ForwardingType.GOOGLE_CHAT && googleChatWebHook.isNotBlank()

    val allStepsCompleted: Boolean
        get() = (isEmailBlockCompleted()
                || isTelegramBlockCompleted()
                || isGoogleChatWebHookBlockCompleted())
                && atLeastOneRuleAdded
}
