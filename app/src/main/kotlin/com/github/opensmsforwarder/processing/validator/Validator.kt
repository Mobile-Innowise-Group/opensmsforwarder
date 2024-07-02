package com.github.opensmsforwarder.processing.validator

import java.util.regex.Pattern

interface Validator {

    val pattern: Pattern

    fun isValid(textToValidate: String?): Boolean =
        textToValidate?.let {
            pattern.matcher(textToValidate).matches()
        } ?: false
}
