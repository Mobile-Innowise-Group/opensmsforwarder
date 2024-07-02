package com.github.opensmsforwarder.model

data class Recipient(
    val id: Long = 0,
    val title: String = "",
    val forwardingType: ForwardingType? = null,
    val senderEmail: String? = null,
    val recipientPhone: String = "",
    val recipientEmail: String = "",
    val isForwardSuccessful: Boolean = true,
) {

    val isSmsForwardingType: Boolean get() = forwardingType != null && forwardingType == ForwardingType.SMS

    val isEmailForwardingType: Boolean get() = forwardingType != null && forwardingType == ForwardingType.EMAIL

    val hasSenderEmail: Boolean get() = !senderEmail.isNullOrEmpty()

    val allStepsCompleted: Boolean
        get() = isEmailBlockCompleted() || isSmsBlockCompleted()

    fun getPhoneToSave() = if (isSmsForwardingType) recipientPhone else ""
    fun getEmailToSave() = if (isEmailForwardingType) recipientEmail else ""

    private fun isEmailBlockCompleted() =
        forwardingType == ForwardingType.EMAIL && senderEmail != null && recipientEmail.isNotEmpty()

    private fun isSmsBlockCompleted() =
        forwardingType == ForwardingType.SMS && recipientPhone.isNotEmpty()
}
