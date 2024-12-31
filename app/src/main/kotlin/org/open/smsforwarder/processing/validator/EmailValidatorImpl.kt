package org.open.smsforwarder.processing.validator

import android.util.Patterns
import org.open.smsforwarder.domain.EmailValidator
import javax.inject.Inject

class EmailValidatorImpl @Inject constructor() : EmailValidator {

    override fun isValid(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
