package org.open.smsforwarder.ui.feedback

import androidx.annotation.StringRes

sealed interface FeedbackEffect

data class DisplaySubmitResultEffect(@StringRes val message: Int) : FeedbackEffect