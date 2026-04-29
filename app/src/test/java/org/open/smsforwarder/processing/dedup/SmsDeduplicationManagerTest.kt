package org.open.smsforwarder.processing.dedup

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.open.smsforwarder.data.repository.ProcessedMessageRepository

@ExtendWith(MockitoExtension::class)
class SmsDeduplicationManagerTest {

    @Mock
    lateinit var processedMessageRepository: ProcessedMessageRepository

    @Test
    fun `shouldProcess returns true when repository marks message as new`() = runTest {
        val service = SmsDeduplicationManager(processedMessageRepository)
        whenever(processedMessageRepository.tryMarkProcessed(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
            .thenReturn(true)

        val result = service.shouldProcess(sender = "Bank", message = "Payment done")

        assertTrue(result)
    }

    @Test
    fun `shouldProcess returns false when repository marks message as duplicate`() = runTest {
        val service = SmsDeduplicationManager(processedMessageRepository)
        whenever(processedMessageRepository.tryMarkProcessed(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
            .thenReturn(false)

        val result = service.shouldProcess(sender = "Bank", message = "Payment done")

        assertFalse(result)
    }

    @Test
    fun `shouldProcess normalizes sender and message before fingerprinting`() = runTest {
        val service = SmsDeduplicationManager(processedMessageRepository)
        whenever(processedMessageRepository.tryMarkProcessed(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
            .thenReturn(true)

        val fingerprintCaptor = argumentCaptor<String>()
        service.shouldProcess(sender = "  BANK  ", message = "\u00A0Payment done\u00A0")
        service.shouldProcess(sender = "bank", message = "Payment done")

        verify(processedMessageRepository, org.mockito.kotlin.times(2))
            .tryMarkProcessed(fingerprintCaptor.capture(), org.mockito.kotlin.any())
        assertEquals(fingerprintCaptor.firstValue, fingerprintCaptor.secondValue)
    }

    @Test
    fun `shouldProcess passes dedup ttl to repository`() = runTest {
        val service = SmsDeduplicationManager(processedMessageRepository)
        whenever(processedMessageRepository.tryMarkProcessed(org.mockito.kotlin.any(), org.mockito.kotlin.any()))
            .thenReturn(true)

        service.shouldProcess(sender = "bank", message = "msg")

        verify(processedMessageRepository).tryMarkProcessed(
            fingerprint = org.mockito.kotlin.any(),
            ttlMs = org.mockito.kotlin.eq(EXPECTED_DEDUP_TTL_MS)
        )
    }

    private companion object {
        const val EXPECTED_DEDUP_TTL_MS = 2 * 60 * 1000L
    }
}
