package org.open.smsforwarder.domain.usecase

import org.open.smsforwarder.domain.PhoneValidator
import org.open.smsforwarder.domain.ValidationError
import org.open.smsforwarder.domain.ValidationResult
import javax.inject.Inject

class ValidatePhoneUseCase @Inject constructor(
    private val phoneValidator: PhoneValidator
) {

    fun execute(phone: String): ValidationResult =
        if (!phoneValidator.isValid(phone)) {
            ValidationResult(
                successful = false,
                errorType = ValidationError.INVALID_PHONE
            )
        } else {
            ValidationResult(
                successful = true
            )
        }
}
