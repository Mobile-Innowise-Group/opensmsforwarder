package org.open.smsforwarder.ui.home

import org.open.smsforwarder.ui.model.ForwardingUI

data class HomeState(
    val forwardings: List<ForwardingUI> = emptyList(),
    val needToShowPermissionPermanentInfo: Boolean = false
) {

    fun hasAtLeastOneCompletedItem(): Boolean = forwardings.any(ForwardingUI::allStepsCompleted)
}
