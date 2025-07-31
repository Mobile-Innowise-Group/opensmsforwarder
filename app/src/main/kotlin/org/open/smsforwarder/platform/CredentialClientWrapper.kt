package org.open.smsforwarder.platform

import android.app.Activity

interface CredentialClientWrapper {
    suspend fun getCredential(activity: Activity)
}
