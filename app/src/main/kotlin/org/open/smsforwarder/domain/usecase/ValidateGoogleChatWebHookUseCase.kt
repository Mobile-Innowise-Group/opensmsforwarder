package org.open.smsforwarder.domain.usecase

import org.open.smsforwarder.domain.GoogleChatWebHookValidator
import org.open.smsforwarder.domain.ValidationError
import org.open.smsforwarder.domain.ValidationResult
import javax.inject.Inject

class ValidateGoogleChatWebHookUseCase @Inject constructor(
    private val googleChatWebHookValidator: GoogleChatWebHookValidator
) {

    fun execute(webHook: String?): ValidationResult {
        if (webHook.isNullOrBlank()) {
            return ValidationResult(
                successful = false,
                errorType = ValidationError.BLANK_FIELD
            )
        }

        return if (googleChatWebHookValidator.isValid(webHook)) {
            ValidationResult(successful = true)
        } else {
            ValidationResult(
                successful = false,
                errorType = ValidationError.INVALID_GOOGLE_CHAT_WEB_HOOK
            )
        }
    }
}
