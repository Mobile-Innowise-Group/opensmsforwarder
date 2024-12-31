package org.open.smsforwarder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import org.open.smsforwarder.MainActivity
import org.open.smsforwarder.R

object NotificationHelper {

    private const val CHANNEL_ID = "SMS_FORWARDER_NOTIFICATION_CHANNEL"

    fun createNotification(
        context: Context,
        contentTitle: String? = null,
        contentText: String? = null
    ) {
        createNotificationChannel(context)
        val notification = createDefaultNotificationBuilder(context)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setVisibility(VISIBILITY_PUBLIC)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }

    fun createNotificationChannel(context: Context) {
        val appName = context.getString(R.string.app_name)
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            appName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(serviceChannel)
    }

    fun createDefaultNotificationBuilder(context: Context): NotificationCompat.Builder {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sms_forwarding)
            .setContentIntent(pendingIntent)
    }
}
