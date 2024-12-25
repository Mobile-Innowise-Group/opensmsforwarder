package com.github.opensmsforwarder

import androidx.lifecycle.ViewModel
import com.github.opensmsforwarder.analytics.AnalyticsEvents.HOME_SCREEN_START
import com.github.opensmsforwarder.analytics.AnalyticsEvents.ONBOARDING_SCREEN_START
import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.data.repository.LocalSettingsRepository
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
