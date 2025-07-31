package org.open.smsforwarder.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.domain.EmailValidator
import org.open.smsforwarder.processing.validator.EmailValidatorImpl

@InstallIn(SingletonComponent::class)
@Module
interface ValidationModule {

    @Binds
    fun bindEmailPatternValidator(emailPatternVerifierImpl: EmailValidatorImpl): EmailValidator
}
