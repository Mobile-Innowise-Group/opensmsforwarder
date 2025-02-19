package org.open.smsforwarder.extension

import android.content.Context
import org.open.smsforwarder.R
import org.open.smsforwarder.domain.ValidationError

fun ValidationError.getErrorMessage(context: Context): String {
    return when (this) {
        ValidationError.BLANK_FIELD -> context.getString(R.string.error_generic_is_blank)
        ValidationError.BLANK_EMAIL -> context.getString(R.string.error_email_is_blank)
        ValidationError.INVALID_EMAIL -> context.getString(R.string.error_email_is_not_valid)
        ValidationError.INVALID_PHONE -> context.getString(R.string.error_phone_number_is_not_valid)
    }
}
