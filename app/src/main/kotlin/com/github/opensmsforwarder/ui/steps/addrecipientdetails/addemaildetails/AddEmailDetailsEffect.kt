package com.github.opensmsforwarder.ui.steps.addrecipientdetails.addemaildetails

import android.content.Intent
import androidx.annotation.StringRes

sealed interface AddEmailDetailsViewEffect

data class GoogleSignInViewEffect(
    val signInIntent: Intent? = null
) : AddEmailDetailsViewEffect

data class GoogleSignInErrorViewEffect(
    @StringRes val errorMessage: Int? = null
) : AddEmailDetailsViewEffect
