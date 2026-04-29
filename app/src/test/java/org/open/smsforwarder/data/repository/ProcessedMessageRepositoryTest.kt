package org.open.smsforwarder.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.open.smsforwarder.data.local.database.dao.ProcessedMessageDao

@ExtendWith(MockitoExtension::class)
class ProcessedMessageRepositoryTest {

    @Mock
    lateinit var processedMessageDao: ProcessedMessageDao

    @Test
    fun `tryMarkProcessed returns true when insert succeeds`() = runTest {
        val repository = ProcessedMessageRepository(processedMessageDao)
        whenever(processedMessageDao.insertProcessedMessage(any())).thenReturn(1L)

        val result = repository.tryMarkProcessed(fingerprint = "fp-1", ttlMs = 60_000L)

        assertTrue(result)
    }

    @Test
    fun `tryMarkProcessed returns false when insert is ignored due to duplicate`() = runTest {
        val repository = ProcessedMessageRepository(processedMessageDao)
        whenever(processedMessageDao.insertProcessedMessage(any())).thenReturn(-1L)

        val result = repository.tryMarkProcessed(fingerprint = "fp-1", ttlMs = 60_000L)

        assertFalse(result)
    }

    @Test
    fun `tryMarkProcessed deletes expired entries before insert`() = runTest {
        val repository = ProcessedMessageRepository(processedMessageDao)
        whenever(processedMessageDao.insertProcessedMessage(any())).thenReturn(1L)

        repository.tryMarkProcessed(fingerprint = "fp-ordered", ttlMs = 1_000L)

        val inOrder: InOrder = inOrder(processedMessageDao)
        inOrder.verify(processedMessageDao).deleteOlderThan(any())
        inOrder.verify(processedMessageDao).insertProcessedMessage(any())
    }

    @Test
    fun `deleteExpired uses current time minus ttl`() = runTest {
        val repository = ProcessedMessageRepository(processedMessageDao)
        val ttlMs = 2_000L
        val beforeCall = System.currentTimeMillis()

        repository.deleteExpired(ttlMs)

        val afterCall = System.currentTimeMillis()
        val minCreatedAtCaptor = argumentCaptor<Long>()
        verify(processedMessageDao).deleteOlderThan(minCreatedAtCaptor.capture())

        val minCreatedAt = minCreatedAtCaptor.firstValue
        assertTrue(minCreatedAt in (beforeCall - ttlMs)..(afterCall - ttlMs))
    }
}
