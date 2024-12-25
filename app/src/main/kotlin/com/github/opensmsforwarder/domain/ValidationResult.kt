package com.github.opensmsforwarder.domain

import com.github.opensmsforwarder.utils.Resources

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: Resources.StringProvider? = null
)