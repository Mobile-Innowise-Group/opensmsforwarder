package com.github.opensmsforwarder.data.mapper

import com.github.opensmsforwarder.data.local.database.entity.HistoryEntity
import javax.inject.Inject

class Mapper @Inject constructor() {

    fun toHistoryEntity(
        forwardingId: Long,
        time: Long,
        message: String,
        isForwardingSuccessful: Boolean,
    ): HistoryEntity =
        HistoryEntity(
            forwardingId = forwardingId,
            date = time,
            message = message,
            isForwardingSuccessful = isForwardingSuccessful
        )
}
