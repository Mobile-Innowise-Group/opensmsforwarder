package org.open.smsforwarder.ui.steps.addrule

import org.open.smsforwarder.domain.model.Rule

sealed class ForwardingRuleEffect

data class ForwardingDeleteRuleEffect(
    val rule: Rule
) : ForwardingRuleEffect()

data class ForwardingEditRuleEffect(
    val rule: Rule
) : ForwardingRuleEffect()
