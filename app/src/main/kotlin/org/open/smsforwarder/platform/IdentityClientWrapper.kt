package org.open.smsforwarder.platform

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationResult

interface IdentityClientWrapper {
    suspend fun authorize(activity: Activity): AuthorizationResult?
    fun getAuthorizationResultFromIntent(data: Intent): AuthorizationResult
}
