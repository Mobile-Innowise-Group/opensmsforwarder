package org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails

import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.utils.Resources

data class AddEmailDetailsState(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val signInTvVisible: Boolean = false,
    val senderEmailVisible: Boolean = false,
    val senderEmail: String? = null,
    val sigInBtnVisible: Boolean = false,
    val signOutBtnVisible: Boolean = false,
    val recipientEmail: String = "",
    val inputError: Resources.StringProvider? = null
) {
    val nextButtonEnabled =
        inputError == null && !senderEmail.isNullOrEmpty() && recipientEmail.isNotBlank()
}
