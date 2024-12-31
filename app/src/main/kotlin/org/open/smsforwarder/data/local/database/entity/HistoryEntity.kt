package org.open.smsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.open.smsforwarder.data.local.database.entity.ForwardingEntity.Companion.ID
import org.open.smsforwarder.data.local.database.entity.HistoryEntity.Companion.FORWARDING_HISTORY_TABLE
import org.open.smsforwarder.data.local.database.entity.HistoryEntity.Companion.FORWARDING_ID

@Entity(
    tableName = FORWARDING_HISTORY_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = ForwardingEntity::class,
            parentColumns = [ID],
            childColumns = [FORWARDING_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0L,

    @ColumnInfo(name = FORWARDING_ID)
    val forwardingId: Long,

    @ColumnInfo(name = DATE)
    val date: Long? = null,

    @ColumnInfo(name = MESSAGE)
    val message: String = "",

    @ColumnInfo(name = SUCCESSFUL_FORWARD)
    val isForwardingSuccessful: Boolean = true
) {

    companion object {
        const val FORWARDING_HISTORY_TABLE = "forwarding_history_table"
        const val ID = "id"
        const val FORWARDING_ID = "forwarding_id"
        const val DATE = "date"
        const val MESSAGE = "message"
        const val SUCCESSFUL_FORWARD = "is_successful"
    }
}
