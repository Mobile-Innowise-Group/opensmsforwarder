package com.github.opensmsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

const val AUTH_TABLE = "auth_table"
const val AUTH_ID_FIELD = "id"
const val RECIPIENT_ID = "recipient_id"
const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"

@Entity(
    tableName = AUTH_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = RecipientEntity::class,
            parentColumns = [RECIPIENT_ID_FIELD],
            childColumns = [RECIPIENT_ID],
            onDelete = CASCADE
        )
    ]
)
data class AuthTokenEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = AUTH_ID_FIELD)
    val id: Long = 0L,

    @ColumnInfo(name = RECIPIENT_ID)
    val recipientId: Long,

    @ColumnInfo(name = ACCESS_TOKEN)
    val accessToken: String? = null,

    @ColumnInfo(name = REFRESH_TOKEN)
    val refreshToken: String? = null,
)
