package com.github.opensmsforwarder.ui.steps.addrecipient

import androidx.annotation.StringRes
import com.github.opensmsforwarder.model.Recipient

data class AddRecipientViewState(
    val recipient: Recipient = Recipient(),
    @StringRes val emailInputError: Int? = null,
    @StringRes val phoneInputError: Int? = null
)
