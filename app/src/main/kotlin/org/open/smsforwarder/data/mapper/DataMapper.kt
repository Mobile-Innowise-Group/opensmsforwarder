package org.open.smsforwarder.data.mapper

import org.open.smsforwarder.data.local.database.entity.ForwardingEntity
import org.open.smsforwarder.data.local.database.entity.RuleEntity
import org.open.smsforwarder.domain.model.Forwarding
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
