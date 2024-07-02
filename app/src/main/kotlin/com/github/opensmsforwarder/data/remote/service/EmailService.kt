package com.github.opensmsforwarder.data.remote.service

import com.github.opensmsforwarder.data.remote.interceptor.AuthInterceptor
import okhttp3.ResponseBody
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
