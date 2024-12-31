package org.open.smsforwarder.data.remote.dto

import com.squareup.moshi.Json

data class AuthCodeExchangeResponse(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "refresh_token")
    val refreshToken: String,
)
