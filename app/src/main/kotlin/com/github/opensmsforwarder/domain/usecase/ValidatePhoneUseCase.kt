package com.github.opensmsforwarder.domain.usecase

import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.domain.PhoneValidator
import com.github.opensmsforwarder.utils.Resources
import com.github.opensmsforwarder.domain.ValidationResult
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
