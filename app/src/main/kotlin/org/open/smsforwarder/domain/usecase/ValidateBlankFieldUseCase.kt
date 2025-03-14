package org.open.smsforwarder.domain.usecase

import org.open.smsforwarder.domain.ValidationError
import org.open.smsforwarder.domain.ValidationResult
import javax.inject.Inject

class ValidateBlankFieldUseCase @Inject constructor() {

    fun execute(field: String?): ValidationResult = if (field.isNullOrBlank()) {
        ValidationResult(
            successful = false,
            errorType = ValidationError.BLANK_FIELD
        )
    } else {
        ValidationResult(successful = true)
    }
}
