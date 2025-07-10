package org.open.smsforwarder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.open.smsforwarder.data.local.database.dao.AuthTokenDao
import org.open.smsforwarder.data.local.database.dao.ForwardingDao
import org.open.smsforwarder.data.local.database.dao.HistoryDao
import org.open.smsforwarder.data.local.database.dao.RulesDao
import org.open.smsforwarder.data.local.database.entity.AuthTokenEntity
import org.open.smsforwarder.data.local.database.entity.ForwardingEntity
import org.open.smsforwarder.data.local.database.entity.HistoryEntity
import org.open.smsforwarder.data.local.database.entity.RuleEntity

@Database(
    entities = [
        ForwardingEntity::class,
        AuthTokenEntity::class,
        RuleEntity::class,
        HistoryEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forwardingDao(): ForwardingDao
    abstract fun authDao(): AuthTokenDao
    abstract fun rulesDao(): RulesDao
    abstract fun historyDao(): HistoryDao
}
