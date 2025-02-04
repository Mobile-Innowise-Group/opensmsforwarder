package org.open.smsforwarder.domain.usecase

import org.open.smsforwarder.R
import org.open.smsforwarder.domain.ValidationResult
import org.open.smsforwarder.utils.Resources
import javax.inject.Inject

class ValidateBlankFieldUseCase @Inject constructor() {

    fun execute(field: String?): ValidationResult {
        return if (field.isNullOrBlank()) {
            ValidationResult(
                successful = false,
                errorMessage = Resources.StringResource(R.string.error_generic_is_blank)
            )
        } else {
            ValidationResult(successful = true)
        }
    }
}
