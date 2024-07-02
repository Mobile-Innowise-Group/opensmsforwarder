package com.github.opensmsforwarder.data.remote.dto

enum class GrantType(val value: String) {
    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token")
}
