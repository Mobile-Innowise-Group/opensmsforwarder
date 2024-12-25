package com.github.opensmsforwarder.processing.validator

import com.github.opensmsforwarder.domain.PhoneValidator
import java.util.regex.Pattern
import javax.inject.Inject

class PhoneValidatorImpl @Inject constructor() : PhoneValidator {

    private val pattern: Pattern = Pattern.compile(PHONE_PATTERN)

    override fun isValid(phoneNumber: String): Boolean = pattern.matcher(phoneNumber).matches()

    private companion object {
        const val PHONE_PATTERN = "^[+][0-9]{10,13}$"
    }
}
