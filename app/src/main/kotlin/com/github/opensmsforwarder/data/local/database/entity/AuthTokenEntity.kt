package com.github.opensmsforwarder.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity.Companion.AUTH_TABLE
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity.Companion.FORWARDING_ID
import com.github.opensmsforwarder.data.local.database.entity.ForwardingEntity.Companion.ID

@Entity(
    tableName = AUTH_TABLE,
    foreignKeys = [
        ForeignKey(
            entity = ForwardingEntity::class,
            parentColumns = [ID],
            childColumns = [FORWARDING_ID],
            onDelete = CASCADE
        )
    ]
)
data class AuthTokenEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Long = 0L,

    @ColumnInfo(name = FORWARDING_ID)
    val forwardingId: Long,

    @ColumnInfo(name = ACCESS_TOKEN)
    val accessToken: String? = null,

    @ColumnInfo(name = REFRESH_TOKEN)
    val refreshToken: String? = null,
) {

    companion object {
        const val AUTH_TABLE = "auth_table"
        const val ID = "id"
        const val FORWARDING_ID = "forwarding_id"
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
    }
}
