package org.open.smsforwarder.data.remote.service

import org.open.smsforwarder.data.remote.dto.ChatMessage
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface GoogleChatService {
    @POST
    suspend fun sendMessage(
        @Url webHookUrl: String,
        @Body message: ChatMessage
    )
}
