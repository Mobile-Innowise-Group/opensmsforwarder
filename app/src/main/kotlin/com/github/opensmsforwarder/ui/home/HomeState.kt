package com.github.opensmsforwarder.ui.home

import com.github.opensmsforwarder.ui.model.ForwardingUI

data class HomeState(
    val forwardings: List<ForwardingUI> = emptyList(),
    val needToShowPermissionPermanentInfo: Boolean = false
) {

    fun hasAtLeastOneCompletedItem(): Boolean = forwardings.any(ForwardingUI::allStepsCompleted)
}
