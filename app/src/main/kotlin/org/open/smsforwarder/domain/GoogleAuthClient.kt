package org.open.smsforwarder.domain

import android.content.Context
import android.content.Intent
import org.open.smsforwarder.data.remote.dto.GoogleSignInResult

interface GoogleAuthClient {
    suspend fun getSignInIntent(context: Context): GoogleSignInResult
    fun extractAuthorizationCode(data: Intent?): String
}
