package org.open.smsforwarder.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.open.smsforwarder.BuildConfig

@Module
@InstallIn(FragmentComponent::class)
class GoogleSignInModule {

    @Provides
    fun provideGoogleSignInOptions(): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(BuildConfig.CLIENT_ID, true)
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/gmail.send"))
            .build()

    @Provides
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        googleSignInOptions: GoogleSignInOptions
    ): GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

}
