package org.open.smsforwarder.ui.feedback

import org.open.smsforwarder.domain.ValidationError

data class FeedbackState(
    val emailInputErrorType: ValidationError? = null,
    val bodyInputErrorType: ValidationError? = null,
    val emailInput: String? = null,
    val bodyInput: String? = null
) {
    private val hasNoInputErrors = (emailInputErrorType == null) && (bodyInputErrorType == null)
    private val hasValues = !emailInput.isNullOrBlank() && !bodyInput.isNullOrBlank()
    val submitButtonEnabled = hasNoInputErrors && hasValues
}
