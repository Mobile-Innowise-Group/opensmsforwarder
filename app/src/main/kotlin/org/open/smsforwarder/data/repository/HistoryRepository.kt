package org.open.smsforwarder.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.local.database.dao.HistoryDao
import org.open.smsforwarder.data.mapper.Mapper
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
    private val mapper: Mapper
) {
    suspend fun getForwardedMessagesForLast24Hours(): Int = withContext(Dispatchers.IO) {
        historyDao.getForwardedMessagesCountLast24Hours()
    }

    suspend fun insertOrUpdateForwardedSms(
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
}
