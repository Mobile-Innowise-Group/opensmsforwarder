package com.github.opensmsforwarder.ui.steps.addrule

import com.github.opensmsforwarder.model.Rule

sealed class ForwardingRuleEffect

data class ForwardingDeleteRuleEffect(
    val rule: Rule
) : ForwardingRuleEffect()

data class ForwardingEditRuleEffect(
    val rule: Rule
) : ForwardingRuleEffect()
