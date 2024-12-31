package org.open.smsforwarder.ui.model

import org.open.smsforwarder.domain.model.ForwardingType

data class ForwardingUI(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val senderEmail: String? = null,
    val recipientPhone: String = "",
    val recipientEmail: String = "",
    val error: String = "",
    val atLeastOneRuleAdded: Boolean = true
) {

    fun isEmailBlockCompleted() =
        forwardingType == ForwardingType.EMAIL && recipientEmail.isNotEmpty()

    fun isSmsBlockCompleted() =
        forwardingType == ForwardingType.SMS && recipientPhone.isNotEmpty()

    val allStepsCompleted: Boolean
        get() = ((isEmailBlockCompleted() && !senderEmail.isNullOrEmpty())
                || isSmsBlockCompleted()) && atLeastOneRuleAdded
}
