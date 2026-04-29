package org.open.smsforwarder.processing.processor

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.open.smsforwarder.data.remote.interceptor.RefreshTokenException
import org.open.smsforwarder.data.remote.interceptor.TokenRevokedException
import org.open.smsforwarder.data.repository.AuthRepository
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.data.repository.HistoryRepository
import org.open.smsforwarder.data.repository.RulesRepository
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.domain.model.Rule
import org.open.smsforwarder.processing.dedup.SmsDeduplicationManager
import org.open.smsforwarder.processing.forwarder.Forwarder
import org.open.smsforwarder.processing.model.IncomingSms

@ExtendWith(MockitoExtension::class)
class ForwardingProcessorTest {

    @Mock
    lateinit var rulesRepository: RulesRepository

    @Mock
    lateinit var forwardingRepository: ForwardingRepository

    @Mock
    lateinit var historyRepository: HistoryRepository

    @Mock
    lateinit var authRepository: AuthRepository

    @Mock
    lateinit var deduplicationService: SmsDeduplicationManager

    @Mock
    lateinit var emailForwarder: Forwarder

    @Test
    fun `process does not forward when dedup marks message as duplicate`() = runTest {
        val processor = createProcessor()
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))
        whenever(deduplicationService.shouldProcess("bank", "Your OTP is 1111")).thenReturn(false)

        processor.process(listOf(sms("Your OTP is 1111", "bank")))

        verify(forwardingRepository, never()).getForwardingById(any())
        verify(emailForwarder, never()).execute(any(), any())
        verify(historyRepository, never()).insertForwardedSms(any(), any(), any())
    }

    @Test
    fun `process forwards and writes history when dedup allows and rule matches`() = runTest {
        val processor = createProcessor()
        val forwarding = Forwarding(id = 10, forwardingType = ForwardingType.EMAIL)
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))
        whenever(deduplicationService.shouldProcess("bank", "Your OTP is 1111")).thenReturn(true)
        whenever(forwardingRepository.getForwardingById(10)).thenReturn(forwarding)
        whenever(emailForwarder.execute(forwarding, "Your OTP is 1111")).thenReturn(Result.success(Unit))

        processor.process(listOf(sms("Your OTP is 1111", "bank")))

        verify(emailForwarder).execute(forwarding, "Your OTP is 1111")
        verify(historyRepository).insertForwardedSms(10, "Your OTP is 1111", true)
    }

    @Test
    fun `process forwards once when multiple rules match same recipient`() = runTest {
        val processor = createProcessor()
        val forwarding = Forwarding(id = 10, forwardingType = ForwardingType.EMAIL)
        whenever(rulesRepository.getRules()).thenReturn(
            listOf(
                Rule(id = 1, forwardingId = 10, textRule = "OTP"),
                Rule(id = 2, forwardingId = 10, textRule = "1111")
            )
        )
        whenever(deduplicationService.shouldProcess("bank", "Your OTP is 1111")).thenReturn(true)
        whenever(forwardingRepository.getForwardingById(10)).thenReturn(forwarding)
        whenever(emailForwarder.execute(forwarding, "Your OTP is 1111")).thenReturn(Result.success(Unit))

        processor.process(listOf(sms("Your OTP is 1111", "bank")))

        verify(emailForwarder, times(1)).execute(forwarding, "Your OTP is 1111")
        verify(historyRepository, times(1)).insertForwardedSms(10, "Your OTP is 1111", true)
    }

    @Test
    fun `process passes sender by message index to dedup service`() = runTest {
        val processor = createProcessor()
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))
        whenever(deduplicationService.shouldProcess(anyOrNull(), any())).thenReturn(true)

        processor.process(
            listOf(
                sms("m1", "s1"),
                sms("m2", null)
            )
        )

        verify(deduplicationService).shouldProcess("s1", "m1")
        verify(deduplicationService).shouldProcess(null, "m2")
    }

    @Test
    fun `process returns immediately when rules are empty`() = runTest {
        val processor = createProcessor()
        whenever(rulesRepository.getRules()).thenReturn(emptyList())

        processor.process(listOf(sms("message", "sender")))

        verify(deduplicationService, never()).shouldProcess(any(), any())
        verify(forwardingRepository, never()).getForwardingById(any())
    }

    @Test
    fun `process returns immediately when messages are empty`() = runTest {
        val processor = createProcessor()
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))

        processor.process(emptyList())

        verify(deduplicationService, never()).shouldProcess(any(), any())
        verify(forwardingRepository, never()).getForwardingById(any())
    }

    @Test
    fun `process does nothing when dedup allows but no rules match`() = runTest {
        val processor = createProcessor()
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))
        whenever(deduplicationService.shouldProcess("bank", "unrelated message")).thenReturn(true)

        processor.process(listOf(sms("unrelated message", "bank")))

        verify(forwardingRepository, never()).getForwardingById(any())
        verify(emailForwarder, never()).execute(any(), any())
    }

    @Test
    fun `process skips forwarding when recipient is not found`() = runTest {
        val processor = createProcessor()
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))
        whenever(deduplicationService.shouldProcess("bank", "Your OTP is 1111")).thenReturn(true)
        whenever(forwardingRepository.getForwardingById(10)).thenReturn(null)

        processor.process(listOf(sms("Your OTP is 1111", "bank")))

        verify(emailForwarder, never()).execute(any(), any())
        verify(historyRepository, never()).insertForwardedSms(any(), any(), any())
    }

    @Test
    fun `process skips forwarding when forwarder for type is missing`() = runTest {
        val processor = createProcessor()
        val telegramForwarding = Forwarding(id = 10, forwardingType = ForwardingType.TELEGRAM)
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))
        whenever(deduplicationService.shouldProcess("bank", "Your OTP is 1111")).thenReturn(true)
        whenever(forwardingRepository.getForwardingById(10)).thenReturn(telegramForwarding)

        processor.process(listOf(sms("Your OTP is 1111", "bank")))

        verify(emailForwarder, never()).execute(any(), any())
        verify(historyRepository, never()).insertForwardedSms(any(), any(), any())
    }

    @Test
    fun `process writes failure state and history when forwarder fails`() = runTest {
        val processor = createProcessor()
        val forwarding = Forwarding(id = 10, forwardingType = ForwardingType.EMAIL, senderEmail = "a@b.com")
        val exception = IllegalStateException("failed to send")
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))
        whenever(deduplicationService.shouldProcess("bank", "Your OTP is 1111")).thenReturn(true)
        whenever(forwardingRepository.getForwardingById(10)).thenReturn(forwarding)
        whenever(emailForwarder.execute(forwarding, "Your OTP is 1111")).thenReturn(Result.failure(exception))

        processor.process(listOf(sms("Your OTP is 1111", "bank")))

        val forwardingCaptor = argumentCaptor<Forwarding>()
        verify(forwardingRepository).insertOrUpdateForwarding(forwardingCaptor.capture())
        assertEquals("failed to send", forwardingCaptor.firstValue.error)
        verify(historyRepository).insertForwardedSms(10, "Your OTP is 1111", false)
        verify(authRepository, never()).signOut(any())
    }

    @Test
    fun `process signs out and clears sender email on token revoked error`() = runTest {
        val processor = createProcessor()
        val forwarding = Forwarding(id = 10, forwardingType = ForwardingType.EMAIL, senderEmail = "sender@example.com")
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))
        whenever(deduplicationService.shouldProcess("bank", "Your OTP is 1111")).thenReturn(true)
        whenever(forwardingRepository.getForwardingById(10)).thenReturn(forwarding)
        whenever(emailForwarder.execute(forwarding, "Your OTP is 1111"))
            .thenReturn(Result.failure(TokenRevokedException()))

        processor.process(listOf(sms("Your OTP is 1111", "bank")))

        verify(authRepository).signOut(10)
        val forwardingCaptor = argumentCaptor<Forwarding>()
        verify(forwardingRepository, times(2)).insertOrUpdateForwarding(forwardingCaptor.capture())
        assertEquals(null, forwardingCaptor.secondValue.senderEmail)
    }

    @Test
    fun `process signs out and clears sender email on refresh token error`() = runTest {
        val processor = createProcessor()
        val forwarding = Forwarding(id = 10, forwardingType = ForwardingType.EMAIL, senderEmail = "sender@example.com")
        whenever(rulesRepository.getRules()).thenReturn(listOf(Rule(id = 1, forwardingId = 10, textRule = "OTP")))
        whenever(deduplicationService.shouldProcess("bank", "Your OTP is 1111")).thenReturn(true)
        whenever(forwardingRepository.getForwardingById(10)).thenReturn(forwarding)
        whenever(emailForwarder.execute(forwarding, "Your OTP is 1111"))
            .thenReturn(Result.failure(RefreshTokenException()))

        processor.process(listOf(sms("Your OTP is 1111", "bank")))

        verify(authRepository).signOut(10)
        val forwardingCaptor = argumentCaptor<Forwarding>()
        verify(forwardingRepository, times(2)).insertOrUpdateForwarding(forwardingCaptor.capture())
        assertEquals(null, forwardingCaptor.secondValue.senderEmail)
    }

    private fun createProcessor(): ForwardingProcessor =
        ForwardingProcessor(
            forwarders = mapOf(ForwardingType.EMAIL to emailForwarder),
            rulesRepository = rulesRepository,
            forwardingRepository = forwardingRepository,
            historyRepository = historyRepository,
            authRepository = authRepository,
            smsDeduplicationManager = deduplicationService
        )

    private fun sms(message: String, sender: String?): IncomingSms =
        IncomingSms(sender = sender, message = message)
}
