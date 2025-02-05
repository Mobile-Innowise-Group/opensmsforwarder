package org.open.smsforwarder.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.processing.notifiction.WorkManagerConfigurator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WorkManagerModule {
    @Provides
    @Singleton
    fun provideWorkManagerConfigurator(@ApplicationContext context: Context): WorkManagerConfigurator =
        WorkManagerConfigurator(context)
}
