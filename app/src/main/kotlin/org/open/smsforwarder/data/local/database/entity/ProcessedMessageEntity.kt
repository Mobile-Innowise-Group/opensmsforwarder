package org.open.smsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.open.smsforwarder.data.local.database.entity.ProcessedMessageEntity.Companion.FINGERPRINT
import org.open.smsforwarder.data.local.database.entity.ProcessedMessageEntity.Companion.PROCESSED_MESSAGES_TABLE

@Entity(
    tableName = PROCESSED_MESSAGES_TABLE,
    indices = [Index(value = [FINGERPRINT], unique = true)]
)
data class ProcessedMessageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0L,
    @ColumnInfo(name = FINGERPRINT)
    val fingerprint: String,
    @ColumnInfo(name = CREATED_AT)
    val createdAt: Long,
) {
    companion object {
        const val PROCESSED_MESSAGES_TABLE = "processed_messages_table"
        const val ID = "id"
        const val FINGERPRINT = "fingerprint"
        const val CREATED_AT = "created_at"
    }
}
