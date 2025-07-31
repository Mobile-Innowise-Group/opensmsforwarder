package org.open.smsforwarder.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import org.open.smsforwarder.BuildConfig
import javax.inject.Inject

class FeedbackRepository @Inject constructor() {

    suspend fun sendFeedback(email: String, body: String): Boolean {
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            auth.signInAnonymously().await()
        }

        val uid = auth.currentUser?.uid ?: return false

        val feedbackRef = FirebaseDatabase.getInstance()
            .getReference(BuildConfig.FEEDBACK_DB_PATH)
            .child(uid)

        val feedbackData = mapOf(
            EMAIL_KEY to email,
            BODY_KEY to body
        )

        feedbackRef.push().setValue(feedbackData).await()
        return true
    }

    private companion object {
        const val EMAIL_KEY = "email"
        const val BODY_KEY = "body"
    }
}
