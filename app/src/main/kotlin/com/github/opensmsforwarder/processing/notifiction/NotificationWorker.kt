package com.github.opensmsforwarder.processing.notifiction

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.data.repository.HistoryRepository
import com.github.opensmsforwarder.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val historyRepository: HistoryRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val messageCount = historyRepository.getForwardedMessagesForLast24Hours()
        NotificationHelper.createNotification(
            context = context,
            contentTitle = context.getString(R.string.forwarded_messages),
            contentText = context.getString(
                R.string.forwarded_messages_count,
                messageCount.toString()
            )
        )
        return Result.success()
    }
}
