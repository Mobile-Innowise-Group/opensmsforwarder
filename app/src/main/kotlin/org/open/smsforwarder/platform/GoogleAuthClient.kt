package org.open.smsforwarder.platform

import android.app.Activity
import android.content.Intent
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.common.api.ApiException
import org.open.smsforwarder.data.remote.dto.SignInResult
import javax.inject.Inject

class GoogleAuthClient @Inject constructor(
    private val credentialClientWrapper: CredentialClientWrapper,
    private val identityClientWrapper: IdentityClientWrapper
) {

    suspend fun getSignInIntent(activity: Activity): SignInResult {
        fetchCredentials(activity)
        val authorizationResult = authorize(activity)
        val intentSender = authorizationResult?.pendingIntent?.intentSender
            ?: throw GoogleSignInFailure.MissingPendingIntent

        return SignInResult(intentSender)
    }


    fun extractAuthorizationCode(data: Intent?): String {
        data ?: throw GoogleSignInFailure.AuthResultIntentIsNull
        val authorizationResult = identityClientWrapper.getAuthorizationResultFromIntent(data)
        return authorizationResult.serverAuthCode ?: throw GoogleSignInFailure.MissingAuthCode
    }

    private suspend fun fetchCredentials(activity: Activity) {
        try {
            credentialClientWrapper.getCredential(activity)
        } catch (e: GetCredentialCancellationException) {
            throw GoogleSignInFailure.CredentialCancellation(e)
        } catch (e: GetCredentialException) {
            throw GoogleSignInFailure.CredentialsNotFound(e)
        }
    }

    private suspend fun authorize(activity: Activity): AuthorizationResult? {
        return try {
            identityClientWrapper.authorize(activity)
        } catch (e: ApiException) {
            throw GoogleSignInFailure.AuthorizationFailed(e)
        }
    }
}

sealed class GoogleSignInFailure(cause: Throwable? = null) : RuntimeException(cause) {
    class CredentialsNotFound(cause: Throwable? = null) : GoogleSignInFailure(cause)
    class CredentialCancellation(cause: Throwable? = null) : GoogleSignInFailure(cause)
    class AuthorizationFailed(cause: Throwable? = null) : GoogleSignInFailure(cause)
    data object AuthResultIntentIsNull : GoogleSignInFailure()
    data object MissingPendingIntent : GoogleSignInFailure()
    data object MissingAuthCode : GoogleSignInFailure()
}
