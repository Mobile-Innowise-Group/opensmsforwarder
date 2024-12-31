package org.open.smsforwarder.ui.steps.addrecipientdetails.addphonedetails

import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.domain.model.Rule
import org.open.smsforwarder.utils.Resources

data class AddPhoneDetailsState(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val recipientPhone: String = "",
    val inputError: Resources.StringProvider? = null,
    val rules: List<Rule> = emptyList()
) {

    val nextButtonEnabled = inputError == null && recipientPhone.isNotBlank()
}
