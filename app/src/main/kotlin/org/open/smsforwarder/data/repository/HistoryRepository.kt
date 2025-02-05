package org.open.smsforwarder.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.HistoryDao
import org.open.smsforwarder.data.local.database.entity.HistoryEntity
import org.open.smsforwarder.data.mapper.Mapper
import org.open.smsforwarder.data.mapper.toData
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

    suspend fun insertForwardedSms(
        forwardingId: Long,
        message: String,
        isForwardingSuccessful: Boolean
    ) {
        withContext(Dispatchers.IO) {
            historyDao.upsertForwardedSms(
                mapper.toHistoryEntity(
                    forwardingId = forwardingId,
                    time = System.currentTimeMillis(),
                    message = message,
                    isForwardingSuccessful = isForwardingSuccessful
                )
            )
        }
    }

    suspend fun updateForwardedSms(history: History) {
        withContext(Dispatchers.IO) {
            historyDao.upsertForwardedSms(history.toData())
        }
    }

    fun getForwardingHistoryFlow(): Flow<List<History>> =
        historyDao
            .getForwardingHistoryFlow()
            .distinctUntilChanged()
            .map { historyEntity -> historyEntity.map(HistoryEntity::toDomain) }


    suspend fun getForwardingHistoryById(id: Long): History? =
        withContext(Dispatchers.IO) {
            historyDao.getForwardingHistoryById(id)?.toDomain()
        }
}
