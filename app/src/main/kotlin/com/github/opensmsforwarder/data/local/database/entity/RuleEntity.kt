package com.github.opensmsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

const val RULES_TABLE = "rules_table"
const val RULE_ID_FIELD = "id"
const val RULE_FIELD = "rule"

@Entity(
    tableName = RULES_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = RecipientEntity::class,
            parentColumns = [RECIPIENT_ID_FIELD],
            childColumns = [RECIPIENT_ID],
            onDelete = CASCADE
        )
    ]
)
data class RuleEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RULE_ID_FIELD)
    val id: Long = 0L,

    @ColumnInfo(name = RECIPIENT_ID)
    val recipientId: Long,

    @ColumnInfo(name = RULE_FIELD)
    val rule: String,
)
