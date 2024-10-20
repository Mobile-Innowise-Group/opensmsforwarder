package com.github.opensmsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

const val FORWARDING_HISTORY_TABLE = "forwarded_sms_table"
const val FORWARDED_SMS_ID_FIELD = "id"
const val FORWARDED_SMS_DATE_FIELD = "date"
const val FORWARDED_SMS_MESSAGE_FIELD = "message"
const val RECIPIENT_SUCCESSFUL_FORWARD_FIELD = "is_successful"

@Entity(
    tableName = FORWARDING_HISTORY_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = RecipientEntity::class,
            parentColumns = [RECIPIENT_ID_FIELD],
            childColumns = [RECIPIENT_ID],
            onDelete = CASCADE
        )
    ]
)

data class ForwardingHistoryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FORWARDED_SMS_ID_FIELD)
    val id: Long = 0L,

    @ColumnInfo(name = RECIPIENT_ID)
    val recipientId: Long,

    @ColumnInfo(name = FORWARDED_SMS_DATE_FIELD)
    val date: Long? = null,

    @ColumnInfo(name = FORWARDED_SMS_MESSAGE_FIELD)
    val message: String = "",

    @ColumnInfo(name = RECIPIENT_SUCCESSFUL_FORWARD_FIELD)
    val isForwardingSuccessful: Boolean = true
)
