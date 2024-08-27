package com.github.opensmsforwarder.data.mapper

import com.github.opensmsforwarder.data.local.database.entity.ForwardedSmsEntity
import com.github.opensmsforwarder.data.local.database.entity.RecipientEntity
import com.github.opensmsforwarder.data.local.database.entity.RuleEntity
import com.github.opensmsforwarder.model.Recipient
import com.github.opensmsforwarder.model.Rule
import javax.inject.Inject

class Mapper @Inject constructor() {

    fun toRecipient(recipientEntity: RecipientEntity): Recipient {
        return Recipient(
            id = recipientEntity.id,
            title = recipientEntity.title,
            forwardingType = recipientEntity.forwardingType,
            recipientPhone = recipientEntity.recipientPhone,
            senderEmail = recipientEntity.senderEmail,
            recipientEmail = recipientEntity.recipientEmail,
            isForwardSuccessful = recipientEntity.isForwardSuccessful
        )
    }

    fun toRecipientEntity(recipient: Recipient): RecipientEntity =
        RecipientEntity(
            id = recipient.id,
            title = recipient.title,
            forwardingType = recipient.forwardingType,
            recipientPhone = recipient.recipientPhone,
            senderEmail = recipient.senderEmail,
            recipientEmail = recipient.recipientEmail,
            isForwardSuccessful = recipient.isForwardSuccessful
        )

    fun toRuleEntity(rule: Rule) =
        RuleEntity(
            id = rule.id,
            recipientId = rule.recipientId,
            rule = rule.textRule
        )

    fun toRule(ruleEntity: RuleEntity) =
        Rule(
            id = ruleEntity.id,
            recipientId = ruleEntity.recipientId,
            textRule = ruleEntity.rule
        )

    fun fromRecipientToForwardedSmsEntity(
        recipient: Recipient,
        time: Long,
        message: String,
        isForwardSuccessful: Boolean,
    ): ForwardedSmsEntity =
        ForwardedSmsEntity(
            date = time,
            title = recipient.title,
            forwardingType = recipient.forwardingType,
            recipientPhone = recipient.recipientPhone,
            senderEmail = recipient.senderEmail,
            recipientEmail = recipient.recipientEmail,
            message = message,
            isForwardSuccessful = isForwardSuccessful
        )
}
