package org.open.smsforwarder.di

import android.content.Context
import android.net.ConnectivityManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.open.smsforwarder.data.NetworkStateObserverImpl
import org.open.smsforwarder.domain.NetworkStateObserver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkStateModule {

    @Binds
    @Singleton
    abstract fun bindNetworkStateObserver(newStateObserverImpl: NetworkStateObserverImpl): NetworkStateObserver

    companion object {
        @Provides
        @Singleton
        fun provideConnectivityManager(
            @ApplicationContext context: Context
        ): ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @Provides
        @Singleton
        fun provideAppCoroutineScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
