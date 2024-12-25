package com.github.opensmsforwarder.processing.forwarder

import android.content.Context
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.github.opensmsforwarder.domain.model.Forwarding
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SmsForwarder @Inject constructor(
    @ApplicationContext private val context: Context,
) : Forwarder {

    override suspend fun execute(forwarding: Forwarding, message: String): Result<Unit> =
        runCatching {
            val smsManager: SmsManager? = ContextCompat.getSystemService(
                context,
                SmsManager::class.java
            )
            smsManager?.sendTextMessage(forwarding.recipientPhone, null, message, null, null)
        }
}
