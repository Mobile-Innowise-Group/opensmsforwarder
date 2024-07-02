package com.github.opensmsforwarder.processing.validator

import java.util.regex.Pattern
import javax.inject.Inject

class EmailValidator @Inject constructor() : Validator {
    override val pattern: Pattern = Pattern.compile(EMAIL_PATTERN)

    private companion object {
        const val EMAIL_PATTERN = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,20}\$"
    }
}
