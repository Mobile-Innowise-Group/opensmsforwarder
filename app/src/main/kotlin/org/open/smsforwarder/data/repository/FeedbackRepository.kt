package org.open.smsforwarder.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import kotlinx.coroutines.suspendCancellableCoroutine
import org.open.smsforwarder.BuildConfig
import javax.inject.Inject
import kotlin.coroutines.resume

class FeedbackRepository @Inject constructor() {

    suspend fun sendFeedback(email: String, body: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val database = Firebase.database
                        val myRef = database.getReference(BuildConfig.FEEDBACK_DB_PATH)
                            .child(user?.uid ?: "")

                        val feedbackData = mapOf(EMAIL_KEY to email, BODY_KEY to body)
                        myRef.setValue(feedbackData)
                            .addOnCompleteListener { task ->
                                continuation.resume(task.isSuccessful)
                            }
                    } else {
                        continuation.resume(false)
                    }
                }
        }
    }


    private companion object {
        const val EMAIL_KEY = "email"
        const val BODY_KEY = "body"
    }
}