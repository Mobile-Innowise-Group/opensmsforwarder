package com.github.opensmsforwarder.extension

import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.data.remote.interceptor.AuthTokenException
import com.github.opensmsforwarder.data.remote.interceptor.RecipientIdNotFoundException
import com.github.opensmsforwarder.data.remote.interceptor.RefreshTokenException
import com.github.opensmsforwarder.data.remote.interceptor.TokenRevokedException
import retrofit2.HttpException
import java.io.IOException

fun Throwable.getErrorDescription(): Int {
    return when (this) {
        is HttpException, is TokenRevokedException ->
            R.string.forwarding_is_disabled_some_problem_with_internet_connection

        is RefreshTokenException, is AuthTokenException ->
            R.string.sms_forward_phone_error


        is RecipientIdNotFoundException ->
            R.string.forwarding_is_disabled_recipient_is_not_found

        is IOException -> {
            if (message?.contains("timeout", ignoreCase = true) == true) {
                R.string.error_the_request_timed_out
            } else {
                R.string.error_the_server_could_not_be_reached
            }
        }

        else -> R.string.forwarding_is_disabled_an_unknown_error_occurred
    }
}
