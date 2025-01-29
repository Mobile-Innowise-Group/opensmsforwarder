package org.open.smsforwarder.ui.feedback

import org.open.smsforwarder.utils.Resources

data class FeedbackState(
    val emailInputError: Resources.StringProvider? = null,
    val bodyInputError: Resources.StringProvider? = null,
) {
    val submitButtonEnabled = emailInputError == null && bodyInputError == null
}
