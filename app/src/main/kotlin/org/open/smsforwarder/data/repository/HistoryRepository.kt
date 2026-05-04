package org.open.smsforwarder.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.HistoryDao
import org.open.smsforwarder.data.local.database.entity.HistoryEntity
import org.open.smsforwarder.data.mapper.toDomain
import org.open.smsforwarder.data.security.DataCipher
import org.open.smsforwarder.domain.model.History
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
    private val dataCipher: DataCipher,
) {

    suspend fun getForwardedMessagesForLast24Hours(): Int = withContext(Dispatchers.IO) {
        historyDao.getForwardedMessagesCountLast24Hours()
    }

    fun getForwardingHistoryFlow(): Flow<List<History>> =
        historyDao
            .getForwardingHistoryFlow()
            .distinctUntilChanged()
            .map { historyEntity ->
                historyEntity.map { entity ->
                    entity.copy(
                        message = dataCipher.decrypt(entity.message).orEmpty()
                    ).toDomain()
                }
            }

    suspend fun insertForwardedSms(
        forwardingId: Long,
        message: String,
        isForwardingSuccessful: Boolean
    ) {
        withContext(Dispatchers.IO) {
            historyDao.upsertForwardedSms(
                HistoryEntity(
                    date = System.currentTimeMillis(),
                    forwardingId = forwardingId,
                    message = dataCipher.encrypt(message).orEmpty(),
                    isForwardingSuccessful = isForwardingSuccessful
                )
            )
        }
    }
}
