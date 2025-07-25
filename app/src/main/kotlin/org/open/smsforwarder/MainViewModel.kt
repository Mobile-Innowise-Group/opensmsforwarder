package org.open.smsforwarder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.open.smsforwarder.analytics.AnalyticsEvents.HOME_SCREEN_START
import org.open.smsforwarder.analytics.AnalyticsEvents.ONBOARDING_SCREEN_START
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.LocalSettingsRepository
import org.open.smsforwarder.domain.NetworkStateObserver
import org.open.smsforwarder.navigation.Screens
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5000L

@HiltViewModel
class MainViewModel @Inject constructor(
    private val localSettingsRepository: LocalSettingsRepository,
    private val router: Router,
    private val analyticsTracker: AnalyticsTracker,
    networkStateObserver: NetworkStateObserver,
) : ViewModel() {

    val networkStatus: StateFlow<Boolean> = networkStateObserver.networkStatus
        .map { it.isOnline() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = networkStateObserver.networkStatus.value.isOnline()
        )

    fun onInit(containerChildCount: Int) {
        if (containerChildCount == 0) {
            when (localSettingsRepository.isOnboardingCompleted()) {
                true -> {
                    analyticsTracker.trackEvent(HOME_SCREEN_START)
                    router.replaceScreen(Screens.homeFragment())
                }

                false -> {
                    analyticsTracker.trackEvent(ONBOARDING_SCREEN_START)
                    router.replaceScreen(Screens.onboardingFragment())
                }
            }
        }
    }
}
