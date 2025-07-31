package org.open.smsforwarder.ui.steps.addrecipientdetails.addtelegramdetails

import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.utils.Resources

data class AddTelegramDetailsState(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val telegramApiToken: String = "",
    val telegramChatId: String = "",
    val inputErrorApiToken: Resources.StringProvider? = null,
    val inputErrorChatId: Resources.StringProvider? = null,
) {

    val nextButtonEnabled = inputErrorApiToken == null
            && inputErrorChatId == null
            && telegramApiToken.isNotBlank()
            && telegramChatId.isNotBlank()
}
