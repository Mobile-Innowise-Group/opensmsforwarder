package com.github.opensmsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.opensmsforwarder.data.local.database.entity.RECIPIENTS_TABLE
import com.github.opensmsforwarder.data.local.database.entity.RECIPIENT_ID_FIELD
import com.github.opensmsforwarder.data.local.database.entity.RecipientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipientsDao {

    @Query("SELECT * FROM $RECIPIENTS_TABLE")
    fun getRecipientsFlow(): Flow<List<RecipientEntity>>

    @Query("SELECT * FROM $RECIPIENTS_TABLE WHERE $RECIPIENT_ID_FIELD = :id")
    fun getRecipientByIdFlow(id: Long): Flow<RecipientEntity?>

    @Query("SELECT * FROM $RECIPIENTS_TABLE WHERE $RECIPIENT_ID_FIELD = :id")
    suspend fun getRecipientById(id: Long): RecipientEntity?

    @Upsert
    suspend fun upsertRecipient(recipientEntity: RecipientEntity): Long

    @Query("DELETE FROM $RECIPIENTS_TABLE WHERE $RECIPIENT_ID_FIELD = :id")
    suspend fun deleteRecipient(id: Long)
}
