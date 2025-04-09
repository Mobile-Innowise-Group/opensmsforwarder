package org.open.smsforwarder.di

import android.content.Context
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.gmail.GmailScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.open.smsforwarder.BuildConfig

@Module
@InstallIn(FragmentComponent::class)
class AuthorizationModule {

    @Provides
    fun provideAuthorizationRequest(): AuthorizationRequest =
        AuthorizationRequest
            .builder()
            .setRequestedScopes(listOf(Scope(GmailScopes.GMAIL_SEND)))
            .requestOfflineAccess(BuildConfig.CLIENT_ID, true)
            .build()

    @Provides
    fun provideAuthorizationResult(
        @ApplicationContext context: Context,
        request: AuthorizationRequest
    ): Task<AuthorizationResult> =
        Identity.getAuthorizationClient(context)
            .authorize(request)

}