package org.open.smsforwarder.di

import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.processing.forwarder.EmailForwarder
import org.open.smsforwarder.processing.forwarder.Forwarder
import org.open.smsforwarder.processing.forwarder.TelegramForwarder

@Module
@InstallIn(SingletonComponent::class)
abstract class Forwarders {

    @Binds
    @IntoMap
    @ForwardingTypeKey(ForwardingType.EMAIL)
    abstract fun provideEmailForwarder(emailForwarder: EmailForwarder): Forwarder

    @Binds
    @IntoMap
    @ForwardingTypeKey(ForwardingType.TELEGRAM)
    abstract fun provideTelegramForwarder(telegramForwarder: TelegramForwarder): Forwarder
}

@MapKey
internal annotation class ForwardingTypeKey(val value: ForwardingType)
