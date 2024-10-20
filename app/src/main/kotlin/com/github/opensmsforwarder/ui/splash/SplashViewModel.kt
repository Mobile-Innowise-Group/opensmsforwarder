package com.github.opensmsforwarder.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.opensmsforwarder.analytics.AnalyticsEvents.HOME_SCREEN_DIRECT_NAVIGATION
import com.github.opensmsforwarder.analytics.AnalyticsEvents.ONBOARDING_START
import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.data.LocalSettingsRepository
import com.github.opensmsforwarder.navigation.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val localSettingsRepository: LocalSettingsRepository,
    private val router: Router,
    private val analyticsTracker: AnalyticsTracker
) : ViewModel() {

    fun onScreenInit() {
        viewModelScope.launch {
            delay(SPLASH_DELAY)
            val screen = if (localSettingsRepository.getOnboardingCompleteFlag()) {
                analyticsTracker.trackEvent(HOME_SCREEN_DIRECT_NAVIGATION)
                Screens.homeFragment()
            } else {
                analyticsTracker.trackEvent(ONBOARDING_START)
                Screens.onboardingFragment()
            }
            router.replaceScreen(screen)
        }
    }

    private companion object {
        const val SPLASH_DELAY = 2000L
    }
}
