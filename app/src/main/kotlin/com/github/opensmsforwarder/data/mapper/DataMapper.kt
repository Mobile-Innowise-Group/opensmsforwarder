package com.github.opensmsforwarder.data.mapper

import com.github.opensmsforwarder.data.local.database.entity.ForwardingEntity
import com.github.opensmsforwarder.data.local.database.entity.RuleEntity
import com.github.opensmsforwarder.domain.model.Forwarding
import com.github.opensmsforwarder.domain.model.Rule

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
