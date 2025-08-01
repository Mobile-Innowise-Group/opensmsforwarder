package org.open.smsforwarder.data.remote.service

import org.open.smsforwarder.BuildConfig
import org.open.smsforwarder.data.remote.dto.AuthCodeExchangeResponse
import org.open.smsforwarder.data.remote.dto.GrantType
import org.open.smsforwarder.data.remote.dto.RefreshTokenResponse
import retrofit2.Response
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

    @FormUrlEncoded
    @POST("revoke")
    suspend fun revokeToken(
        @Field("token") token: String?
    ): Response<Unit>
}
