package org.open.smsforwarder.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import javax.inject.Inject

class FeedbackRepository @Inject constructor() {
    fun sendFeedback(email: String, body: String, callback: (Boolean) -> Unit) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val database = Firebase.database
                    val myRef = database.getReference(DB_PATH).child(user?.uid ?: "")

                    val feedbackData = mapOf(EMAIL_FIELD_NAME to email, BODY_FIELD_NAME to body)
                    myRef.setValue(feedbackData)
                        .addOnCompleteListener { task ->
                            callback(task.isSuccessful)
                        }
                } else {
                    callback(false)
                }
            }
    }

    companion object {
        const val DB_PATH = "feedback"
        const val EMAIL_FIELD_NAME = "email"
        const val BODY_FIELD_NAME = "body"
    }
}