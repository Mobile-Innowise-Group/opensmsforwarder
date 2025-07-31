package org.open.smsforwarder.data

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import org.open.smsforwarder.domain.NetworkStateObserver
import org.open.smsforwarder.domain.model.NetworkStatus
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5000L

class NetworkStateObserverImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    coroutineScope: CoroutineScope
) : NetworkStateObserver {

    private var currentNetwork: Network? = null

    override val networkStatus: StateFlow<NetworkStatus> = callbackFlow {

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return
                if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    currentNetwork = network
                    trySend(NetworkStatus.Available)
                }
            }

            override fun onUnavailable() {
                trySend(NetworkStatus.Unavailable)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                if (network == currentNetwork) {
                    trySend(NetworkStatus.Losing)
                }
            }

            override fun onLost(network: Network) {
                if (network == currentNetwork) {
                    currentNetwork = null
                    trySend(NetworkStatus.Lost)
                }
            }
        }

        val request: NetworkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }

    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = getCurrentStatus()
    )

    private fun getCurrentStatus(): NetworkStatus {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return if (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
            NetworkStatus.Available
        } else {
            NetworkStatus.Unavailable
        }
    }
}
