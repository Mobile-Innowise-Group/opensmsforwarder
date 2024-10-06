package com.github.opensmsforwarder.processing.forwarding.processor

import com.github.opensmsforwarder.data.AuthRepository
import com.github.opensmsforwarder.data.ForwardingHistoryRepository
import com.github.opensmsforwarder.data.RecipientsRepository
import com.github.opensmsforwarder.data.RulesRepository
import com.github.opensmsforwarder.data.remote.interceptor.RefreshTokenException
import com.github.opensmsforwarder.data.remote.interceptor.TokenRevokedException
import com.github.opensmsforwarder.model.ForwardingType
import com.github.opensmsforwarder.model.Recipient
import com.github.opensmsforwarder.processing.forwarding.handler.ForwardingHandler
import javax.inject.Inject

class ForwardingProcessor @Inject constructor(
    private val forwardingHandlers: Map<ForwardingType, @JvmSuppressWildcards ForwardingHandler>,
    private val rulesRepository: RulesRepository,
    private val recipientsRepository: RecipientsRepository,
    private val forwardingHistoryRepository: ForwardingHistoryRepository,
    private val authRepository: AuthRepository
) {

    suspend fun process(messages: Array<String>) {
        val rules = rulesRepository.getRules()
        if (rules.isEmpty()) return

        val messagesToForward = mutableListOf<Pair<Long, String>>()

        messages.forEach { message ->
            rules.forEach { rule ->
                if (message.contains(rule.textRule)) {
                    messagesToForward.add(rule.recipientId to message)
                }
            }
        }

        messagesToForward.forEach { (recipientId, message) ->
            recipientsRepository.getRecipientById(recipientId)?.let { recipient ->
                forwardingHandlers[recipient.forwardingType]
                    ?.run(recipient, message)
                    ?.onSuccess {
                        updateForwardingStatus(recipient, message, true)
                    }
                    ?.onFailure { error ->
                        updateForwardingStatus(recipient, message, false)
                        if (error is TokenRevokedException || error is RefreshTokenException) {
                            authRepository.signOut(recipient)
                        }
                    }
            }
        }
    }

    private suspend fun updateForwardingStatus(
        recipient: Recipient,
        message: String,
        isSuccess: Boolean
    ) {
        recipientsRepository.insertOrUpdateRecipient(
            recipient.copy(isForwardSuccessful = isSuccess)
        )
        forwardingHistoryRepository.insertOrUpdateForwardedSms(
            recipientId = recipient.id,
            message = message,
            isForwardingSuccessful = isSuccess
        )
    }
}