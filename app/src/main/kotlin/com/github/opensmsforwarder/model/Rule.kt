package com.github.opensmsforwarder.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rule(
    val id: Long = 0,
    val recipientId: Long,
    val textRule: String,
) : Parcelable
