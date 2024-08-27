package com.github.opensmsforwarder.data

import com.github.opensmsforwarder.data.local.database.dao.ForwardedSmsDao
import com.github.opensmsforwarder.data.mapper.Mapper
import com.github.opensmsforwarder.model.Recipient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ForwardedSmsRepository @Inject constructor(
    private val forwardedSmsDao: ForwardedSmsDao,
    private val mapper: Mapper
) {
    suspend fun getCountOfForwardedSms(startDate: Long, endDate: Long): Int {
        return forwardedSmsDao.getCountOfForwardedSms(startDate, endDate)
    }

    suspend fun insertForwardedSms(
        recipient: Recipient,
        message: String,
        isForwardSuccessful: Boolean
    ) {
        withContext(Dispatchers.IO) {
            forwardedSmsDao.insertForwardedSms(
                mapper.fromRecipientToForwardedSmsEntity(
                    recipient = recipient,
                    time = System.currentTimeMillis(),
                    message = message,
                    isForwardSuccessful = isForwardSuccessful
                )
            )
        }
    }
}
