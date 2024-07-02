package com.github.opensmsforwarder.ui.steps.addrecipient

import android.content.Intent
import androidx.annotation.StringRes

sealed interface AddRecipientViewEffect

data class GoogleSignInViewEffect(
    val signInIntent: Intent? = null
) : AddRecipientViewEffect

data class GoogleSignInErrorViewEffect(
    @StringRes val errorMessage: Int? = null
) : AddRecipientViewEffect
