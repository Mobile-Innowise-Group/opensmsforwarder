package com.github.opensmsforwarder.di

import android.content.Context
import com.github.opensmsforwarder.BuildConfig
import com.github.opensmsforwarder.helper.GoogleSignInHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.gmail.GmailScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class GoogleSignInModule {

    @Provides
    fun provideGoogleSignInOptions(): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(BuildConfig.CLIENT_ID, true)
            .requestScopes(Scope(GmailScopes.GMAIL_SEND))
            .requestEmail()
            .build()

    @Provides
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        googleSignInOptions: GoogleSignInOptions,
    ): GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    @Provides
    fun provideSignInHelper(
        googleSignInClient: GoogleSignInClient,
    ): GoogleSignInHelper = GoogleSignInHelper(googleSignInClient)
}
