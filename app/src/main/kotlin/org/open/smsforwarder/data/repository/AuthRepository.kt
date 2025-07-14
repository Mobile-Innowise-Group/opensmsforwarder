package org.open.smsforwarder.data.repository

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.AuthTokenDao
import org.open.smsforwarder.data.local.database.entity.AuthTokenEntity
import org.open.smsforwarder.data.remote.dto.AuthorizationResult
import org.open.smsforwarder.data.remote.dto.GoogleSignInResult
import org.open.smsforwarder.data.remote.service.AuthService
import org.open.smsforwarder.domain.GoogleAuthClient
import org.open.smsforwarder.domain.IdTokenParser
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val authTokenDao: AuthTokenDao,
    private val googleAuthClient: GoogleAuthClient,
    private val idTokenParser: IdTokenParser,
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun getSignInIntent(context: Context): Result<GoogleSignInResult> =
        withContext(ioDispatcher) {
            runCatching {
                googleAuthClient.getSignInIntent(context)
            }
        }

    suspend fun processAuthorizationResult(
        data: Intent?,
        forwardingId: Long
    ): Result<AuthorizationResult> =
        withContext(ioDispatcher) {
            runCatching {
                val authCode = googleAuthClient.extractAuthorizationCode(data)
                val authResponse = authService.exchangeAuthCodeForTokens(authCode)
                val senderEmail = idTokenParser.extractEmail(authResponse.idToken)
                saveTokens(forwardingId, authResponse.accessToken, authResponse.refreshToken)
                AuthorizationResult(email = senderEmail)
            }
        }

    suspend fun signOut(forwardingId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                val authTokenEntity = authTokenDao.getAuthToken(forwardingId) ?: return@runCatching
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
