package com.github.opensmsforwarder.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.opensmsforwarder.data.local.database.entity.FORWARDED_SMS_TABLE
import com.github.opensmsforwarder.data.local.database.entity.FORWARDED_SMS_DATE_FIELD
import com.github.opensmsforwarder.data.local.database.entity.ForwardedSmsEntity

@Dao
interface ForwardedSmsDao {
    @Query("SELECT COUNT(*) FROM $FORWARDED_SMS_TABLE WHERE $FORWARDED_SMS_DATE_FIELD >= :startDate AND $FORWARDED_SMS_DATE_FIELD < :endDate")
    suspend fun getCountOfForwardedSms(startDate: Long, endDate: Long): Int

    @Insert
    suspend fun insertForwardedSms(forwardedSmsEntity: ForwardedSmsEntity): Long
}
