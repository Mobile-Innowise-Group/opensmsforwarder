package com.github.opensmsforwarder.ui.home

import com.github.opensmsforwarder.model.Recipient
import com.github.opensmsforwarder.model.Rule

data class HomeState(
    val recipients: List<Recipient> = emptyList(),
    val rules: List<Rule> = emptyList(),
)
