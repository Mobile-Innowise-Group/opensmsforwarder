package org.open.smsforwarder

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import org.open.smsforwarder.analytics.AnalyticsEvents.HOME_SCREEN_START
import org.open.smsforwarder.analytics.AnalyticsEvents.ONBOARDING_SCREEN_START
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.LocalSettingsRepository
import org.open.smsforwarder.navigation.Screens
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val localSettingsRepository: LocalSettingsRepository,
    private val router: Router,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

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
