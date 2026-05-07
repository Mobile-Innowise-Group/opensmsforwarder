package org.open.smsforwarder.processing.forwarder

import org.open.smsforwarder.data.remote.interceptor.AuthTokenException
import org.open.smsforwarder.data.remote.interceptor.RecipientIdNotFoundException
import org.open.smsforwarder.data.remote.interceptor.RefreshTokenException
import org.open.smsforwarder.data.remote.interceptor.TokenRevokedException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ErrorMapper @Inject constructor() {
    fun map(error: Throwable): ForwardingResult =
        when (error) {
            is TokenRevokedException -> ForwardingResult.AuthRevoked(error.message.orEmpty())
            is RefreshTokenException -> ForwardingResult.RetryableFailure(error.message.orEmpty())
            is AuthTokenException, is RecipientIdNotFoundException ->
                ForwardingResult.AuthUnavailable(error.message.orEmpty())

            is HttpException -> mapHttpException(error)
            is IOException -> ForwardingResult.RetryableFailure(error.message.orEmpty())
            else -> ForwardingResult.PermanentFailure(error.message.orEmpty())
        }

    private fun mapHttpException(error: HttpException): ForwardingResult {
        val code = error.code()
        return when {
            code == HTTP_UNAUTHORIZED -> ForwardingResult.AuthRevoked(error.message.orEmpty())
            code == HTTP_TOO_MANY_REQUESTS || code in SERVER_ERROR_RANGE ->
                ForwardingResult.RetryableFailure(error.message.orEmpty())

            else -> ForwardingResult.PermanentFailure(error.message.orEmpty())
        }
    }

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_TOO_MANY_REQUESTS = 429
        val SERVER_ERROR_RANGE = 500..599
    }
}
