package org.open.smsforwarder.extension

import org.open.smsforwarder.R
import org.open.smsforwarder.domain.ValidationError
import org.open.smsforwarder.utils.Resources

fun ValidationError.getStringProvider(): Resources.StringProvider {
    return when (this) {
        ValidationError.BLANK_FIELD -> Resources.StringResource(R.string.error_generic_is_blank)
        ValidationError.BLANK_EMAIL -> Resources.StringResource(R.string.error_email_is_blank)
        ValidationError.INVALID_EMAIL -> Resources.StringResource(R.string.error_email_is_not_valid)
        ValidationError.INVALID_PHONE -> Resources.StringResource(R.string.error_phone_number_is_not_valid)
    }
}
