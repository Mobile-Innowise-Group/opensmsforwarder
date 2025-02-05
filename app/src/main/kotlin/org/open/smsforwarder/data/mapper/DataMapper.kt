package org.open.smsforwarder.data.mapper

import org.open.smsforwarder.data.local.database.entity.ForwardingEntity
import org.open.smsforwarder.data.local.database.entity.HistoryEntity
import org.open.smsforwarder.data.local.database.entity.RuleEntity
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.History
import org.open.smsforwarder.domain.model.Rule

fun ForwardingEntity.toDomain(): Forwarding =
    Forwarding(
        id = id,
        title = forwardingTitle,
        forwardingType = forwardingType,
        recipientPhone = recipientPhone,
        senderEmail = senderEmail,
        recipientEmail = recipientEmail,
        error = errorText
    )

fun RuleEntity.toDomain() =
    Rule(
        id = id,
        forwardingId = forwardingId,
        textRule = rule
    )

fun HistoryEntity.toDomain() =
    History(
        id = id,
        date = date,
        forwardingId = forwardingId,
        message = message,
        isForwardingSuccessful = isForwardingSuccessful
    )

fun Forwarding.toData(): ForwardingEntity =
    ForwardingEntity(
        id = id,
        forwardingTitle = title,
        forwardingType = forwardingType,
        recipientPhone = recipientPhone,
        senderEmail = senderEmail,
        recipientEmail = recipientEmail,
        errorText = error
    )

fun Rule.toData(): RuleEntity =
    RuleEntity(
        id = id,
        forwardingId = forwardingId,
        rule = textRule
    )

fun History.toData(): HistoryEntity =
    HistoryEntity(
        id = id,
        forwardingId = forwardingId,
        date = date,
        message = message,
        isForwardingSuccessful = isForwardingSuccessful
    )
