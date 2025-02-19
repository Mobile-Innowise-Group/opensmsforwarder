package org.open.smsforwarder.domain

import org.open.smsforwarder.utils.Resources

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: Resources.StringProvider? = null
)
