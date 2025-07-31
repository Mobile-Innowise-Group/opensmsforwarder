package org.open.smsforwarder.domain

interface IdTokenParser {
    fun extractEmail(idToken: String): String?
}
