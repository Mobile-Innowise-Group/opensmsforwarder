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
import org.open.smsforwarder.processing.worker.ForwardingWorker

class SmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (isSmsReceived(intent.action)) {
            val smsParts = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val smsMessages = smsParts.map { it.messageBody }
            val senders = smsParts.map { it.originatingAddress.orEmpty() }
            if (smsMessages.isNotEmpty() && smsMessages.size == senders.size) {
                startProcessing(context, smsMessages, senders)
            }
        }
    }

    private fun startProcessing(
        context: Context,
        smsMessages: List<String>,
        senders: List<String>,
    ) {
        val workRequestBuilder: OneTimeWorkRequest.Builder =
            OneTimeWorkRequest.Builder(ForwardingWorker::class.java)

        val data = Data.Builder()
            .putStringArray(MESSAGES_KEY, smsMessages.toTypedArray())
            .putStringArray(SENDERS_KEY, senders.toTypedArray())
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
        const val SENDERS_KEY = "SMS_SENDER_KEY"
    }
}
