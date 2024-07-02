package com.github.opensmsforwarder.data.remote.interceptor

import com.github.opensmsforwarder.data.local.database.dao.AuthTokenDao
import com.github.opensmsforwarder.data.local.database.entity.AuthTokenEntity
import com.github.opensmsforwarder.data.remote.interceptor.AuthInterceptor.Companion.AUTHORIZATION_HEADER
import com.github.opensmsforwarder.data.remote.interceptor.AuthInterceptor.Companion.TOKEN_TYPE
import com.github.opensmsforwarder.data.remote.service.AuthService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authTokenDao: AuthTokenDao,
    private val authService: AuthService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request {
        synchronized(this) {
            var token: String? = null
            runBlocking {
                val recipientId: Long = response.request.header(AuthInterceptor.ID)?.toLong()
                    ?: throw RecipientIdNotFoundException()

                val authTokenEntity = authTokenDao.getAuthToken(recipientId)
                    ?: throw AuthTokenException()

                val refreshToken = authTokenEntity.refreshToken
                    ?: throw RefreshTokenException()

                token = if (isRefreshNeed(response, authTokenEntity)) {
                    try {
                        val newToken =
                            authService.refreshToken(refreshToken = refreshToken).accessToken
                        authTokenDao.upsertAuthToken(authTokenEntity.copy(accessToken = newToken))
                        newToken
                    } catch (httpException: HttpException) {
                        throw TokenRevokedException()
                    } catch (ioException: IOException) {
                        throw RefreshTokenException()
                    }
                } else {
                    authTokenEntity.accessToken
                }
            }
            return response
                .request
                .newBuilder()
                .header(AUTHORIZATION_HEADER, "$TOKEN_TYPE $token")
                .build()
        }
    }

    private fun isRefreshNeed(response: Response, authTokenEntity: AuthTokenEntity): Boolean {
        val oldToken: String? =
            response.request.header(AUTHORIZATION_HEADER)?.replace("$TOKEN_TYPE ", "")
        val newToken: String? = authTokenEntity.accessToken
        return oldToken == newToken
    }
}

class TokenRevokedException : IOException()
class AuthTokenException : IOException()
class RefreshTokenException : IOException()
class RecipientIdNotFoundException : IOException()
