package org.open.smsforwarder.data.remote.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okio.IOException
import org.open.smsforwarder.data.local.database.dao.AuthTokenDao
import org.open.smsforwarder.data.remote.interceptor.AuthInterceptor.Companion.AUTHORIZATION_HEADER
import org.open.smsforwarder.data.remote.interceptor.AuthInterceptor.Companion.TOKEN_TYPE
import org.open.smsforwarder.data.remote.service.AuthService
import org.open.smsforwarder.data.security.DataCipher
import retrofit2.HttpException
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authTokenDao: AuthTokenDao,
    private val authService: AuthService,
    private val dataCipher: DataCipher,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request {
        synchronized(this) {
            var token: String? = null
            runBlocking {
                val recipientId: Long = response.request.header(AuthInterceptor.ID)?.toLong()
                    ?: throw RecipientIdNotFoundException()

                val authTokenEntity = authTokenDao.getAuthToken(recipientId)
                    ?: throw AuthTokenException()

                val refreshToken = dataCipher.decrypt(authTokenEntity.refreshToken)
                    ?: throw RefreshTokenException()
                val currentAccessToken = dataCipher.decrypt(authTokenEntity.accessToken)

                token = if (isRefreshNeed(response, currentAccessToken)) {
                    try {
                        val newToken =
                            authService.refreshToken(refreshToken = refreshToken).accessToken
                        authTokenDao.upsertAuthToken(
                            authTokenEntity.copy(
                                accessToken = dataCipher.encrypt(newToken)
                            )
                        )
                        newToken
                    } catch (httpException: HttpException) {
                        throw TokenRevokedException()
                    } catch (ioException: IOException) {
                        throw RefreshTokenException()
                    }
                } else {
                    currentAccessToken
                }
            }
            return response
                .request
                .newBuilder()
                .header(AUTHORIZATION_HEADER, "$TOKEN_TYPE $token")
                .build()
        }
    }

    private fun isRefreshNeed(response: Response, currentAccessToken: String?): Boolean {
        val oldToken: String? =
            response.request.header(AUTHORIZATION_HEADER)?.replace("$TOKEN_TYPE ", "")
        return oldToken == currentAccessToken
    }
}

class TokenRevokedException : IOException()
class AuthTokenException : IOException()
class RefreshTokenException : IOException()
class RecipientIdNotFoundException : IOException()
