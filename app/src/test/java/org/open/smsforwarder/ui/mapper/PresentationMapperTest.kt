package org.open.smsforwarder.ui.mapper

import kotlinx.coroutines.CancellationException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.open.smsforwarder.R
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.domain.model.History
import org.open.smsforwarder.domain.model.Rule
import org.open.smsforwarder.platform.GoogleSignInFailure
import org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails.AddEmailDetailsState
import org.open.smsforwarder.ui.steps.addrecipientdetails.addtelegramdetails.AddTelegramDetailsState
import java.net.UnknownHostException
import java.util.Date

class PresentationMapperTest {

    @Test
    fun `Forwarding maps to AddEmailDetailsState`() {
        val forwarding = Forwarding(
            id = 1,
            title = "Email Rule",
            forwardingType = ForwardingType.EMAIL,
            senderEmail = "sender@example.com",
            recipientEmail = "receiver@example.com"
        )

        val state = forwarding.toEmailDetailsUi()

        assertEquals(forwarding.id, state.id)
        assertEquals(forwarding.title, state.title)
        assertEquals(forwarding.forwardingType, state.forwardingType)
        assertEquals(forwarding.senderEmail, state.senderEmail)
        assertEquals(forwarding.recipientEmail, state.recipientEmail)
    }

    @Test
    fun `Forwarding maps to AddTelegramDetailsState`() {
        val forwarding = Forwarding(
            id = 2,
            title = "Telegram Rule",
            forwardingType = ForwardingType.TELEGRAM,
            telegramApiToken = "token123",
            telegramChatId = "chat456"
        )

        val state = forwarding.toTelegramDetailsUi()

        assertEquals(forwarding.id, state.id)
        assertEquals(forwarding.title, state.title)
        assertEquals(forwarding.forwardingType, state.forwardingType)
        assertEquals(forwarding.telegramApiToken, state.telegramApiToken)
        assertEquals(forwarding.telegramChatId, state.telegramChatId)
    }

    @Test
    fun `History maps to HistoryUI`() {
        val date = Date()
        val history = History(
            id = 10,
            date = date.time,
            forwardingId = 99,
            message = "Test message",
            isForwardingSuccessful = true
        )

        val ui = history.toHistoryUi()

        assertEquals(history.id, ui.id)
        assertEquals(history.date, ui.date)
        assertEquals(history.message, ui.message)
        assertEquals(history.isForwardingSuccessful, ui.isForwardingSuccessful)
    }

    @Test
    fun `mergeWithRules maps correctly to HomeState with rules present`() {
        val forwardingList = listOf(
            Forwarding(id = 1, title = "F1", forwardingType = ForwardingType.EMAIL),
            Forwarding(id = 2, title = "F2", forwardingType = ForwardingType.TELEGRAM)
        )

        val rules = listOf(
            Rule(id = 1, forwardingId = 2, textRule = "rule")
        )

        val homeState = forwardingList.mergeWithRules(rules)

        assertEquals(2, homeState.forwardings.size)
        assertFalse(homeState.forwardings[0].atLeastOneRuleAdded)
        assertTrue(homeState.forwardings[1].atLeastOneRuleAdded)
    }

    @Test
    fun `AddEmailDetailsState maps to Forwarding`() {
        val state = AddEmailDetailsState(
            id = 5,
            title = "Email Forward",
            forwardingType = ForwardingType.EMAIL,
            senderEmail = "a@b.com",
            recipientEmail = "b@c.com"
        )

        val model = state.toDomain()

        assertEquals(state.id, model.id)
        assertEquals(state.title, model.title)
        assertEquals(state.forwardingType, model.forwardingType)
        assertEquals(state.senderEmail, model.senderEmail)
        assertEquals(state.recipientEmail, model.recipientEmail)
    }

    @Test
    fun `AddTelegramDetailsState maps to Forwarding`() {
        val state = AddTelegramDetailsState(
            id = 6,
            title = "Telegram Forward",
            forwardingType = ForwardingType.TELEGRAM,
            telegramApiToken = "token",
            telegramChatId = "chatId"
        )

        val model = state.toDomain()

        assertEquals(state.id, model.id)
        assertEquals(state.title, model.title)
        assertEquals(state.forwardingType, model.forwardingType)
        assertEquals(state.telegramApiToken, model.telegramApiToken)
        assertEquals(state.telegramChatId, model.telegramChatId)
    }

    @Test
    fun `Throwable toUserMessageResId maps correctly`() {
        assertEquals(
            R.string.google_sign_in_canceled,
            GoogleSignInFailure.CredentialCancellation().toUserMessageResId()
        )
        assertEquals(
            R.string.network_failure,
            GoogleSignInFailure.NoInternetConnection().toUserMessageResId()
        )
        assertEquals(
            R.string.network_failure,
            UnknownHostException().toUserMessageResId()
        )
        assertEquals(
            R.string.credentials_not_found,
            GoogleSignInFailure.CredentialsNotFound().toUserMessageResId()
        )
        assertEquals(
            R.string.authorization_failed,
            GoogleSignInFailure.AuthorizationFailed().toUserMessageResId()
        )
        assertEquals(
            R.string.authorization_result_is_null,
            GoogleSignInFailure.AuthResultIntentIsNull.toUserMessageResId()
        )
        assertEquals(
            R.string.pending_intent_missing,
            GoogleSignInFailure.MissingPendingIntent.toUserMessageResId()
        )
        assertEquals(
            R.string.auth_code_not_received,
            GoogleSignInFailure.MissingAuthCode.toUserMessageResId()
        )
        assertEquals(
            R.string.sign_in_general_error,
            RuntimeException("unknown error").toUserMessageResId()
        )
    }

    @Test
    fun `CancellationException thrown in toUserMessageResId`() {
        assertThrows(CancellationException::class.java) {
            CancellationException("Cancelled").toUserMessageResId()
        }
    }
}
