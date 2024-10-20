package com.github.opensmsforwarder.extension

import android.app.Activity
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import com.github.opensmsforwarder.R

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
