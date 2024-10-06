package com.github.opensmsforwarder.di

import com.github.opensmsforwarder.model.ForwardingType
import com.github.opensmsforwarder.processing.forwarding.handler.EmailForwardingHandler
import com.github.opensmsforwarder.processing.forwarding.handler.ForwardingHandler
import com.github.opensmsforwarder.processing.forwarding.handler.SmsForwardingHandler
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class ForwardingHandlers {

    @Binds
    @IntoMap
    @ForwardingTypeKey(ForwardingType.EMAIL)
    abstract fun provideEmailForwardingHandler(emailForwardingHandler: EmailForwardingHandler): ForwardingHandler

    @Binds
    @IntoMap
    @ForwardingTypeKey(ForwardingType.SMS)
    abstract fun provideSmsForwardingHandler(smsForwardingHandler: SmsForwardingHandler): ForwardingHandler
}

@MapKey
internal annotation class ForwardingTypeKey(val value: ForwardingType)