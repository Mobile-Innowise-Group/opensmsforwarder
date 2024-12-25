package com.github.opensmsforwarder.ui.steps.addrecipientdetails.addphonedetails

import com.github.opensmsforwarder.domain.model.ForwardingType
import com.github.opensmsforwarder.domain.model.Rule
import com.github.opensmsforwarder.utils.Resources

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
