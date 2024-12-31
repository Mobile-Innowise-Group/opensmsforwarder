package org.open.smsforwarder.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.analytics.FirebaseTracker
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
