package org.open.smsforwarder.data.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.open.smsforwarder.data.local.database.entity.ForwardingEntity
import org.open.smsforwarder.data.local.database.entity.HistoryEntity
import org.open.smsforwarder.data.local.database.entity.RuleEntity
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.domain.model.History
import org.open.smsforwarder.domain.model.Rule
import java.util.Date

class DataMapperTest {

    @Test
    fun `ForwardingEntity maps to Forwarding`() {
        val entity = ForwardingEntity(
            id = 1,
            forwardingTitle = "Test Title",
            forwardingType = ForwardingType.EMAIL,
            senderEmail = "sender@example.com",
            recipientEmail = "recipient@example.com",
            telegramApiToken = "api-token",
            telegramChatId = "chat-id",
            errorText = "Some error"
        )

        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.forwardingTitle, domain.title)
        assertEquals(entity.forwardingType, domain.forwardingType)
        assertEquals(entity.senderEmail, domain.senderEmail)
        assertEquals(entity.recipientEmail, domain.recipientEmail)
        assertEquals(entity.telegramApiToken, domain.telegramApiToken)
        assertEquals(entity.telegramChatId, domain.telegramChatId)
        assertEquals(entity.errorText, domain.error)
    }

    @Test
    fun `Forwarding maps to ForwardingEntity`() {
        val model = Forwarding(
            id = 2,
            title = "My Forwarding",
            forwardingType = ForwardingType.TELEGRAM,
            senderEmail = null,
            recipientEmail = "",
            telegramApiToken = "12345",
            telegramChatId = "54321",
            error = ""
        )

        val entity = model.toData()

        assertEquals(model.id, entity.id)
        assertEquals(model.title, entity.forwardingTitle)
        assertEquals(model.forwardingType, entity.forwardingType)
        assertEquals(model.senderEmail, entity.senderEmail)
        assertEquals(model.recipientEmail, entity.recipientEmail)
        assertEquals(model.telegramApiToken, entity.telegramApiToken)
        assertEquals(model.telegramChatId, entity.telegramChatId)
        assertEquals(model.error, entity.errorText)
    }

    @Test
    fun `RuleEntity maps to Rule`() {
        val entity = RuleEntity(
            id = 5,
            forwardingId = 10,
            rule = "contains:abc"
        )

        val model = entity.toDomain()

        assertEquals(entity.id, model.id)
        assertEquals(entity.forwardingId, model.forwardingId)
        assertEquals(entity.rule, model.textRule)
    }

    @Test
    fun `Rule maps to RuleEntity`() {
        val rule = Rule(
            id = 6,
            forwardingId = 11,
            textRule = "startsWith:SMS"
        )

        val entity = rule.toData()

        assertEquals(rule.id, entity.id)
        assertEquals(rule.forwardingId, entity.forwardingId)
        assertEquals(rule.textRule, entity.rule)
    }

    @Test
    fun `HistoryEntity maps to History`() {
        val date = Date().time
        val entity = HistoryEntity(
            id = 99,
            date = date,
            forwardingId = 123,
            message = "Hello",
            isForwardingSuccessful = true
        )

        val model = entity.toDomain()

        assertEquals(entity.id, model.id)
        assertEquals(entity.date, model.date)
        assertEquals(entity.forwardingId, model.forwardingId)
        assertEquals(entity.message, model.message)
        assertEquals(entity.isForwardingSuccessful, model.isForwardingSuccessful)
    }

    @Test
    fun `History maps to HistoryEntity`() {
        val now = Date()
        val history = History(
            id = 100,
            date = now.time,
            forwardingId = 321,
            message = "Test message",
            isForwardingSuccessful = false
        )

        val entity = history.toData()

        assertEquals(history.id, entity.id)
        assertEquals(history.date, entity.date)
        assertEquals(history.forwardingId, entity.forwardingId)
        assertEquals(history.message, entity.message)
        assertEquals(history.isForwardingSuccessful, entity.isForwardingSuccessful)
    }
}
