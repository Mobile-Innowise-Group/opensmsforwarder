package com.github.opensmsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.opensmsforwarder.model.ForwardingType

const val RECIPIENTS_TABLE = "recipients_table"
const val RECIPIENT_ID_FIELD = "id"
const val TITLE_FIELD = "title"
const val FORWARDING_TYPE_FIELD = "forwarding_type"
const val RECIPIENT_PHONE_FIELD = "recipient_phone"
const val SENDER_EMAIL_FIELD = "sender_email"
const val RECIPIENT_EMAIL_FIELD = "recipient_email"
const val RECIPIENT_SUCCESSFUL_FORWARD_FIELD = "recipient_is_successful_forward"

@Entity(tableName = RECIPIENTS_TABLE)
data class RecipientEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RECIPIENT_ID_FIELD)
    val id: Long = 0L,

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

    @ColumnInfo(name = RECIPIENT_SUCCESSFUL_FORWARD_FIELD)
    val isForwardSuccessful: Boolean = true
)
