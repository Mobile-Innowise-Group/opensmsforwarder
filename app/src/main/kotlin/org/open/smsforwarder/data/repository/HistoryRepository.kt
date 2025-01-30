package org.open.smsforwarder.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.HistoryDao
import org.open.smsforwarder.data.local.database.entity.HistoryEntity
import org.open.smsforwarder.data.mapper.Mapper
import org.open.smsforwarder.data.mapper.toDomain
import org.open.smsforwarder.domain.model.History
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
    private val mapper: Mapper
) {
    suspend fun getForwardedMessagesForLast24Hours(): Int = withContext(Dispatchers.IO) {
        historyDao.getForwardedMessagesCountLast24Hours()
    }

    suspend fun insertOrUpdateForwardedSms(
        historyEntityId: Long?,
        forwardingId: Long,
        message: String,
        isForwardingSuccessful: Boolean
    ) {
        withContext(Dispatchers.IO) {
            historyDao.upsertForwardedSms(
                mapper.toHistoryEntity(
                    id = historyEntityId ?: 0L,
                    forwardingId = forwardingId,
                    time = System.currentTimeMillis(),
                    message = message,
                    isForwardingSuccessful = isForwardingSuccessful
                )
            )
        }
    }

    fun getForwardedMessagesFlow(): Flow<List<History>> =
        historyDao.getForwardedMessagesFlow()
            .distinctUntilChanged()
            .map { historyEntity -> historyEntity.map(HistoryEntity::toDomain) }


    suspend fun getForwardedMessageById(id: Long): History? =
        withContext(Dispatchers.IO) {
            historyDao.getForwardedMessageById(id)?.toDomain()
        }
}
