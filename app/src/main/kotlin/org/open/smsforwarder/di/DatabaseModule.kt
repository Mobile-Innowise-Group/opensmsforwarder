package org.open.smsforwarder.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.data.local.database.AppDatabase
import org.open.smsforwarder.data.local.database.dao.AuthTokenDao
import org.open.smsforwarder.data.local.database.dao.ForwardingDao
import org.open.smsforwarder.data.local.database.dao.HistoryDao
import org.open.smsforwarder.data.local.database.dao.RulesDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

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

    private companion object {
        const val DATABASE_NAME = "sms_forwarder"
    }
}
