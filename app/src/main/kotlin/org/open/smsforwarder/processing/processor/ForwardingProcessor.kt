package org.open.smsforwarder.processing.processor

import org.open.smsforwarder.data.remote.interceptor.RefreshTokenException
import org.open.smsforwarder.data.remote.interceptor.TokenRevokedException
import org.open.smsforwarder.data.repository.AuthRepository
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.data.repository.HistoryRepository
import org.open.smsforwarder.data.repository.RulesRepository
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.processing.forwarder.Forwarder
import javax.inject.Inject

class ForwardingProcessor @Inject constructor(
    private val forwarders: Map<ForwardingType, @JvmSuppressWildcards Forwarder>,
    private val rulesRepository: RulesRepository,
    private val forwardingRepository: ForwardingRepository,
    private val historyRepository: HistoryRepository,
    private val authRepository: AuthRepository
) {

    suspend fun process(messages: Array<String>, historyEntityId: Long? = null) {
        val rules = rulesRepository.getRules()
        if (rules.isEmpty()) return

        val messagesToForward = mutableListOf<Pair<Long, String>>()

        messages.forEach { message ->
            rules.forEach { rule ->
                if (message.contains(rule.textRule)) {
                    messagesToForward.add(rule.forwardingId to message)
                }
            }
        }

        messagesToForward.forEach { (recipientId, message) ->
            forwardingRepository.getForwardingById(recipientId)?.let { recipient ->
                forwarders[recipient.forwardingType]
                    ?.execute(recipient, message)
                    ?.onSuccess {
                        updateForwardingStatus(recipient, message, "", historyEntityId)
                    }
                    ?.onFailure { error ->
                        updateForwardingStatus(recipient, message, error.message.orEmpty(), historyEntityId)
                        if (error is TokenRevokedException || error is RefreshTokenException) {
                            authRepository.signOut(recipient)
                        }
                    }
            }
        }
    }

    private suspend fun updateForwardingStatus(
        forwarding: Forwarding,
        message: String,
        errorText: String,
        historyEntityId: Long?
    ) {
        forwardingRepository.insertOrUpdateForwarding(
            forwarding.copy(error = errorText)
        )
        historyRepository.insertOrUpdateForwardedSms(
            forwardingId = forwarding.id,
            message = message,
            isForwardingSuccessful = errorText.isEmpty(),
            historyEntityId = historyEntityId
        )
    }
}
