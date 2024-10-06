package com.github.opensmsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.opensmsforwarder.data.local.database.entity.FORWARDED_SMS_DATE_FIELD
import com.github.opensmsforwarder.data.local.database.entity.FORWARDING_HISTORY_TABLE
import com.github.opensmsforwarder.data.local.database.entity.ForwardingHistoryEntity

@Dao
interface ForwardingHistoryDao {
    @Upsert
    suspend fun upsertForwardedSms(forwardingHistoryEntity: ForwardingHistoryEntity): Long

    @Query(
        """
        SELECT COUNT(*)
        FROM $FORWARDING_HISTORY_TABLE
        WHERE $FORWARDED_SMS_DATE_FIELD >= (strftime('%s', 'now', '-1 day')) * 1000
    """
    )
    suspend fun getForwardedMessagesCountLast24Hours(): Int
}
