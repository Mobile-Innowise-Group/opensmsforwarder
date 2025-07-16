package org.open.smsforwarder.platform

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import javax.inject.Inject

class CredentialClientWrapperImpl @Inject constructor(
    private val getCredentialRequest: GetCredentialRequest
) : CredentialClientWrapper {

    override suspend fun getCredential(activity: Activity) {
        CredentialManager.create(activity).getCredential(activity, getCredentialRequest)
    }
}
