package org.open.smsforwarder.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.domain.EmailValidator
import org.open.smsforwarder.domain.GoogleChatWebHookValidator
import org.open.smsforwarder.processing.validator.EmailValidatorImpl
import org.open.smsforwarder.processing.validator.GoogleChatWebHookValidatorImpl

@InstallIn(SingletonComponent::class)
@Module
interface ValidationModule {

    @Binds
    fun bindEmailPatternValidator(emailPatternVerifierImpl: EmailValidatorImpl): EmailValidator

    @Binds
    fun bindGoogleChatWebHookValidator(
        googleChatWebHookValidatorImpl: GoogleChatWebHookValidatorImpl
    ): GoogleChatWebHookValidator
}
