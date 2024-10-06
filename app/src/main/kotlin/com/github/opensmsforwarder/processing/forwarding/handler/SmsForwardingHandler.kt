package com.github.opensmsforwarder.processing.forwarding.handler

import android.content.Context
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.github.opensmsforwarder.model.Recipient
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SmsForwardingHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ForwardingHandler {

    override suspend fun run(recipient: Recipient, message: String): Result<Unit> =
        runCatching {
            forwardSmsViaPhone(recipient.recipientPhone, message)
        }

    private fun forwardSmsViaPhone(to: String, sms: String) {
        val smsManager: SmsManager? = ContextCompat.getSystemService(
            context,
            SmsManager::class.java
        )
        smsManager?.sendTextMessage(to, null, sms, null, null)
    }
}