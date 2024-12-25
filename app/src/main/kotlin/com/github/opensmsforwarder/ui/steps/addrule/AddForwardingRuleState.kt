package com.github.opensmsforwarder.ui.steps.addrule

import androidx.annotation.StringRes
import com.github.opensmsforwarder.domain.model.Rule

data class AddForwardingRuleState(
    val rules: List<Rule> = emptyList(),
    val isAddRuleButtonEnabled: Boolean = false,
    @StringRes val errorMessage: Int? = null
)
