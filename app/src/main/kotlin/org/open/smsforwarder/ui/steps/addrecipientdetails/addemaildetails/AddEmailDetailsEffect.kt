package org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails

import android.content.IntentSender
import androidx.annotation.StringRes

sealed interface AddEmailDetailsViewEffect

data class GoogleSignInIntentSenderEffect(val intentSender: IntentSender) : AddEmailDetailsViewEffect

data class GoogleSignInErrorEffect(@StringRes val errorMessageRes: Int? = null) :
    AddEmailDetailsViewEffect
