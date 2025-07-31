package org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails

import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.utils.Resources

data class AddEmailDetailsState(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val senderEmail: String? = null,
    val recipientEmail: String = "",
    val inputErrorProvider: Resources.StringProvider? = null
) {
    val nextButtonEnabled =
        (inputErrorProvider == null) && !senderEmail.isNullOrEmpty() && recipientEmail.isNotBlank()
}
