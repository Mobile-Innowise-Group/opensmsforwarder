package org.open.smsforwarder.data.remote.service

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TelegramService {
    @GET("/bot{apiToken}/sendMessage")
    suspend fun sendMessage(
        @Path("apiToken", encoded = true) apiToken: String,
        @Query("chat_id") chatId: String,
        @Query("text") text: String
    ): ResponseBody
}
