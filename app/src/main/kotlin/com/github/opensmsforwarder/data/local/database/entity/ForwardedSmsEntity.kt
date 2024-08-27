package com.github.opensmsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.opensmsforwarder.model.ForwardingType

const val FORWARDED_SMS_TABLE = "forwarded_sms_table"
const val FORWARDED_SMS_ID_FIELD = "id"
const val FORWARDED_SMS_DATE_FIELD = "date"
const val FORWARDED_SMS_MESSAGE_FIELD = "message"

@Entity(tableName = FORWARDED_SMS_TABLE)
data class ForwardedSmsEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FORWARDED_SMS_ID_FIELD)
    val id: Long = 0L,

    @ColumnInfo(name = FORWARDED_SMS_DATE_FIELD)
    val date: Long? = null,

    @ColumnInfo(name = TITLE_FIELD)
    val title: String = "",

    @ColumnInfo(name = FORWARDING_TYPE_FIELD)
    val forwardingType: ForwardingType? = null,

    @ColumnInfo(name = SENDER_EMAIL_FIELD)
    val senderEmail: String? = null,

    @ColumnInfo(name = RECIPIENT_PHONE_FIELD)
    val recipientPhone: String = "",

    @ColumnInfo(name = RECIPIENT_EMAIL_FIELD)
    val recipientEmail: String = "",

    @ColumnInfo(name = FORWARDED_SMS_MESSAGE_FIELD)
    val message: String = "",

    @ColumnInfo(name = RECIPIENT_SUCCESSFUL_FORWARD_FIELD)
    val isForwardSuccessful: Boolean = true
)
