package org.open.smsforwarder.domain.usecase

import org.open.smsforwarder.R
import org.open.smsforwarder.domain.PhoneValidator
import org.open.smsforwarder.domain.ValidationResult
import org.open.smsforwarder.utils.Resources
import javax.inject.Inject

class ValidatePhoneUseCase @Inject constructor(
    private val phoneValidator: PhoneValidator
) {

    fun execute(phone: String): ValidationResult =
        if (!phoneValidator.isValid(phone)) {
            ValidationResult(
                successful = false,
                errorMessage = Resources.StringResource(R.string.error_phone_number_is_not_valid)
            )
        } else {
            ValidationResult(
                successful = true
            )
        }
}
