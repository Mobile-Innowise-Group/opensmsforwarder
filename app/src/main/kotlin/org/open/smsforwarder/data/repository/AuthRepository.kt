package org.open.smsforwarder.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.AuthTokenDao
import org.open.smsforwarder.data.local.database.entity.AuthTokenEntity
import org.open.smsforwarder.data.remote.dto.AuthCodeExchangeResponse
import org.open.smsforwarder.data.remote.service.AuthService
import org.open.smsforwarder.domain.model.Forwarding
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val authTokenDao: AuthTokenDao,
    private val forwardingRepository: ForwardingRepository,
) {

    suspend fun exchangeAuthCodeForTokensAnd(authCode: String?): Result<AuthCodeExchangeResponse> =
        runCatching {
            authService.exchangeAuthCodeForTokens(authCode)
        }

    suspend fun saveTokens(
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

    suspend fun signOut(forwarding: Forwarding): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO) {
                authTokenDao.getAuthToken(forwarding.id)?.let {
                    authTokenDao.upsertAuthToken(it.copy(accessToken = null, refreshToken = null))
                }
                forwardingRepository.insertOrUpdateForwarding(forwarding.copy(senderEmail = null))
            }
        }
}
