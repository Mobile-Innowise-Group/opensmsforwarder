package org.open.smsforwarder.processing.composer

import com.google.api.services.gmail.model.Message
import org.apache.commons.codec.binary.Base64
import javax.inject.Inject

class EmailComposer @Inject constructor() {

    fun composeMessage(
        toEmailAddress: String,
        subject: String? = null,
        messageBody: String? = null
    ): String {
        val rawMessage = buildString {
            append("To: $toEmailAddress\r\n")
            append("Subject: ${subject ?: "No subject"}\r\n")
            append("Content-Type: text/plain; charset=\"UTF-8\"\r\n")
            append("\r\n")
            append(messageBody ?: "")
        }
        return Base64.encodeBase64URLSafeString(rawMessage.toByteArray(Charsets.UTF_8))
    }
}
