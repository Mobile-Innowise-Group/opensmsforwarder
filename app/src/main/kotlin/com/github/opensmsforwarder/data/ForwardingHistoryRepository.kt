package com.github.opensmsforwarder.data

import com.github.opensmsforwarder.data.local.database.dao.ForwardingHistoryDao
import com.github.opensmsforwarder.data.mapper.Mapper
import com.github.opensmsforwarder.model.Recipient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ForwardingHistoryRepository @Inject constructor(
    private val forwardingHistoryDao: ForwardingHistoryDao,
    private val mapper: Mapper
) {
    suspend fun insertForwardedSms(
        recipient: Recipient,
        message: String,
        isForwardingSuccessful: Boolean
    ) {
        withContext(Dispatchers.IO) {
            forwardingHistoryDao.insertForwardedSms(
                mapper.toForwardingHistoryEntity(
                    recipient = recipient,
                    time = System.currentTimeMillis(),
                    message = message,
                    isForwardingSuccessful = isForwardingSuccessful
                )
            )
        }
    }
}
