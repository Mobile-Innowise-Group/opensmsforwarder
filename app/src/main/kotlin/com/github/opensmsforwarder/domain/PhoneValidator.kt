package com.github.opensmsforwarder.domain

interface PhoneValidator {
    fun isValid(phoneNumber: String): Boolean
}
