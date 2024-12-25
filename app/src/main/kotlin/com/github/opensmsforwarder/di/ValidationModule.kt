package com.github.opensmsforwarder.di

import com.github.opensmsforwarder.domain.EmailValidator
import com.github.opensmsforwarder.domain.PhoneValidator
import com.github.opensmsforwarder.processing.validator.EmailValidatorImpl
import com.github.opensmsforwarder.processing.validator.PhoneValidatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface ValidationModule {

    @Binds
    fun bindEmailPatternValidator(emailPatternVerifierImpl: EmailValidatorImpl): EmailValidator

    @Binds
    fun bindPhonePatternValidator(phoneValidatorImpl: PhoneValidatorImpl): PhoneValidator
}
