package org.open.smsforwarder.ui.steps.addrecipientdetails.addphonedetails

import org.open.smsforwarder.domain.ValidationError
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.domain.model.Rule

data class AddPhoneDetailsState(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val recipientPhone: String = "",
    val inputErrorType: ValidationError? = null,
    val rules: List<Rule> = emptyList()
) {

    val nextButtonEnabled = inputErrorType == null && recipientPhone.isNotBlank()
}
