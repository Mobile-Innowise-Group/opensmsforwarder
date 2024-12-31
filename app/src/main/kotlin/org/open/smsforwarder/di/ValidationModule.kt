package org.open.smsforwarder.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.domain.EmailValidator
import org.open.smsforwarder.domain.PhoneValidator
import org.open.smsforwarder.processing.validator.EmailValidatorImpl
import org.open.smsforwarder.processing.validator.PhoneValidatorImpl

@InstallIn(SingletonComponent::class)
@Module
interface ValidationModule {

    @Binds
    fun bindEmailPatternValidator(emailPatternVerifierImpl: EmailValidatorImpl): EmailValidator

    @Binds
    fun bindPhonePatternValidator(phoneValidatorImpl: PhoneValidatorImpl): PhoneValidator
}
