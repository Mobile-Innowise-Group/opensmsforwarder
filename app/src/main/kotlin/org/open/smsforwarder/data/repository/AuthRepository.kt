package org.open.smsforwarder.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.AuthTokenDao
import org.open.smsforwarder.data.local.database.entity.AuthTokenEntity
import org.open.smsforwarder.data.remote.dto.AuthorizationResult
import org.open.smsforwarder.data.remote.service.AuthService
import org.open.smsforwarder.data.security.DataCipher
import org.open.smsforwarder.domain.IdTokenParser
import org.open.smsforwarder.utils.runSuspendCatching
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val authTokenDao: AuthTokenDao,
    private val idTokenParser: IdTokenParser,
    private val ioDispatcher: CoroutineDispatcher,
    private val dataCipher: DataCipher,
) {

    suspend fun exchangeAuthCodeForTokens(
        authCode: String,
        forwardingId: Long
    ): AuthorizationResult =
        withContext(ioDispatcher) {
            val authResponse = authService.exchangeAuthCodeForTokens(authCode)
            val senderEmail = idTokenParser.extractEmail(authResponse.idToken)
            saveTokens(forwardingId, authResponse.accessToken, authResponse.refreshToken)
            AuthorizationResult(email = senderEmail)
        }

    suspend fun signOut(forwardingId: Long): Result<Unit> =
        withContext(ioDispatcher) {
            runSuspendCatching {
                val authTokenEntity =
                    authTokenDao.getAuthToken(forwardingId) ?: return@runSuspendCatching
                val accessToken = dataCipher.decrypt(authTokenEntity.accessToken)
                if (!accessToken.isNullOrBlank()) {
                    authService.revokeToken(accessToken)
                }
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
        withContext(ioDispatcher) {
            val authEntity = authTokenDao.getAuthToken(forwardingId)?.copy(
                accessToken = dataCipher.encrypt(accessToken),
                refreshToken = dataCipher.encrypt(refreshToken)
            ) ?: AuthTokenEntity(
                forwardingId = forwardingId,
                accessToken = dataCipher.encrypt(accessToken),
                refreshToken = dataCipher.encrypt(refreshToken)
            )
            authTokenDao.upsertAuthToken(authEntity)
        }
    }
}
