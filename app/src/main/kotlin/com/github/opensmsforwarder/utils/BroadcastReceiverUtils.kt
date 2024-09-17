package com.github.opensmsforwarder.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.model.SmsSendException
import kotlinx.coroutines.CompletableDeferred

const val BROADCAST_ACTION_SENT_NAME = "FORWARDED_SMS_SENT"

fun createSentStatusReceiver(
    sentResult: CompletableDeferred<Unit>,
): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BROADCAST_ACTION_SENT_NAME -> {
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            sentResult.complete(Unit)
                        }

                        SmsManager.RESULT_ERROR_GENERIC_FAILURE -> sentResult.completeExceptionally(
                            SmsSendException(context.getString(R.string.forwarding_is_disabled_generic_failure_error))
                        )

                        SmsManager.RESULT_ERROR_NO_SERVICE -> sentResult.completeExceptionally(
                            SmsSendException(context.getString(R.string.forwarding_is_disabled_no_service_available))
                        )

                        SmsManager.RESULT_ERROR_NULL_PDU -> sentResult.completeExceptionally(
                            SmsSendException(context.getString(R.string.forwarding_is_disabled_null_pdu))
                        )

                        SmsManager.RESULT_ERROR_RADIO_OFF -> sentResult.completeExceptionally(
                            SmsSendException(context.getString(R.string.forwarding_is_disabled_radio_is_off))
                        )

                        else -> sentResult.completeExceptionally(
                            SmsSendException(
                                context.getString(
                                    R.string.sms_forward_phone_error
                                )
                            )
                        )
                    }
                    context.unregisterReceiver(this)
                }
            }
        }
    }
}
