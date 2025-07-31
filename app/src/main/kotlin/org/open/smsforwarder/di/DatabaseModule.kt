package org.open.smsforwarder.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.BuildConfig
import org.open.smsforwarder.data.local.database.AppDatabase
import org.open.smsforwarder.data.local.database.dao.AuthTokenDao
import org.open.smsforwarder.data.local.database.dao.ForwardingDao
import org.open.smsforwarder.data.local.database.dao.HistoryDao
import org.open.smsforwarder.data.local.database.dao.RulesDao
import org.open.smsforwarder.data.local.database.migration.MIGRATION_1_2
import org.open.smsforwarder.data.local.database.migration.RoomMigrationChecker
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    private val migrations = listOf(
        MIGRATION_1_2
    )

    @Provides
    @Singleton
    @Suppress("SpreadOperator")
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        if (BuildConfig.DEBUG) {
            RoomMigrationChecker.validateMigrations(appContext, migrations)
        }

        val database = Room
            .databaseBuilder(
                appContext,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
            .addMigrations(*migrations.toTypedArray())
            .build()
        return database
    }

    @Provides
    @Singleton
    fun provideRecipientsDao(database: AppDatabase): ForwardingDao = database.forwardingDao()

    @Provides
    @Singleton
    fun provideAuthDao(database: AppDatabase): AuthTokenDao = database.authDao()

    @Provides
    @Singleton
    fun provideRulesDao(database: AppDatabase): RulesDao = database.rulesDao()

    @Provides
    @Singleton
    fun provideForwardingHistoryDao(database: AppDatabase): HistoryDao =
        database.historyDao()
}
