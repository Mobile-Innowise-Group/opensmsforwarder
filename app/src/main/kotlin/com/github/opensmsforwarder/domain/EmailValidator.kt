package com.github.opensmsforwarder.domain

interface EmailValidator {
    fun isValid(email: String): Boolean
}