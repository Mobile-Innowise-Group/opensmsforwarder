package org.open.smsforwarder.data.repository

import android.content.Context
import android.content.Intent
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import org.open.smsforwarder.data.remote.dto.SignInResult
import org.open.smsforwarder.domain.GoogleAuthClient
import javax.inject.Inject

class GoogleAuthClientImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authorizationRequest: AuthorizationRequest,
    private val getCredentialRequest: GetCredentialRequest,
) : GoogleAuthClient {

    override suspend fun getSignInIntent(context: Context): SignInResult =
        try {
            CredentialManager.create(context).getCredential(context, getCredentialRequest)

            val result =
                Identity.getAuthorizationClient(context).authorize(authorizationRequest).await()

            val intentSender =
                result.pendingIntent?.intentSender
                    ?: throw GoogleSignInException("Error getting intentSender")

            SignInResult(intentSender)
        } catch (e: CancellationException) {
            throw e
        } catch (e: GetCredentialException) {
            throw GoogleSignInException("Error retrieving credentials", e)
        } catch (e: ApiException) {
            throw GoogleSignInException("Google Sign-In API error", e)
        }

    override fun extractAuthorizationCode(data: Intent?): String {
        return try {
            val authorizationResult = Identity
                .getAuthorizationClient(context)
                .getAuthorizationResultFromIntent(data)
            authorizationResult.serverAuthCode ?: throw GoogleSignInException("Missing auth code")
        } catch (e: ApiException) {
            throw GoogleSignInException("Failed to extract auth code", e)
        }
    }
}

class GoogleSignInException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)
