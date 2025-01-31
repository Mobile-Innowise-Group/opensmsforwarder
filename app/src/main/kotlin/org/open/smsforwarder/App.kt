package org.open.smsforwarder

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.processing.notifiction.WorkManagerConfigurator
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workManagerConfigurator: WorkManagerConfigurator

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltWorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }

    override val workManagerConfiguration: Configuration = Configuration.Builder()
        .setWorkerFactory(
            EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory()
        )
        .build()

    override fun onCreate() {
        super.onCreate()
        workManagerConfigurator.scheduleDailyForwardedMessageCountWork()
    }
}
