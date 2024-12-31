package org.open.smsforwarder.domain

interface EmailValidator {
    fun isValid(email: String): Boolean
}
