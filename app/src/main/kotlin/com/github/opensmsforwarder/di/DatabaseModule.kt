package com.github.opensmsforwarder.di

import android.content.Context
import androidx.room.Room
import com.github.opensmsforwarder.data.local.database.RecipientsDatabase
import com.github.opensmsforwarder.data.local.database.dao.AuthTokenDao
import com.github.opensmsforwarder.data.local.database.dao.ForwardingHistoryDao
import com.github.opensmsforwarder.data.local.database.dao.RecipientsDao
import com.github.opensmsforwarder.data.local.database.dao.RulesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): RecipientsDatabase =
        Room.databaseBuilder(
            appContext,
            RecipientsDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideRecipientsDao(database: RecipientsDatabase): RecipientsDao = database.recipientsDao()

    @Provides
    @Singleton
    fun provideAuthDao(database: RecipientsDatabase): AuthTokenDao = database.authDao()

    @Provides
    @Singleton
    fun provideRulesDao(database: RecipientsDatabase): RulesDao = database.rulesDao()

    @Provides
    @Singleton
    fun provideForwardingHistoryDao(database: RecipientsDatabase): ForwardingHistoryDao =
        database.forwardingHistoryDao()

    private companion object {
        const val DATABASE_NAME = "sms_forwarder"
    }
}
