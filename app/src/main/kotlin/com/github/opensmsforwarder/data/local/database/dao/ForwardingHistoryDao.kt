package com.github.opensmsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.github.opensmsforwarder.data.local.database.entity.ForwardingHistoryEntity

@Dao
interface ForwardingHistoryDao {
    @Insert
    suspend fun insertForwardedSms(forwardingHistoryEntity: ForwardingHistoryEntity): Long
}
