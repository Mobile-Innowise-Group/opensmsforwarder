package org.open.smsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.open.smsforwarder.data.local.database.entity.HistoryEntity
import org.open.smsforwarder.data.local.database.entity.HistoryEntity.Companion.DATE
import org.open.smsforwarder.data.local.database.entity.HistoryEntity.Companion.FORWARDING_HISTORY_TABLE

@Dao
interface HistoryDao {
    @Upsert
    suspend fun upsertForwardedSms(historyEntity: HistoryEntity): Long

    @Query(
        """
        SELECT COUNT(*)
        FROM $FORWARDING_HISTORY_TABLE
        WHERE $DATE >= (strftime('%s', 'now', '-1 day')) * 1000
    """
    )
    suspend fun getForwardedMessagesCountLast24Hours(): Int
}
