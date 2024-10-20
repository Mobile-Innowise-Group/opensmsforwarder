package com.github.opensmsforwarder.processing.notifiction

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerConfigurator @Inject constructor(private val context: Context) {

    fun scheduleDailyForwardedMessageCountWork() {
        val workRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            FORWARDED_MESSAGE_COUNT_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    companion object {
        private const val FORWARDED_MESSAGE_COUNT_WORK_NAME = "Forwarded messages count notification"
    }
}
