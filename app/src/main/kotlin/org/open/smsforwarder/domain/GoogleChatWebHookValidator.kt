package org.open.smsforwarder.domain

interface GoogleChatWebHookValidator {
    fun isValid(webHook: String): Boolean
}
