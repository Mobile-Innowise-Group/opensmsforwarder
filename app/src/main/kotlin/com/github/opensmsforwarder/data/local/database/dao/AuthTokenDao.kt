package com.github.opensmsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.opensmsforwarder.data.local.database.entity.AUTH_TABLE
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity
import com.github.opensmsforwarder.data.local.database.entity.RECIPIENT_ID

@Dao
interface AuthTokenDao {

    @Query("SELECT * FROM $AUTH_TABLE WHERE $RECIPIENT_ID = :recipientId")
    suspend fun getAuthToken(recipientId: Long): AuthTokenEntity?

    @Upsert
    suspend fun upsertAuthToken(authTokenEntity: AuthTokenEntity)
}
