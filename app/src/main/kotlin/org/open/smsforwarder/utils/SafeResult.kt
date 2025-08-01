package org.open.smsforwarder.utils

import kotlin.coroutines.cancellation.CancellationException

/**
 * Calls the specified function block with this value as its receiver and returns
 * its encapsulated result if invocation was successful,
 * catching [CancellationException] and rethrow it
 * catch any Throwable exception, excluding [CancellationException], that was thrown from the
 * block function execution and encapsulating it as a failure.
 */
@Suppress("TooGenericExceptionCaught")
suspend fun <R> runSuspendCatching(block: suspend () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}

@Suppress("TooGenericExceptionCaught")
inline fun <R, T> Result<T>.mapSuspendCatching(transform: (value: T) -> R): Result<R> {
    return if (isSuccess) {
        try {
            Result.success(transform(getOrThrow()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    } else {
        Result.failure(exceptionOrNull() ?: IllegalStateException("Unknown failure"))
    }
}
