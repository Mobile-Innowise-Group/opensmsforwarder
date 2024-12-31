package org.open.smsforwarder.domain

interface PhoneValidator {
    fun isValid(phoneNumber: String): Boolean
}
