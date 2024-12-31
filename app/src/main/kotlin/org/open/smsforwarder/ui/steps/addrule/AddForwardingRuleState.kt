package org.open.smsforwarder.ui.steps.addrule

import androidx.annotation.StringRes
import org.open.smsforwarder.domain.model.Rule

data class AddForwardingRuleState(
    val rules: List<Rule> = emptyList(),
    val isAddRuleButtonEnabled: Boolean = false,
    @StringRes val errorMessage: Int? = null
)
