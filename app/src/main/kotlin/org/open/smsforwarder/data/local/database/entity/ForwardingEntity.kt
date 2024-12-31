package org.open.smsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.open.smsforwarder.data.local.database.entity.ForwardingEntity.Companion.FORWARDING_TABLE
import org.open.smsforwarder.domain.model.ForwardingType

@Entity(tableName = FORWARDING_TABLE)
data class ForwardingEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0L,

    @ColumnInfo(name = TITLE)
    val forwardingTitle: String = "",

    @ColumnInfo(name = FORWARDING_TYPE)
    val forwardingType: ForwardingType? = null,

    @ColumnInfo(name = SENDER_EMAIL)
    val senderEmail: String? = null,

    @ColumnInfo(name = RECIPIENT_PHONE)
    val recipientPhone: String = "",

    @ColumnInfo(name = RECIPIENT_EMAIL)
    val recipientEmail: String = "",

    @ColumnInfo(name = ERROR_TEXT)
    val errorText: String = ""
) {

    companion object {
        const val FORWARDING_TABLE = "forwarding_table"
        const val ID = "id"
        const val TITLE = "title"
        const val FORWARDING_TYPE = "forwarding_type"
        const val RECIPIENT_PHONE = "recipient_phone"
        const val SENDER_EMAIL = "sender_email"
        const val RECIPIENT_EMAIL = "recipient_email"
        const val ERROR_TEXT = "error_text"
    }
}
