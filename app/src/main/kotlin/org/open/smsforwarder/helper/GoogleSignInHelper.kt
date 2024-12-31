package org.open.smsforwarder.helper

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleSignInHelper @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
) {

    val signInIntent: Intent
        get() {
            // Call googleSignInClient.signOut() before getting signInIntent every time auth flow starts.
            // Otherwise last logged in account will be used as default. "Choose an account" dialog will not be shown
            googleSignInClient.signOut()
            return googleSignInClient.signInIntent
        }

    suspend fun getGoogleSignInAccount(data: Intent?): Result<GoogleSignInAccount> =
        runCatching {
            withContext(Dispatchers.IO) {
                Tasks.await(GoogleSignIn.getSignedInAccountFromIntent(data))
            }
        }
}
