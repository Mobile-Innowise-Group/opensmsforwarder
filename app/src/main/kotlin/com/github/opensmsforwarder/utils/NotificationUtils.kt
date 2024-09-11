package com.github.opensmsforwarder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import com.github.opensmsforwarder.MainActivity
import com.github.opensmsforwarder.R

object NotificationUtils {

    private const val CHANNEL_ID = "SMS_FORWARDER_NOTIFICATION_CHANNEL"

    fun createReminderNotification(context: Context, count: Int) {
        createNotificationChannel(context)
        val notification = createBaseNotificationBuilder(context)
            .setContentTitle(context.getString(R.string.forwarded_messages))
            .setContentText(context.getString(R.string.messages_was_forwarded, count))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVisibility(VISIBILITY_PUBLIC)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }

    fun createBaseNotificationBuilder(context: Context): NotificationCompat.Builder {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sms_forwarding)
            .setContentIntent(pendingIntent)
    }

    fun createNotificationChannel(context: Context) {
        val appName = context.getString(R.string.app_name)
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            appName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }
}
