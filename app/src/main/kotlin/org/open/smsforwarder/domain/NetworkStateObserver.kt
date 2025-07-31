package org.open.smsforwarder.domain

import kotlinx.coroutines.flow.StateFlow
import org.open.smsforwarder.domain.model.NetworkStatus

interface NetworkStateObserver {
    val networkStatus: StateFlow<NetworkStatus>
}
