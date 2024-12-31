package org.open.smsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.open.smsforwarder.data.local.database.entity.ForwardingEntity
import org.open.smsforwarder.data.local.database.entity.ForwardingEntity.Companion.FORWARDING_TABLE
import org.open.smsforwarder.data.local.database.entity.ForwardingEntity.Companion.ID

@Dao
interface ForwardingDao {

    @Query("SELECT * FROM $FORWARDING_TABLE")
    fun getForwardingFlow(): Flow<List<ForwardingEntity>>

    @Query("SELECT * FROM $FORWARDING_TABLE WHERE $ID = :id")
    suspend fun getForwardingById(id: Long): ForwardingEntity?

    @Query("SELECT * FROM $FORWARDING_TABLE WHERE $ID = :id")
    fun getForwardingByIdFlow(id: Long): Flow<ForwardingEntity?>

    @Upsert
    suspend fun upsertForwarding(forwardingEntity: ForwardingEntity): Long

    @Query("DELETE FROM $FORWARDING_TABLE WHERE $ID = :id")
    suspend fun deleteForwarding(id: Long)
}
