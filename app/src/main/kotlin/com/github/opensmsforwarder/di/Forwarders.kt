package com.github.opensmsforwarder.di

import com.github.opensmsforwarder.model.ForwardingType
import com.github.opensmsforwarder.processing.handler.EmailForwarder
import com.github.opensmsforwarder.processing.handler.Forwarder
import com.github.opensmsforwarder.processing.handler.SmsForwarder
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class Forwarders {

    @Binds
    @IntoMap
    @ForwardingTypeKey(ForwardingType.EMAIL)
    abstract fun provideEmailForwarder(emailForwarder: EmailForwarder): Forwarder

    @Binds
    @IntoMap
    @ForwardingTypeKey(ForwardingType.SMS)
    abstract fun provideSmsForwarder(smsForwarder: SmsForwarder): Forwarder
}

@MapKey
internal annotation class ForwardingTypeKey(val value: ForwardingType)
