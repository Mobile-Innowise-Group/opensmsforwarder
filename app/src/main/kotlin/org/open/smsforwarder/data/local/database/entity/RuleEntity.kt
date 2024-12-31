package org.open.smsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import org.open.smsforwarder.data.local.database.entity.ForwardingEntity.Companion.ID
import org.open.smsforwarder.data.local.database.entity.RuleEntity.Companion.FORWARDING_ID
import org.open.smsforwarder.data.local.database.entity.RuleEntity.Companion.FORWARDING_RULES_TABLE

@Entity(
    tableName = FORWARDING_RULES_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = ForwardingEntity::class,
            parentColumns = [ID],
            childColumns = [FORWARDING_ID],
            onDelete = CASCADE
        )
    ]
)
data class RuleEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0L,

    @ColumnInfo(name = FORWARDING_ID)
    val forwardingId: Long,

    @ColumnInfo(name = RULE)
    val rule: String,
) {

    companion object {
        const val FORWARDING_RULES_TABLE = "forwarding_rules_table"
        const val ID = "id"
        const val FORWARDING_ID = "forwarding_id"
        const val RULE = "rule"
    }
}
