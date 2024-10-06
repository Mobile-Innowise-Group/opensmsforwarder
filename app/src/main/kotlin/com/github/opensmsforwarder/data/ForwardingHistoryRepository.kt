package com.github.opensmsforwarder.data

import com.github.opensmsforwarder.data.local.database.dao.ForwardingHistoryDao
import com.github.opensmsforwarder.data.mapper.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ForwardingHistoryRepository @Inject constructor(
    private val forwardingHistoryDao: ForwardingHistoryDao,
    private val mapper: Mapper
) {
    suspend fun getForwardedMessagesForLast24Hours(): Int = withContext(Dispatchers.IO) {
        forwardingHistoryDao.getForwardedMessagesCountLast24Hours()
    }

    suspend fun insertOrUpdateForwardedSms(
        recipientId: Long,
        message: String,
        isForwardingSuccessful: Boolean
    ) {
        withContext(Dispatchers.IO) {
            forwardingHistoryDao.upsertForwardedSms(
                mapper.toForwardingHistoryEntity(
                    recipientId = recipientId,
                    time = System.currentTimeMillis(),
                    message = message,
                    isForwardingSuccessful = isForwardingSuccessful
                )
            )
        }
    }
}
