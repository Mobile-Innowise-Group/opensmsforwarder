package com.github.opensmsforwarder.processing.validator

import android.util.Patterns
import com.github.opensmsforwarder.domain.EmailValidator
import javax.inject.Inject

class EmailValidatorImpl @Inject constructor() : EmailValidator {

    override fun isValid(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
