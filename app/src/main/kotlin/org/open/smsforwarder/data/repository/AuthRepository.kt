package org.open.smsforwarder.data.repository

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.AuthTokenDao
import org.open.smsforwarder.data.local.database.entity.AuthTokenEntity
import org.open.smsforwarder.data.remote.dto.AuthorizationResult
import org.open.smsforwarder.data.remote.dto.SignInResult
import org.open.smsforwarder.data.remote.service.AuthService
import org.open.smsforwarder.domain.GoogleAuthClient
import org.open.smsforwarder.domain.IdTokenParser
import org.open.smsforwarder.utils.runSuspendCatching
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val authTokenDao: AuthTokenDao,
    private val googleAuthClient: GoogleAuthClient,
    private val idTokenParser: IdTokenParser,
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun getSignInIntent(context: Context): Result<SignInResult> =
        withContext(ioDispatcher) {
            runSuspendCatching {
                googleAuthClient.getSignInIntent(context)
            }
        }

    suspend fun processAuthorizationResult(
        data: Intent?,
        forwardingId: Long
    ): Result<AuthorizationResult> =
        withContext(ioDispatcher) {
            runSuspendCatching {
                val authCode = googleAuthClient.extractAuthorizationCode(data)
                val authResponse = authService.exchangeAuthCodeForTokens(authCode)
                val senderEmail = idTokenParser.extractEmail(authResponse.idToken)
                saveTokens(forwardingId, authResponse.accessToken, authResponse.refreshToken)
                AuthorizationResult(email = senderEmail)
            }
        }

    suspend fun signOut(forwardingId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            runSuspendCatching {
                val authTokenEntity =
                    authTokenDao.getAuthToken(forwardingId) ?: return@runSuspendCatching
                authService.revokeToken(authTokenEntity.accessToken)
                authTokenDao.upsertAuthToken(
                    authTokenEntity.copy(
                        accessToken = null,
                        refreshToken = null
                    )
                )
            }
        }

    private suspend fun saveTokens(
        forwardingId: Long,
        accessToken: String,
        refreshToken: String,
    ) {
        val authEntity = authTokenDao.getAuthToken(forwardingId)?.copy(
            accessToken = accessToken,
            refreshToken = refreshToken
        ) ?: AuthTokenEntity(
            forwardingId = forwardingId,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
        authTokenDao.upsertAuthToken(authEntity)
    }
}
