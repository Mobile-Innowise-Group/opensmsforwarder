package org.open.smsforwarder.domain.usecase

import org.open.smsforwarder.R
import org.open.smsforwarder.domain.EmailValidator
import org.open.smsforwarder.domain.ValidationResult
import org.open.smsforwarder.utils.Resources
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor(
    private val emailValidator: EmailValidator
) {

    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = Resources.StringResource(R.string.error_email_is_blank)
            )
        }
        return if (!emailValidator.isValid(email)) {
            ValidationResult(
                successful = false,
                errorMessage = Resources.StringResource(R.string.error_email_is_not_valid)
            )
        } else {
            ValidationResult(successful = true)
        }
    }
}
