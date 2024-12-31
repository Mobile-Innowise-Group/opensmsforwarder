package com.github.opensmsforwarder.processing.starter

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.processing.processor.ForwardingProcessor
import com.github.opensmsforwarder.processing.reciever.SmsBroadcastReceiver
import com.github.opensmsforwarder.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForwardingService : Service() {

    @Inject
    lateinit var forwardingProcessor: ForwardingProcessor

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        NotificationHelper.createNotificationChannel(this)
        val notification: Notification = NotificationHelper.createDefaultNotificationBuilder(this)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.forwarding_sms))
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)

        intent
            ?.getStringArrayExtra(SmsBroadcastReceiver.MESSAGES_KEY)
            ?.let(::processMessages)
            ?: stopSelf()

        return START_REDELIVER_INTENT
    }

    private fun processMessages(messages: Array<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            forwardingProcessor.process(messages)
            stopSelf()
        }
    }

    companion object {
        const val ONGOING_NOTIFICATION_ID = 1001
    }
}