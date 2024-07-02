package com.github.opensmsforwarder.processing.validator

import java.util.regex.Pattern
import javax.inject.Inject

class PhoneValidator @Inject constructor() : Validator {
    override val pattern: Pattern = Pattern.compile(PHONE_PATTERN)

    private companion object {
        const val PHONE_PATTERN = "^[+][0-9]{10,13}$"
    }
}
