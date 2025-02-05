package org.open.smsforwarder.processing.processor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.remote.interceptor.RefreshTokenException
import org.open.smsforwarder.data.remote.interceptor.TokenRevokedException
import org.open.smsforwarder.data.repository.AuthRepository
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.data.repository.HistoryRepository
import org.open.smsforwarder.data.repository.RulesRepository
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.domain.model.History
import org.open.smsforwarder.processing.forwarder.Forwarder
import javax.inject.Inject

class ForwardingProcessor @Inject constructor(
    private val forwarders: Map<ForwardingType, @JvmSuppressWildcards Forwarder>,
    private val rulesRepository: RulesRepository,
    private val forwardingRepository: ForwardingRepository,
    private val historyRepository: HistoryRepository,
    private val authRepository: AuthRepository
) {

    suspend fun process(messages: Array<String>) {
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
                        updateForwardingAndCreateHistory(recipient, message, "")
                    }
                    ?.onFailure { error ->
                        updateForwardingAndCreateHistory(recipient, message, error.message.orEmpty())
                        handleTokenErrors(error, recipient)
                    }
            }
        }
    }

    suspend fun retryForwarding(historyItem: History) {
        withContext(Dispatchers.IO) {
            historyItem.let { history ->
                forwardingRepository.getForwardingById(history.forwardingId)?.let { recipient ->
                    forwarders[recipient.forwardingType]
                        ?.execute(recipient, history.message)
                        ?.onSuccess {
                            updateForwardingAndHistory(recipient, history, "")
                        }
                        ?.onFailure { error ->
                            updateForwardingAndHistory(recipient, history, error.message.orEmpty())
                            handleTokenErrors(error, recipient)
                        }
                }
            }
        }
    }

    private suspend fun handleTokenErrors(error: Throwable, recipient: Forwarding) {
        if (error is TokenRevokedException || error is RefreshTokenException) {
            authRepository.signOut(recipient)
        }
    }

    private suspend fun updateForwardingAndHistory(
        forwarding: Forwarding,
        history: History,
        errorText: String
    ) {
        forwardingRepository.insertOrUpdateForwarding(
            forwarding.copy(error = errorText)
        )
        historyRepository.updateForwardedSms(
            history.copy(
                isForwardingSuccessful = errorText.isEmpty(),
                date = System.currentTimeMillis()
            )
        )
    }

    private suspend fun updateForwardingAndCreateHistory(
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
}
