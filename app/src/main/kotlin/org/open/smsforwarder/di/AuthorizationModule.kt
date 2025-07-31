package org.open.smsforwarder.di

import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.api.services.gmail.GmailScopes
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.open.smsforwarder.BuildConfig
import org.open.smsforwarder.data.repository.IdTokenParserImpl
import org.open.smsforwarder.domain.IdTokenParser
import org.open.smsforwarder.platform.CredentialClientWrapper
import org.open.smsforwarder.platform.CredentialClientWrapperImpl
import org.open.smsforwarder.platform.IdentityClientWrapper
import org.open.smsforwarder.platform.IdentityClientWrapperImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthorizationModule {

    @Binds
    abstract fun bindIdTokenParser(idTokenParserImpl: IdTokenParserImpl): IdTokenParser

    @Binds
    abstract fun bindCredentialClientWrapper(
        credentialManagerWrapperImpl: CredentialClientWrapperImpl
    ): CredentialClientWrapper

    @Binds
    abstract fun bindIdentityClientWrapper(
        identityClientWrapperImpl: IdentityClientWrapperImpl
    ): IdentityClientWrapper


    companion object {

        @Provides
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
        fun provideGetCredentialRequest(): GetCredentialRequest {
            val googleOption = GetSignInWithGoogleOption
                .Builder(BuildConfig.CLIENT_ID)
                .build()

            return GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build()
        }
    }
}
