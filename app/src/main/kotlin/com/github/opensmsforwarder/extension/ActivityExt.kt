package com.github.opensmsforwarder.extension

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.github.opensmsforwarder.R

fun Activity.smsReceivePermissionGranted(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECEIVE_SMS
    ) == PackageManager.PERMISSION_GRANTED

fun Activity.smsSendPermissionGranted(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.SEND_SMS
    ) == PackageManager.PERMISSION_GRANTED

fun Activity.postNotificationsPermissionGranted(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

fun Activity.showOkDialog(
    title: String? = null,
    message: String,
    @StringRes buttonNameRes: Int = R.string.ok,
    @StyleRes dialogStyle: Int = 0,
    okClickAction: (() -> Unit)? = null,
) {
    AlertDialog.Builder(this, dialogStyle)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(buttonNameRes) { dialog, _ ->
            dialog.dismiss()
            okClickAction?.invoke()
        }
        .create()
        .show()
}
