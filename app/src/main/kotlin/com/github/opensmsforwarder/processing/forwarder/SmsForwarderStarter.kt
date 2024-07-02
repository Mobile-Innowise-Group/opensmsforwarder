package com.github.opensmsforwarder.processing.forwarder

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.opensmsforwarder.processing.reciever.SmsBroadcastReceiver.Companion.MESSAGES_KEY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SmsForwarderStarter @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        inputData.getStringArray(MESSAGES_KEY)?.let { messages ->
            val intent = Intent(context, SmsForwarderService::class.java)
            intent.putExtra(MESSAGES_KEY, messages)
            context.startForegroundService(intent)
        }
        return Result.success()
    }
}
