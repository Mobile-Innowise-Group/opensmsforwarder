package com.github.opensmsforwarder

import androidx.lifecycle.ViewModel
import com.github.opensmsforwarder.analytics.AnalyticsEvents.HOME_SCREEN_START
import com.github.opensmsforwarder.analytics.AnalyticsEvents.ONBOARDING_SCREEN_START
import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.data.LocalSettingsRepository
import com.github.opensmsforwarder.navigation.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val localSettingsRepository: LocalSettingsRepository,
    private val router: Router,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    fun onInit(containerChildCount: Int) {
        if (containerChildCount == 0) {
            val startScreen = if (localSettingsRepository.isOnboardingCompleted()) {
                analyticsTracker.trackEvent(HOME_SCREEN_START)
                Screens.homeFragment()
            } else {
                analyticsTracker.trackEvent(ONBOARDING_SCREEN_START)
                Screens.onboardingFragment()
            }
            router.replaceScreen(startScreen)
        }
    }
}
