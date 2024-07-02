package com.github.opensmsforwarder.data.remote.service

import com.github.opensmsforwarder.BuildConfig
import com.github.opensmsforwarder.data.remote.dto.AuthCodeExchangeResponse
import com.github.opensmsforwarder.data.remote.dto.GrantType
import com.github.opensmsforwarder.data.remote.dto.RefreshTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    @POST("token")
    @FormUrlEncoded
    suspend fun exchangeAuthCodeForTokens(
        @Field("code") code: String?,
        @Field("grant_type") grantType: String = GrantType.AUTHORIZATION_CODE.value,
        @Field("redirect_uri") redirectUri: String = BuildConfig.REDIRECT_URI,
        @Field("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Field("client_secret") clientSecret: String = BuildConfig.CLIENT_SECRET,
    ): AuthCodeExchangeResponse

    @POST("token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = GrantType.REFRESH_TOKEN.value,
        @Field("redirect_uri") redirectUri: String = BuildConfig.REDIRECT_URI,
        @Field("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Field("client_secret") clientSecret: String = BuildConfig.CLIENT_SECRET,
    ): RefreshTokenResponse
}
