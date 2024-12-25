package com.github.opensmsforwarder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.opensmsforwarder.data.local.database.dao.AuthTokenDao
import com.github.opensmsforwarder.data.local.database.dao.ForwardingDao
import com.github.opensmsforwarder.data.local.database.dao.HistoryDao
import com.github.opensmsforwarder.data.local.database.dao.RulesDao
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity
import com.github.opensmsforwarder.data.local.database.entity.ForwardingEntity
import com.github.opensmsforwarder.data.local.database.entity.HistoryEntity
import com.github.opensmsforwarder.data.local.database.entity.RuleEntity

@Database(
    entities = [
        ForwardingEntity::class,
        AuthTokenEntity::class,
        RuleEntity::class,
        HistoryEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forwardingDao(): ForwardingDao
    abstract fun authDao(): AuthTokenDao
    abstract fun rulesDao(): RulesDao
    abstract fun historyDao(): HistoryDao
}
