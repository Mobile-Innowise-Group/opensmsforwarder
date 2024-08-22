package com.github.opensmsforwarder.ui.onboarding

import androidx.lifecycle.ViewModel
import com.github.opensmsforwarder.R
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
) : ViewModel() {

    private val _viewState: MutableStateFlow<OnboardingState> = MutableStateFlow(OnboardingState())
    val viewState: StateFlow<OnboardingState> = _viewState.asStateFlow()

    private val _viewEffect: Channel<OnboardingEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<OnboardingEffect> = _viewEffect.receiveAsFlow()

    fun onSlidePage(position: Int, itemCount: Int) {
        val isLastSlide = position == itemCount - 1
        val isFirstSlide = position == FIRST_SLIDE_INDEX
        val isPreFinalSlide = position == itemCount - 2
        val nextButtonRes = if (isLastSlide) {
            R.string.onboarding_fragment_finish
        } else {
            R.string.onboarding_fragment_next
        }
        _viewState.update {
            it.copy(
                isBackButtonVisible = !isFirstSlide,
                isLastSlide =  isLastSlide,
                isPreFinalSlide = isPreFinalSlide,
                nextButtonRes = nextButtonRes
            )
        }
    }

    fun onFinishOnboarding(isOnboardingCompleted: Boolean) {
        if (isOnboardingCompleted) {
            localSettingsRepository.setOnboardingCompleteFlag(true)
            router.replaceScreen(Screens.homeFragment())
        } else {
            _viewEffect.trySend(WarningEffect)
        }
    }

    private companion object {
        const val FIRST_SLIDE_INDEX = 0
    }
}
