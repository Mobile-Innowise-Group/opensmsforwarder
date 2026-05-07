package org.open.smsforwarder.processing.processor

import org.open.smsforwarder.data.repository.AuthRepository
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.data.repository.HistoryRepository
import org.open.smsforwarder.data.repository.RulesRepository
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.extension.normalizeSpaces
import org.open.smsforwarder.processing.dedup.SmsDeduplicationManager
import org.open.smsforwarder.processing.forwarder.Forwarder
import org.open.smsforwarder.processing.forwarder.ForwardingResult
import org.open.smsforwarder.processing.model.IncomingSms
import javax.inject.Inject

class ForwardingProcessor @Inject constructor(
    private val forwarders: Map<ForwardingType, @JvmSuppressWildcards Forwarder>,
    private val rulesRepository: RulesRepository,
    private val forwardingRepository: ForwardingRepository,
    private val historyRepository: HistoryRepository,
    private val authRepository: AuthRepository,
    private val smsDeduplicationManager: SmsDeduplicationManager,
) {

    suspend fun process(incomingMessages: List<IncomingSms>) {
        val rules = rulesRepository.getRules()
        if (rules.isEmpty() || incomingMessages.isEmpty()) return

        val normalizedRules = rules.map { it.forwardingId to it.textRule.normalizeSpaces() }

        incomingMessages.forEach { incomingSms ->
            if (!smsDeduplicationManager.shouldProcess(incomingSms.sender, incomingSms.message)) return@forEach

            val matchedRecipientIds = findMatchedRecipientIds(incomingSms.message, normalizedRules)
            matchedRecipientIds.forEach { recipientId ->
                forwardMessage(recipientId, incomingSms.message)
            }
        }
    }

    private fun findMatchedRecipientIds(
        message: String,
        normalizedRules: List<Pair<Long, String>>,
    ): Set<Long> {
        val normalizedMessage = message.normalizeSpaces()
        return normalizedRules
            .asSequence()
            .filter { (_, normalizedRule) -> normalizedMessage.contains(normalizedRule) }
            .map { (forwardingId, _) -> forwardingId }
            .toSet()
    }

    private suspend fun forwardMessage(recipientId: Long, message: String) {
        val recipient = forwardingRepository.getForwardingById(recipientId) ?: return
        val forwarder = forwarders[recipient.forwardingType] ?: return

        when (val result = forwarder.execute(recipient, message)) {
            is ForwardingResult.Success -> {
                postProcessForwarding(recipient, message, "")
            }

            is ForwardingResult.AuthRevoked -> {
                postProcessForwarding(recipient, message, result.errorMessage)
                handleRevokedAuth(recipient)
            }

            is ForwardingResult.RetryableFailure,
            is ForwardingResult.PermanentFailure,
            is ForwardingResult.AuthUnavailable ->
                postProcessForwarding(recipient, message, extractErrorMessage(result))
        }
    }

    private fun extractErrorMessage(result: ForwardingResult): String =
        when (result) {
            is ForwardingResult.RetryableFailure -> result.errorMessage
            is ForwardingResult.PermanentFailure -> result.errorMessage
            is ForwardingResult.AuthUnavailable -> result.errorMessage
            else -> ""
        }

    private suspend fun postProcessForwarding(
        forwarding: Forwarding,
        message: String,
        errorText: String,
    ) {
        forwardingRepository.insertOrUpdateForwarding(
            forwarding.copy(error = errorText)
        )
        historyRepository.insertForwardedSms(
            forwardingId = forwarding.id,
            message = message,
            isForwardingSuccessful = errorText.isEmpty(),
        )
    }

    private suspend fun handleRevokedAuth(recipient: Forwarding) {
        authRepository.signOut(recipient.id)
        forwardingRepository.insertOrUpdateForwarding(recipient.copy(senderEmail = null))
    }
}
