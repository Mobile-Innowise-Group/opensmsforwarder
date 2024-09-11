package com.github.opensmsforwarder.processing.forwarder

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.data.AuthRepository
import com.github.opensmsforwarder.data.ForwardingHistoryRepository
import com.github.opensmsforwarder.data.RecipientsRepository
import com.github.opensmsforwarder.data.RulesRepository
import com.github.opensmsforwarder.data.remote.interceptor.RefreshTokenException
import com.github.opensmsforwarder.data.remote.interceptor.TokenRevokedException
import com.github.opensmsforwarder.data.remote.service.EmailService
import com.github.opensmsforwarder.model.ForwardingType
import com.github.opensmsforwarder.model.Recipient
import com.github.opensmsforwarder.processing.composer.EmailComposer
import com.github.opensmsforwarder.utils.NotificationUtils.createBaseNotificationBuilder
import com.github.opensmsforwarder.utils.NotificationUtils.createNotificationChannel
import com.github.opensmsforwarder.processing.reciever.SmsBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsForwarderService : Service() {

    @Inject
    lateinit var recipientsRepository: RecipientsRepository

    @Inject
    lateinit var ruleRepository: RulesRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var forwardingHistoryRepository: ForwardingHistoryRepository

    @Inject
    lateinit var emailComposer: EmailComposer

    @Inject
    lateinit var emailService: EmailService

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification: Notification = createBaseNotificationBuilder(this)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.processing))
            .build()
        startForeground(1, notification)

        intent?.getStringArrayExtra(SmsBroadcastReceiver.MESSAGES_KEY)?.let { messages ->
            processMessages(messages)
        } ?: stopSelf()

        return START_NOT_STICKY
    }

    private fun processMessages(messages: Array<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val recipientMessages = mutableListOf<Pair<Long, String>>()
            val rules = ruleRepository.getRules()

            if (rules.isEmpty()) {
                stopSelf()
                return@launch
            }

            messages.forEach { message ->
                rules.forEach { rule ->
                    if (message.contains(rule.textRule)) {
                        recipientMessages.add(rule.recipientId to message)
                    }
                }
            }

            if (recipientMessages.isEmpty()) {
                stopSelf()
                return@launch
            }

            recipientMessages.forEach { (recipientId, message) ->
                recipientsRepository.getRecipientById(recipientId)?.let { recipient ->
                    when (recipient.forwardingType) {
                        ForwardingType.SMS -> runCatching {
                            forwardSmsToPhone(recipient.recipientPhone, message)
                        }.onSuccess {
                            recipientsRepository.insertOrUpdateRecipient(
                                recipient.copy(isForwardSuccessful = true)
                            )
                            forwardingHistoryRepository.upsertForwardedSms(recipient.id, message, true)
                        }.onFailure {
                            recipientsRepository.insertOrUpdateRecipient(
                                recipient.copy(isForwardSuccessful = false)
                            )
                            forwardingHistoryRepository.upsertForwardedSms(recipient.id, message, false)
                        }

                        ForwardingType.EMAIL -> runCatching {
                            forwardSmsToEmail(recipient, message)
                        }.onSuccess {
                            recipientsRepository.insertOrUpdateRecipient(
                                recipient.copy(isForwardSuccessful = true)
                            )
                            forwardingHistoryRepository.upsertForwardedSms(recipient.id, message, true)
                        }.onFailure { error ->
                            recipientsRepository.insertOrUpdateRecipient(
                                recipient.copy(isForwardSuccessful = false)
                            )
                            forwardingHistoryRepository.upsertForwardedSms(recipient.id, message, false)
                            if (error is TokenRevokedException || error is RefreshTokenException) {
                                authRepository.signOut(recipient)
                            }
                        }

                        else -> {
                            stopSelf()
                            return@launch
                        }
                    }
                }
            }
            stopSelf()
        }
    }

    private suspend fun forwardSmsToEmail(recipient: Recipient, message: String) {
        if (recipient.senderEmail != null) {
            val emailMessage = emailComposer.composeMessage(
                toEmailAddress = recipient.recipientEmail,
                subject = DEFAULT_SUBJECT,
                messageBody = message
            )
            emailService.sendEmail(
                id = recipient.id,
                rawBody = hashMapOf(SEND_FORMAT to emailMessage)
            )
        }
    }

    private fun forwardSmsToPhone(to: String, sms: String) {
        val smsManager: SmsManager? = ContextCompat.getSystemService(
            applicationContext,
            SmsManager::class.java
        )
        smsManager?.sendTextMessage(to, null, sms, null, null)
    }

    private companion object {
        const val DEFAULT_SUBJECT = "Forwarded SMS"
        const val SEND_FORMAT = "raw"
    }
}
