package com.github.opensmsforwarder.ui.onboarding

import androidx.lifecycle.ViewModel
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.analytics.AnalyticsEvents.ONBOARDING_COMPLETE
import com.github.opensmsforwarder.analytics.AnalyticsEvents.ONBOARDING_WARNING_SHOW
import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.data.LocalSettingsRepository
import com.github.opensmsforwarder.navigation.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val localSettingsRepository: LocalSettingsRepository,
    private val router: Router,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val _viewState: MutableStateFlow<OnboardingState> = MutableStateFlow(OnboardingState())
    val viewState: StateFlow<OnboardingState> = _viewState.asStateFlow()

    private val _viewEffect: Channel<OnboardingEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<OnboardingEffect> = _viewEffect.receiveAsFlow()

    fun onSlidePage(position: Int) {
        val isLastSlide = position == OnboardingState.slides.size - 1
        val nextButtonRes = if (isLastSlide) {
            R.string.onboarding_fragment_finish
        } else {
            R.string.onboarding_fragment_next
        }
        _viewState.update {
            it.copy(
                isLastSlide = isLastSlide,
                nextButtonRes = nextButtonRes,
                currentStep = position + 1
            )
        }
    }

    fun onNextButtonClicked(currentItem: Int, potentialRisksAcknowledged: Boolean) {
        if (currentItem == OnboardingState.slides.size - 1) {
            if (potentialRisksAcknowledged) {
                localSettingsRepository.setOnboardingCompleted(true)
                analyticsTracker.trackEvent(ONBOARDING_COMPLETE)
                router.replaceScreen(Screens.homeFragment())
            } else {
                analyticsTracker.trackEvent(ONBOARDING_WARNING_SHOW)
                _viewEffect.trySend(WarningEffect)
            }
        } else {
            _viewEffect.trySend(NextEffect)
        }
    }

    fun onPreviousButtonClicked() {
        _viewEffect.trySend(PreviousEffect)
    }

    fun onSkipAllButtonClicked() {
        _viewEffect.trySend(SkipAllEffect)
    }
}
