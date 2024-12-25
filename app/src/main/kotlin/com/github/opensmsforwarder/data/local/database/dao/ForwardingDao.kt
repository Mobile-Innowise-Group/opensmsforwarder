package com.github.opensmsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.opensmsforwarder.data.local.database.entity.ForwardingEntity
import com.github.opensmsforwarder.data.local.database.entity.ForwardingEntity.Companion.FORWARDING_TABLE
import com.github.opensmsforwarder.data.local.database.entity.ForwardingEntity.Companion.ID
import kotlinx.coroutines.flow.Flow

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
