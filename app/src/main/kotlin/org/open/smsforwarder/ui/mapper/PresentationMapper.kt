package org.open.smsforwarder.ui.mapper

import kotlinx.coroutines.CancellationException
import org.open.smsforwarder.R
import org.open.smsforwarder.platform.GoogleSignInFailure
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.History
import org.open.smsforwarder.domain.model.Rule
import org.open.smsforwarder.ui.home.HomeState
import org.open.smsforwarder.ui.model.ForwardingUI
import org.open.smsforwarder.ui.model.HistoryUI
import org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails.AddEmailDetailsState
import org.open.smsforwarder.ui.steps.addrecipientdetails.addtelegramdetails.AddTelegramDetailsState

fun Forwarding.toEmailDetailsUi(): AddEmailDetailsState {
    return AddEmailDetailsState(
        id = id,
        title = title,
        forwardingType = forwardingType,
        senderEmail = senderEmail,
        recipientEmail = recipientEmail,
    )
}

fun Forwarding.toTelegramDetailsUi(): AddTelegramDetailsState =
    AddTelegramDetailsState(
        id = id,
        title = title,
        forwardingType = forwardingType,
        telegramApiToken = telegramApiToken,
        telegramChatId = telegramChatId,
    )

fun History.toHistoryUi() =
    HistoryUI(
        id = id,
        date = date,
        message = message,
        isForwardingSuccessful = isForwardingSuccessful
    )

fun List<Forwarding>.mergeWithRules(rules: List<Rule>): HomeState {
    val forwardingUI = mutableListOf<ForwardingUI>()
    forEach { forwarding ->
        forwardingUI.add(
            ForwardingUI(
                id = forwarding.id,
                title = forwarding.title,
                forwardingType = forwarding.forwardingType,
                senderEmail = forwarding.senderEmail,
                telegramApiToken = forwarding.telegramApiToken,
                telegramChatId = forwarding.telegramChatId,
                recipientEmail = forwarding.recipientEmail,
                error = forwarding.error,
                atLeastOneRuleAdded = rules.any { forwarding.id == it.forwardingId }
            )
        )
    }
    return HomeState(forwardingUI)
}

fun AddTelegramDetailsState.toDomain() =
    Forwarding(
        id = id,
        title = title,
        forwardingType = forwardingType,
        telegramApiToken = telegramApiToken,
        telegramChatId = telegramChatId,
    )

fun AddEmailDetailsState.toDomain() =
    Forwarding(
        id = id,
        title = title,
        forwardingType = forwardingType,
        senderEmail = senderEmail,
        recipientEmail = recipientEmail
    )

fun Throwable.toUserMessageResId(): Int = when (this) {
    is CancellationException -> throw this
    is GoogleSignInFailure.CredentialCancellation -> R.string.google_sign_in_canceled
    is GoogleSignInFailure.CredentialsNotFound -> R.string.credentials_not_found
    is GoogleSignInFailure.AuthorizationFailed -> R.string.authorization_failed
    is GoogleSignInFailure.AuthResultIntentIsNull -> R.string.authorization_result_is_null
    is GoogleSignInFailure.MissingPendingIntent -> R.string.pending_intent_missing
    is GoogleSignInFailure.MissingAuthCode -> R.string.auth_code_not_received
    else -> R.string.sign_in_general_error
}
