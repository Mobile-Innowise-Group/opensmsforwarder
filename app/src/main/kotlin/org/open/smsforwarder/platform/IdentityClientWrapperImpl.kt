package org.open.smsforwarder.platform

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IdentityClientWrapperImpl @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val authorizationRequest: AuthorizationRequest,
) : IdentityClientWrapper {

    override suspend fun authorize(activity: Activity): AuthorizationResult? =
        Identity.getAuthorizationClient(activity)
            .authorize(authorizationRequest)
            .await()

    override fun getAuthorizationResultFromIntent(
        data: Intent
    ): AuthorizationResult =
        Identity
            .getAuthorizationClient(applicationContext)
            .getAuthorizationResultFromIntent(data)

}
