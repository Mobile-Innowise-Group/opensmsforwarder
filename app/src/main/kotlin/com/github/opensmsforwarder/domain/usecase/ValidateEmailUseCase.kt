package com.github.opensmsforwarder.domain.usecase

import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.domain.EmailValidator
import com.github.opensmsforwarder.domain.ValidationResult
import com.github.opensmsforwarder.utils.Resources
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
