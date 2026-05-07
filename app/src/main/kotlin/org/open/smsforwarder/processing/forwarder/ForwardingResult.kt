package org.open.smsforwarder.processing.forwarder

sealed interface ForwardingResult {
    data object Success : ForwardingResult
    data class RetryableFailure(val errorMessage: String) : ForwardingResult
    data class PermanentFailure(val errorMessage: String) : ForwardingResult
    data class AuthUnavailable(val errorMessage: String) : ForwardingResult
    data class AuthRevoked(val errorMessage: String) : ForwardingResult
}
