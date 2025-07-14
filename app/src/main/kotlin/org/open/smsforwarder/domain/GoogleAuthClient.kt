package org.open.smsforwarder.domain

import android.content.Context
import android.content.Intent
import org.open.smsforwarder.data.remote.dto.SignInResult

interface GoogleAuthClient {
    suspend fun getSignInIntent(context: Context): SignInResult
    fun extractAuthorizationCode(data: Intent?): String
}
