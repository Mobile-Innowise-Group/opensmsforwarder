package org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails

import androidx.annotation.StringRes

sealed interface AddEmailDetailsViewEffect

data object GoogleSignInViewEffect : AddEmailDetailsViewEffect

data class GoogleSignInErrorViewEffect(
    @StringRes val errorMessageRes: Int? = null,
    val error: Throwable? = null
) : AddEmailDetailsViewEffect
