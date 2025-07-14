package org.open.smsforwarder.di

import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.api.services.gmail.GmailScopes
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.BuildConfig
import org.open.smsforwarder.data.repository.GoogleAuthClientImpl
import org.open.smsforwarder.data.repository.IdTokenParserImpl
import org.open.smsforwarder.domain.GoogleAuthClient
import org.open.smsforwarder.domain.IdTokenParser
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthorizationModule {

    @Binds
    @Singleton
    abstract fun bindGoogleAuthClient(googleAuthClientImpl: GoogleAuthClientImpl): GoogleAuthClient

    @Binds
    @Singleton
    abstract fun bindIdTokenParser(idTokenParserImpl: IdTokenParserImpl): IdTokenParser


    companion object {

        @Provides
        @Singleton
        fun provideAuthorizationRequest(): AuthorizationRequest =
            AuthorizationRequest
                .Builder()
                .setRequestedScopes(
                    listOf(
                        Scope(GmailScopes.GMAIL_SEND),
                        Scope(Scopes.EMAIL),
                    )
                )
                .requestOfflineAccess(BuildConfig.CLIENT_ID, true)
                .build()

        @Provides
        @Singleton
        fun provideGetCredentialRequest(): GetCredentialRequest {
            val googleOption = GetGoogleIdOption.Builder()
                .setServerClientId(BuildConfig.CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            return GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build()
        }
    }
}
