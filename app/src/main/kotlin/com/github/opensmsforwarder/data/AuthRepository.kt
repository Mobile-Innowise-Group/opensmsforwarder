package com.github.opensmsforwarder.data

import com.github.opensmsforwarder.data.local.database.dao.AuthTokenDao
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity
import com.github.opensmsforwarder.data.remote.dto.AuthCodeExchangeResponse
import com.github.opensmsforwarder.data.remote.service.AuthService
import com.github.opensmsforwarder.model.Recipient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val authTokenDao: AuthTokenDao,
    private val recipientsRepository: RecipientsRepository,
) {

    suspend fun exchangeAuthCodeForTokensAnd(authCode: String?): Result<AuthCodeExchangeResponse> =
        kotlin.runCatching {
            authService.exchangeAuthCodeForTokens(authCode)
        }

    suspend fun saveTokensForRecipient(
        recipientId: Long,
        accessToken: String,
        refreshToken: String,
    ) {
        val authEntity = authTokenDao.getAuthToken(recipientId)?.copy(
            accessToken = accessToken,
            refreshToken = refreshToken
        ) ?: AuthTokenEntity(
            recipientId = recipientId,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
        authTokenDao.upsertAuthToken(authEntity)
    }

    suspend fun signOut(recipient: Recipient): Result<Unit> =
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                authTokenDao.getAuthToken(recipient.id)?.let {
                    authTokenDao.upsertAuthToken(it.copy(accessToken = null, refreshToken = null))
                }
                recipientsRepository.insertOrUpdateRecipient(recipient.copy(senderEmail = null))
            }
        }
}
