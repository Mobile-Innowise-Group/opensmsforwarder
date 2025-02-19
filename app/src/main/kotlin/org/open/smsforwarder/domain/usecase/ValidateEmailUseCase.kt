package org.open.smsforwarder.domain.usecase

import org.open.smsforwarder.domain.EmailValidator
import org.open.smsforwarder.domain.ValidationError
import org.open.smsforwarder.domain.ValidationResult
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor(
    private val emailValidator: EmailValidator
) {

    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorType = ValidationError.BLANK_EMAIL
            )
        }
        return if (!emailValidator.isValid(email)) {
            ValidationResult(
                successful = false,
                errorType = ValidationError.INVALID_EMAIL
            )
        } else {
            ValidationResult(successful = true)
        }
    }
}
