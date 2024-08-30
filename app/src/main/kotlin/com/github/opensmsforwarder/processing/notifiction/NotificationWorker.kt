package com.github.opensmsforwarder.processing.notifiction

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.opensmsforwarder.data.ForwardingHistoryRepository
import com.github.opensmsforwarder.utils.NotificationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val forwardingHistoryRepository: ForwardingHistoryRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val count = forwardingHistoryRepository.getForwardedMessagesCountLast24Hours()
        NotificationUtils.createReminderNotification(context, count)
        return Result.success()
    }
}
