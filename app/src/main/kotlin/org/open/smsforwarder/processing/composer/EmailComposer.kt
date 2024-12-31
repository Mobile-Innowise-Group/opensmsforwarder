package org.open.smsforwarder.processing.composer

import com.google.api.services.gmail.model.Message
import org.apache.commons.codec.binary.Base64
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.inject.Inject
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailComposer @Inject constructor() {

    fun composeMessage(
        toEmailAddress: String,
        subject: String? = null,
        messageBody: String? = null
    ): String {
        val emailContent = createEmailContent(
            toEmailAddress = toEmailAddress,
            subject = subject,
            bodyText = messageBody
        )
        return createRawGmailMessage(emailContent).raw
    }

    @Throws(MessagingException::class)
    private fun createEmailContent(
        toEmailAddress: String?,
        subject: String?,
        bodyText: String?
    ): MimeMessage {
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)
        val email = MimeMessage(session)
        email.addRecipient(
            javax.mail.Message.RecipientType.TO,
            InternetAddress(toEmailAddress)
        )
        email.subject = subject
        email.setText(bodyText)
        return email
    }

    @Throws(MessagingException::class)
    private fun createRawGmailMessage(emailContent: MimeMessage): Message {
        val buffer = ByteArrayOutputStream()
        emailContent.writeTo(buffer)
        val bytes = buffer.toByteArray()
        val encodedEmail: String = Base64.encodeBase64URLSafeString(bytes)
        val message = Message()
        message.setRaw(encodedEmail)
        return message
    }
}
