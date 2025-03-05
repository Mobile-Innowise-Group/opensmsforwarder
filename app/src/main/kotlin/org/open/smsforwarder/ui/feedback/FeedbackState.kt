package org.open.smsforwarder.ui.feedback

import org.open.smsforwarder.utils.Resources

data class FeedbackState(
    val emailInputErrorProvider: Resources.StringProvider? = null,
    val bodyInputErrorProvider: Resources.StringProvider? = null,
    val emailInput: String? = null,
    val bodyInput: String? = null
) {
    private val hasNoInputErrors = (emailInputErrorProvider == null) && (bodyInputErrorProvider == null)
    private val hasValues = !emailInput.isNullOrBlank() && !bodyInput.isNullOrBlank()
    val submitButtonEnabled = hasNoInputErrors && hasValues
}
