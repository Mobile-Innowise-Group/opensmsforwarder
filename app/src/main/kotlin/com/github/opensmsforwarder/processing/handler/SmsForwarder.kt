package com.github.opensmsforwarder.processing.handler

import android.content.Context
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.github.opensmsforwarder.model.Recipient
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SmsForwarder @Inject constructor(
    @ApplicationContext private val context: Context,
) : Forwarder {

    override suspend fun execute(recipient: Recipient, message: String): Result<Unit> =
        runCatching {
            val smsManager: SmsManager? = ContextCompat.getSystemService(
                context,
                SmsManager::class.java
            )
            smsManager?.sendTextMessage(recipient.recipientPhone, null, message, null, null)
        }
}
