package org.open.smsforwarder.ui.feedback

import org.open.smsforwarder.utils.Resources

data class FeedbackState(
    val emailInputError: Resources.StringProvider? = null,
    val bodyInputError: Resources.StringProvider? = null,
    val emailInput: String? = null,
    val bodyInput: String? = null
) {
    val submitButtonEnabled =
        emailInputError == null && bodyInputError == null
                && !emailInput.isNullOrBlank() && !bodyInput.isNullOrBlank()
}
