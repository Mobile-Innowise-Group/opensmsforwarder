package org.open.smsforwarder.processing.starter

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.open.smsforwarder.data.repository.RulesRepository
import org.open.smsforwarder.processing.reciever.SmsBroadcastReceiver.Companion.MESSAGES_KEY

@HiltWorker
class ForwardingStarter @AssistedInject constructor(
    private val rulesRepository: RulesRepository,
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (rulesRepository.getRules().isEmpty()) return Result.success()

        inputData.getStringArray(MESSAGES_KEY)?.let { messages ->
            val intent = Intent(context, ForwardingService::class.java)
            intent.putExtra(MESSAGES_KEY, messages)
            context.startForegroundService(intent)
        }
        return Result.success()
    }
}
