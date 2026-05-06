package org.open.smsforwarder.processing.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.open.smsforwarder.R
import org.open.smsforwarder.data.repository.RulesRepository
import org.open.smsforwarder.processing.model.IncomingSms
import org.open.smsforwarder.processing.processor.ForwardingProcessor
import org.open.smsforwarder.processing.receiver.SmsBroadcastReceiver
import org.open.smsforwarder.utils.NotificationHelper

@HiltWorker
class ForwardingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val rulesRepository: RulesRepository,
    private val forwardingProcessor: ForwardingProcessor,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val messages = inputData.getStringArray(SmsBroadcastReceiver.Companion.MESSAGES_KEY)
        val senders = inputData.getStringArray(SmsBroadcastReceiver.Companion.SENDERS_KEY)

        val result = when {
            rulesRepository.getRules().isEmpty() -> Result.success()
            messages == null || senders == null || messages.size != senders.size -> Result.failure()
            else -> {
                val incomingMessages = messages.mapIndexed { index, message ->
                    IncomingSms(sender = senders[index], message = message)
                }
                setForeground(createForegroundInfo())
                forwardingProcessor.process(incomingMessages)
                Result.success()
            }
        }

        return result
    }

    private fun createForegroundInfo(): ForegroundInfo {
        NotificationHelper.createNotificationChannel(context)
        val notification: Notification =
            NotificationHelper.createDefaultNotificationBuilder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.forwarding_sms))
                .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(
                ONGOING_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING
            )
        } else {
            ForegroundInfo(ONGOING_NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val ONGOING_NOTIFICATION_ID = 1001
    }
}
