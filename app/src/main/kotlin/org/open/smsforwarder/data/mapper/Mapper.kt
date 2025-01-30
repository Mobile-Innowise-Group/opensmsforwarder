package org.open.smsforwarder.data.mapper

import org.open.smsforwarder.data.local.database.entity.HistoryEntity
import javax.inject.Inject

class Mapper @Inject constructor() {

    fun toHistoryEntity(
        id: Long,
        forwardingId: Long,
        time: Long,
        message: String,
        isForwardingSuccessful: Boolean,
    ): HistoryEntity =
        HistoryEntity(
            id = id,
            forwardingId = forwardingId,
            date = time,
            message = message,
            isForwardingSuccessful = isForwardingSuccessful
        )
}
