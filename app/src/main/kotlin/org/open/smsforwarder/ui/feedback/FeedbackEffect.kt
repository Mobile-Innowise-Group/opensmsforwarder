package org.open.smsforwarder.ui.feedback

import androidx.annotation.StringRes

sealed interface FeedbackEffect

data class SubmitResultEffect(@StringRes val messageRes: Int) : FeedbackEffect
