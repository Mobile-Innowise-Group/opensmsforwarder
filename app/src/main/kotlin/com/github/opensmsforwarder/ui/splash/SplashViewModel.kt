package com.github.opensmsforwarder.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    fun checkOnboardingCompleteFlag() {
        viewModelScope.launch {
            val isOnboardingComplete = localSettingsRepository.getOnboardingCompleteFlag()
            delay(SPLASH_DELAY)
            if (isOnboardingComplete) {
                router.replaceScreen(Screens.homeFragment())
            } else {
                router.replaceScreen(Screens.onboardingFragment())
            }
        }
    }

    companion object {
        private const val SPLASH_DELAY = 2000L
    }
}
