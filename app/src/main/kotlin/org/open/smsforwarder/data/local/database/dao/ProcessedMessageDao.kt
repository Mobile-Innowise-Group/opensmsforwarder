package org.open.smsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.open.smsforwarder.data.local.database.entity.ProcessedMessageEntity
import org.open.smsforwarder.data.local.database.entity.ProcessedMessageEntity.Companion.CREATED_AT
import org.open.smsforwarder.data.local.database.entity.ProcessedMessageEntity.Companion.PROCESSED_MESSAGES_TABLE

@Dao
interface ProcessedMessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProcessedMessage(entity: ProcessedMessageEntity): Long

    @Query("DELETE FROM $PROCESSED_MESSAGES_TABLE WHERE $CREATED_AT < :minCreatedAt")
    suspend fun deleteOlderThan(minCreatedAt: Long)
}
