package com.github.opensmsforwarder.di

import android.content.Context
import com.github.opensmsforwarder.processing.notifiction.WorkManagerConfigurator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WorkManagerModule {
    @Provides
    @Singleton
    fun provideWorkManagerConfigurator(@ApplicationContext context: Context): WorkManagerConfigurator =
        WorkManagerConfigurator(context)
}
