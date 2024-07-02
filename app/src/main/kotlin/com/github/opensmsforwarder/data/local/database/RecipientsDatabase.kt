package com.github.opensmsforwarder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.opensmsforwarder.data.local.database.dao.AuthTokenDao
import com.github.opensmsforwarder.data.local.database.dao.RecipientsDao
import com.github.opensmsforwarder.data.local.database.dao.RulesDao
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity
import com.github.opensmsforwarder.data.local.database.entity.RecipientEntity
import com.github.opensmsforwarder.data.local.database.entity.RuleEntity

@Database(
    entities = [
        RecipientEntity::class,
        RuleEntity::class,
        AuthTokenEntity::class
    ],
    version = 1
)
abstract class RecipientsDatabase : RoomDatabase() {
    abstract fun recipientsDao(): RecipientsDao
    abstract fun authDao(): AuthTokenDao
    abstract fun rulesDao(): RulesDao
}
