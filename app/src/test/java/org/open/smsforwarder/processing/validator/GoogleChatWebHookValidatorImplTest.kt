package org.open.smsforwarder.processing.validator

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GoogleChatWebHookValidatorImplTest {

    private val validator = GoogleChatWebHookValidatorImpl()

    @Test
    fun `isValid returns true for valid google chat webhook`() {
        val validWebHook = "https://chat.googleapis.com/v1/spaces/AAQAsyvwszg/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=1xqqxqqJztXNo7gJdvwuWOC8ZSHknsii2F8j3yPMFaQ"

        assertTrue(validator.isValid(validWebHook))
    }

    @Test
    fun `isValid returns false for non https webhook`() {
        val invalidWebHook = "http://chat.googleapis.com/v1/spaces/AAQAsyvwszg/messages?key=test&token=test"

        assertFalse(validator.isValid(invalidWebHook))
    }

    @Test
    fun `isValid returns false when key is missing`() {
        val invalidWebHook = "https://chat.googleapis.com/v1/spaces/AAQAsyvwszg/messages?token=test"

        assertFalse(validator.isValid(invalidWebHook))
    }

    @Test
    fun `isValid returns false when token is missing`() {
        val invalidWebHook = "https://chat.googleapis.com/v1/spaces/AAQAsyvwszg/messages?key=test"

        assertFalse(validator.isValid(invalidWebHook))
    }

    @Test
    fun `isValid returns false for invalid path`() {
        val invalidWebHook = "https://chat.googleapis.com/v1/rooms/AAQAsyvwszg/messages?key=test&token=test"

        assertFalse(validator.isValid(invalidWebHook))
    }
}
