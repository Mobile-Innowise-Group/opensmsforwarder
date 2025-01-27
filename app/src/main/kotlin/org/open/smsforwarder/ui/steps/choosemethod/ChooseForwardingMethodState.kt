package org.open.smsforwarder.ui.steps.choosemethod

import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.utils.Resources

data class ChooseForwardingMethodState(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val titleInputError: Resources.StringProvider? = null
) {

    val isSmsForwardingType: Boolean get() = forwardingType != null && forwardingType == ForwardingType.SMS

    val isEmailForwardingType: Boolean get() = forwardingType != null && forwardingType == ForwardingType.EMAIL

    val isNextButtonEnabled: Boolean get() = titleInputError == null && forwardingType != null
}
