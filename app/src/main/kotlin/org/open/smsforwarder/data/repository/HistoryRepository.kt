package org.open.smsforwarder.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.HistoryDao
import org.open.smsforwarder.data.local.database.entity.HistoryEntity
import org.open.smsforwarder.data.mapper.toDomain
import org.open.smsforwarder.domain.model.History
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
) {

    suspend fun getForwardedMessagesForLast24Hours(): Int = withContext(Dispatchers.IO) {
        historyDao.getForwardedMessagesCountLast24Hours()
    }

    fun getForwardingHistoryFlow(): Flow<List<History>> =
        historyDao
            .getForwardingHistoryFlow()
            .distinctUntilChanged()
            .map { historyEntity -> historyEntity.map(HistoryEntity::toDomain) }

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
                    message = message,
                    isForwardingSuccessful = isForwardingSuccessful
                )
            )
        }
    }
}
