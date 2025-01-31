package org.open.smsforwarder.processing.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import org.open.smsforwarder.extension.notificationsPermissionGranted
import org.open.smsforwarder.processing.starter.ForwardingStarter

class SmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!context.notificationsPermissionGranted()) return

        if (isSmsReceived(intent.action)) {
            val smsMessages =
                Telephony.Sms.Intents.getMessagesFromIntent(intent).map { it.messageBody }
            if (smsMessages.isNotEmpty()) {
                startProcessing(context, smsMessages)
            }
        }
    }

    private fun startProcessing(context: Context, smsMessages: List<String>) {
        val workRequestBuilder: OneTimeWorkRequest.Builder =
            OneTimeWorkRequest.Builder(ForwardingStarter::class.java)

        val data = Data.Builder()
            .putStringArray(MESSAGES_KEY, smsMessages.toTypedArray())
            .build()

        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = workRequestBuilder
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun isSmsReceived(action: String?): Boolean =
        action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION

    companion object {
        const val MESSAGES_KEY = "SMS_BODY_KEY"
    }
}
