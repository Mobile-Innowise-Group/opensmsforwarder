package org.open.smsforwarder.extension

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DATE_FORMAT = ThreadLocal.withInitial {
    SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.getDefault())
}

fun Long?.toDateTime(): String? =
    runCatching {
        this?.let { timestamp ->
            DATE_FORMAT.get()?.format(Date(timestamp))
        }
    }.getOrDefault(null)
