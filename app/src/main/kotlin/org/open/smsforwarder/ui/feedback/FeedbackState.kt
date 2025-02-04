package org.open.smsforwarder.ui.feedback

import org.open.smsforwarder.utils.Resources

data class FeedbackState(
    val emailInputError: Resources.StringProvider? = null,
    val bodyInputError: Resources.StringProvider? = null,
    val emailInput: String? = null,
    val bodyInput: String? = null
) {
    private val hasNoInputErrors = emailInputError == null && bodyInputError == null
    private val hasValues = !emailInput.isNullOrBlank() && !bodyInput.isNullOrBlank()
    val submitButtonEnabled = hasNoInputErrors && hasValues
}
