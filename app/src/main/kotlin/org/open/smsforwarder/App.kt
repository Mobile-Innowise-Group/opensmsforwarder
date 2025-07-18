package org.open.smsforwarder

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
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

        FirebaseApp.initializeApp(this)
        val appCheck = FirebaseAppCheck.getInstance()

        if (BuildConfig.DEBUG) {
            appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        } else {
            appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        }
        workManagerConfigurator.scheduleDailyForwardedMessageCountWork()
    }
}
