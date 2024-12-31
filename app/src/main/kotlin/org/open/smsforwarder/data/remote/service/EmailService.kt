package org.open.smsforwarder.data.remote.service

import okhttp3.ResponseBody
import org.open.smsforwarder.data.remote.interceptor.AuthInterceptor
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface EmailService {

    @POST("gmail/v1/users/me/messages/send")
    suspend fun sendEmail(
        @Header(AuthInterceptor.ID) id: Long,
        @Body rawBody: Map<String, String>,
    ): ResponseBody
}
