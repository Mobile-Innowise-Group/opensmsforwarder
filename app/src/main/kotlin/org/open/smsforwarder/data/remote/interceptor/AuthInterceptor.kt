package org.open.smsforwarder.data.remote.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.open.smsforwarder.data.local.database.dao.AuthTokenDao
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authTokenDao: AuthTokenDao,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        runBlocking {
            val modifiedRequest = chain
                .request()
                .newBuilder()
                .addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)

            val recipientId = chain.request().header(ID)?.toLong()
                ?: throw RecipientIdNotFoundException()

            val accessToken = authTokenDao.getAuthToken(recipientId)?.accessToken

            if (accessToken != null) {
                modifiedRequest.addHeader(AUTHORIZATION_HEADER, "$TOKEN_TYPE $accessToken")
            }

            chain.proceed(modifiedRequest.build())
        }

    companion object {
        const val CONTENT_TYPE_HEADER = "Content-Type"
        const val CONTENT_TYPE_HEADER_VALUE = "application/json"
        const val AUTHORIZATION_HEADER = "Authorization"
        const val TOKEN_TYPE = "Bearer"
        const val ID = "id"
    }
}
