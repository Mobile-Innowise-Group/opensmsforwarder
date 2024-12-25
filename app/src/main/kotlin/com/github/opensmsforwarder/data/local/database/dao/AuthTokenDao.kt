package com.github.opensmsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity.Companion.AUTH_TABLE
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity.Companion.FORWARDING_ID

@Dao
interface AuthTokenDao {

    @Query("SELECT * FROM $AUTH_TABLE WHERE $FORWARDING_ID = :recipientId")
    suspend fun getAuthToken(recipientId: Long): AuthTokenEntity?

    @Upsert
    suspend fun upsertAuthToken(authTokenEntity: AuthTokenEntity)
}
