package org.open.smsforwarder.di

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.open.smsforwarder.BuildConfig

@Module
@InstallIn(FragmentComponent::class)
class CredentialSignInModule {

    @Provides
    fun provideCredentialManagerOptions(): GetGoogleIdOption =
        GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .build()

    @Provides
    fun provideCredentialManagerRequest(googleIdOption: GetGoogleIdOption): GetCredentialRequest =
        GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

    @Provides
    fun provideCredentialManager(
        @ApplicationContext context: Context,
    ): CredentialManager = CredentialManager.create(context)

}