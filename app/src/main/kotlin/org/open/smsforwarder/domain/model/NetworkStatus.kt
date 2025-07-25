package org.open.smsforwarder.domain.model

sealed class NetworkStatus {
    data object Available : NetworkStatus()
    data object Unavailable : NetworkStatus()
    data object Losing : NetworkStatus()
    data object Lost : NetworkStatus()

    fun isOnline(): Boolean = when (this) {
        Available, Losing -> true
        Lost, Unavailable -> false
    }
}
