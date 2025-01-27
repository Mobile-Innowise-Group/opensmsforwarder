package org.open.smsforwarder.domain.usecase

import org.open.smsforwarder.R
import org.open.smsforwarder.domain.ValidationResult
import org.open.smsforwarder.utils.Resources
import javax.inject.Inject

class ValidateTitleUseCase @Inject constructor() {

    fun execute(title: String): ValidationResult {
        return if (title.isNotEmpty() && title.trim().isEmpty()) {
            ValidationResult(
                successful = false,
                errorMessage = Resources.StringResource(R.string.error_title_is_not_valid)
            )
        } else {
            ValidationResult(
                successful = true
            )
        }
    }
}
