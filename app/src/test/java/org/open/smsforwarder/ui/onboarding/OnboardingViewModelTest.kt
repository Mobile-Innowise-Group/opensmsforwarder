import com.github.terrakok.cicerone.Router

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argThat
import org.open.smsforwarder.R
import org.open.smsforwarder.analytics.AnalyticsEvents
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.LocalSettingsRepository
import org.open.smsforwarder.navigation.Screens
import org.open.smsforwarder.ui.onboarding.NextPageEffect
import org.open.smsforwarder.ui.onboarding.OnboardingViewModel
import org.open.smsforwarder.ui.onboarding.PreviousPageEffect
import org.open.smsforwarder.ui.onboarding.SkipAllEffect
import org.open.smsforwarder.ui.onboarding.WarningEffect

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class OnboardingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    lateinit var localSettingsRepository: LocalSettingsRepository

    @Mock
    lateinit var analyticsTracker: AnalyticsTracker

    @Mock
    lateinit var router: Router

    private lateinit var viewModel: OnboardingViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OnboardingViewModel(localSettingsRepository, analyticsTracker, router)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSlidePage - updates state correctly when not last page`() = runTest {
        viewModel.onSlidePage(0)
        val state = viewModel.viewState.first()
        assertEquals(1, state.currentStep)
        assertEquals(R.string.onboarding_fragment_next, state.nextButtonRes)
        assertEquals(false, state.isLastSlide)
    }

    @Test
    fun `onSlidePage - updates state correctly when last page`() = runTest {
        viewModel.onSlidePage(4)
        val state = viewModel.viewState.first()
        assertEquals(5, state.currentStep)
        assertEquals(R.string.onboarding_fragment_finish, state.nextButtonRes)
        assertEquals(true, state.isLastSlide)
    }

    @Test
    fun `onNextButtonClicked - last page with acknowledgment - completes onboarding`() = runTest {
        viewModel.onSlidePage(4)
        viewModel.onNextButtonClicked(4, true)

        verify(localSettingsRepository).setOnboardingCompleted()
        verify(analyticsTracker).trackEvent(AnalyticsEvents.ONBOARDING_COMPLETE)
        verify(router).replaceScreen(argThat { screen ->
            screen.screenKey == Screens.homeFragment().screenKey
        })
    }

    @Test
    fun `onNextButtonClicked - last page without acknowledgment - sends warning`() = runTest {
        viewModel.onSlidePage(3)
        viewModel.onNextButtonClicked(4, false)

        val effect = viewModel.viewEffect.first()
        verify(analyticsTracker).trackEvent(AnalyticsEvents.ONBOARDING_WARNING_SHOW)
        assertEquals(WarningEffect, effect)
    }

    @Test
    fun `onNextButtonClicked - not last page - sends NextPageEffect`() = runTest {
        viewModel.onNextButtonClicked(0, true)
        val effect = viewModel.viewEffect.first()
        assertEquals(NextPageEffect, effect)
    }

    @Test
    fun `onPreviousButtonClicked - sends PreviousPageEffect`() = runTest {
        viewModel.onPreviousButtonClicked()
        val effect = viewModel.viewEffect.first()
        assertEquals(PreviousPageEffect, effect)
    }

    @Test
    fun `onSkipAllButtonClicked - sends SkipAllEffect`() = runTest {
        viewModel.onSkipAllButtonClicked()
        val effect = viewModel.viewEffect.first()
        assertEquals(SkipAllEffect, effect)
    }
}
