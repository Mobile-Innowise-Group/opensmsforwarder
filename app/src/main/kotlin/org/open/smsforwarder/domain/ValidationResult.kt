package org.open.smsforwarder.domain

data class ValidationResult(
    val successful: Boolean,
    val errorType: ValidationError? = null
)
