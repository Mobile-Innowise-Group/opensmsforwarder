package com.github.opensmsforwarder.data.repository

import com.github.opensmsforwarder.data.local.database.dao.HistoryDao
import com.github.opensmsforwarder.data.mapper.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
