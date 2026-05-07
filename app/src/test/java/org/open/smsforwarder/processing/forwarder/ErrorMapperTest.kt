package org.open.smsforwarder.processing.forwarder

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.open.smsforwarder.data.remote.interceptor.AuthTokenException
import org.open.smsforwarder.data.remote.interceptor.RefreshTokenException
import org.open.smsforwarder.data.remote.interceptor.TokenRevokedException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ErrorMapperTest {

    private val mapper = ForwardingErrorMapper()

    @Test
    fun `maps token revoked to auth revoked result`() {
        val result = mapper.map(TokenRevokedException())

        assertTrue(result is ForwardingResult.AuthRevoked)
    }

    @Test
    fun `maps refresh token exception to retryable failure`() {
        val result = mapper.map(RefreshTokenException())

        assertTrue(result is ForwardingResult.RetryableFailure)
    }

    @Test
    fun `maps auth token exception to auth unavailable`() {
        val result = mapper.map(AuthTokenException())

        assertTrue(result is ForwardingResult.AuthUnavailable)
    }

    @Test
    fun `maps io exception to retryable failure`() {
        val result = mapper.map(IOException("network down"))

        assertTrue(result is ForwardingResult.RetryableFailure)
    }

    @Test
    fun `maps http 429 to retryable failure`() {
        val response = Response.error<Unit>(429, "{}".toResponseBody(null))
        val result = mapper.map(HttpException(response))

        assertTrue(result is ForwardingResult.RetryableFailure)
    }

    @Test
    fun `maps http 400 to permanent failure`() {
        val response = Response.error<Unit>(400, "{}".toResponseBody(null))
        val result = mapper.map(HttpException(response))

        assertTrue(result is ForwardingResult.PermanentFailure)
    }
}
