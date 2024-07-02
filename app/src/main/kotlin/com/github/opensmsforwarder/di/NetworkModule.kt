package com.github.opensmsforwarder.di

import com.github.opensmsforwarder.BuildConfig
import com.github.opensmsforwarder.data.local.database.dao.AuthTokenDao
import com.github.opensmsforwarder.data.remote.interceptor.AuthInterceptor
import com.github.opensmsforwarder.data.remote.interceptor.TokenAuthenticator
import com.github.opensmsforwarder.data.remote.service.AuthService
import com.github.opensmsforwarder.data.remote.service.EmailService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authTokenDao: AuthTokenDao,
    ) = AuthInterceptor(authTokenDao)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        authTokenDao: AuthTokenDao,
        authService: AuthService,
    ): TokenAuthenticator = TokenAuthenticator(authTokenDao, authService)

    @Provides
    @Singleton
    fun provideOkHttpClient(
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        })
        .build()

    @Provides
    @Singleton
    @Authorized
    fun provideAuthorizedOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient = OkHttpClient.Builder()
        .authenticator(tokenAuthenticator)
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        })
        .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Provides
    @Singleton
    fun provideAuthService(
        okHttpClient: OkHttpClient,
        moshiFactory: MoshiConverterFactory,
    ): AuthService =
        Retrofit
            .Builder()
            .addConverterFactory(moshiFactory)
            .client(okHttpClient)
            .baseUrl(BuildConfig.OAUTH_BASE_URL)
            .build()
            .create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideEmailService(
        @Authorized okHttpClient: OkHttpClient,
        moshiFactory: MoshiConverterFactory,
    ): EmailService =
        Retrofit
            .Builder()
            .addConverterFactory(moshiFactory)
            .client(okHttpClient)
            .baseUrl(BuildConfig.API_BASE_URL)
            .build()
            .create(EmailService::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Authorized
