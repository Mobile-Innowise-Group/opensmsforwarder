package org.open.smsforwarder.extension

fun String.normalizeSpaces(): String = this.replace('\u00A0', ' ').trim()
