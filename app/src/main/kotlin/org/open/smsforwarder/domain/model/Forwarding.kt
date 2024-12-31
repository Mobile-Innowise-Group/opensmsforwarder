package org.open.smsforwarder.domain.model

data class Forwarding(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val senderEmail: String? = null,
    val recipientPhone: String = "",
    val recipientEmail: String = "",
    val error: String = "",
) {

    val isSmsForwardingType: Boolean get() = forwardingType != null && forwardingType == ForwardingType.SMS

    val isEmailForwardingType: Boolean get() = forwardingType != null && forwardingType == ForwardingType.EMAIL
}
