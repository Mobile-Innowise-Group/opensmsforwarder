package com.github.opensmsforwarder.di

import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.analytics.FirebaseTracker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsBindingModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsService(
        firebaseAnalyticsManager: FirebaseTracker
    ): AnalyticsTracker
}
