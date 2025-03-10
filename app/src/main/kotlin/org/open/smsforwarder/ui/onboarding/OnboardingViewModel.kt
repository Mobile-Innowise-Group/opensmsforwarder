package org.open.smsforwarder.ui.onboarding

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import org.open.smsforwarder.R
import org.open.smsforwarder.analytics.AnalyticsEvents.ONBOARDING_COMPLETE
import org.open.smsforwarder.analytics.AnalyticsEvents.ONBOARDING_WARNING_SHOW
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.LocalSettingsRepository
import org.open.smsforwarder.navigation.Screens
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val localSettingsRepository: LocalSettingsRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val router: Router,
) : ViewModel() {

    private val _viewState = MutableStateFlow(OnboardingState())
    val viewState = _viewState.asStateFlow()

    private val _viewEffect: Channel<OnboardingEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<OnboardingEffect> = _viewEffect.receiveAsFlow()

    fun onSlidePage(itemPosition: Int) {
        val nextButtonRes = if (viewState.value.isLastItem(itemPosition)) {
            R.string.onboarding_fragment_finish
        } else {
            R.string.onboarding_fragment_next
        }
        _viewState.update {
            it.copy(
                isLastSlide = viewState.value.isLastItem(itemPosition),
                nextButtonRes = nextButtonRes,
                currentStep = itemPosition + 1
            )
        }
    }

    fun onNextButtonClicked(itemPosition: Int, potentialRisksAcknowledged: Boolean) {
        if (viewState.value.isLastItem(itemPosition)) {
            if (potentialRisksAcknowledged) {
                localSettingsRepository.setOnboardingCompleted()
                analyticsTracker.trackEvent(ONBOARDING_COMPLETE)
                router.replaceScreen(Screens.homeFragment())
            } else {
                analyticsTracker.trackEvent(ONBOARDING_WARNING_SHOW)
                _viewEffect.trySend(WarningEffect)
            }
        } else {
            _viewEffect.trySend(NextPageEffect)
        }
    }

    fun onPreviousButtonClicked() {
        _viewEffect.trySend(PreviousPageEffect)
    }

    fun onSkipAllButtonClicked() {
        _viewEffect.trySend(SkipAllEffect)
    }
}
