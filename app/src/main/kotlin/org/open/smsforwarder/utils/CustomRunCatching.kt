package org.open.smsforwarder.utils

import kotlinx.coroutines.CancellationException

/**
 * Calls the specified function block with this value as its receiver and returns
 * its encapsulated result if invocation was successful,
 * catching [CancellationException] and rethrow it
 * catch any Throwable exception, excluding [CancellationException], that was thrown from the
 * block function execution and encapsulating it as a failure.
 */
suspend fun <T, R> T.runCatchingWithCancellation(block: suspend T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
