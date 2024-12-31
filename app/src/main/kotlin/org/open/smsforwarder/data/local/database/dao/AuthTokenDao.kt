package org.open.smsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.open.smsforwarder.data.local.database.entity.AuthTokenEntity
import org.open.smsforwarder.data.local.database.entity.AuthTokenEntity.Companion.AUTH_TABLE
import org.open.smsforwarder.data.local.database.entity.AuthTokenEntity.Companion.FORWARDING_ID

@Dao
interface AuthTokenDao {

    @Query("SELECT * FROM $AUTH_TABLE WHERE $FORWARDING_ID = :recipientId")
    suspend fun getAuthToken(recipientId: Long): AuthTokenEntity?

    @Upsert
    suspend fun upsertAuthToken(authTokenEntity: AuthTokenEntity)
}
