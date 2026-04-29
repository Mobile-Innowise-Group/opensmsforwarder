package org.open.smsforwarder.ui.steps.addrecipientdetails.addgooglechatdetails

import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.utils.Resources

data class AddGoogleChatDetailsState(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val googleChatWebHook: String = "",
    val inputErrorGoogleChat: Resources.StringProvider? = null,
) {
    val nextButtonEnabled = inputErrorGoogleChat == null && googleChatWebHook.isNotBlank()
}
