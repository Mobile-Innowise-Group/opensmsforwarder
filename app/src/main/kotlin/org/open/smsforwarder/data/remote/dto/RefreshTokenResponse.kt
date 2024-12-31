package org.open.smsforwarder.data.remote.dto

import com.squareup.moshi.Json

data class RefreshTokenResponse(
    @Json(name = "access_token")
    val accessToken: String,
)
