package org.open.smsforwarder.ui.steps.choosemethod

import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.argThat
import org.mockito.quality.Strictness
import org.open.smsforwarder.analytics.AnalyticsEvents
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.navigation.Screens
import org.open.smsforwarder.utils.awaitInitialAction

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChooseForwardingMethodViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    lateinit var forwardingRepository: ForwardingRepository

    @Mock
    lateinit var analyticsTracker: AnalyticsTracker

    @Mock
    lateinit var router: Router

    private lateinit var viewModel: ChooseForwardingMethodViewModel

    private val testId = 123L

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        `when`(forwardingRepository.getForwardingByIdFlow(testId))
            .thenReturn(flowOf(Forwarding(id = testId)))

        viewModel = ChooseForwardingMethodViewModel(
            id = testId,
            forwardingRepository = forwardingRepository,
            analyticsTracker = analyticsTracker,
            router = router
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onTitleChanged - updates view state with new title`() = runTest {
        awaitInitialAction(viewModel.viewState) {
            viewModel.onTitleChanged("My Forwarding")

            val state = viewModel.viewState.value
            assertEquals("My Forwarding", state.title)
        }
    }

    @Test
    fun `onForwardingMethodChanged - updates view state with new method`() = runTest {
        awaitInitialAction(viewModel.viewState) {
            viewModel.onForwardingMethodChanged(ForwardingType.EMAIL)
            val state = viewModel.viewState.value
            assertEquals(ForwardingType.EMAIL, state.forwardingType)
        }
    }

    @Test
    fun `onNextClicked - with EMAIL method navigates to email screen`() = runTest {
        awaitInitialAction(viewModel.viewState) {
            viewModel.onForwardingMethodChanged(ForwardingType.EMAIL)
            viewModel.onNextClicked()

            verify(analyticsTracker).trackEvent(AnalyticsEvents.RECIPIENT_CREATION_STEP1_NEXT_CLICKED)
            verify(router).navigateTo(argThat { screenKey == Screens.addEmailDetailsFragment(testId).screenKey })
        }
    }

    @Test
    fun `onNextClicked - with TELEGRAM method navigates to telegram screen`() = runTest {
        awaitInitialAction(viewModel.viewState) {
            viewModel.onForwardingMethodChanged(ForwardingType.TELEGRAM)
            viewModel.onNextClicked()

            verify(analyticsTracker).trackEvent(AnalyticsEvents.RECIPIENT_CREATION_STEP1_NEXT_CLICKED)
            verify(router).navigateTo(argThat {
                screenKey == Screens.addTelegramDetailsFragment(
                    testId
                ).screenKey
            })
        }
    }

    @Test
    fun `onNextClicked - with null method does not navigate`() = runTest {
        awaitInitialAction(viewModel.viewState) {
            viewModel.onForwardingMethodChanged(null)
            viewModel.onNextClicked()

            verify(
                analyticsTracker,
                org.mockito.Mockito.never()
            ).trackEvent(AnalyticsEvents.RECIPIENT_CREATION_STEP1_NEXT_CLICKED)
            verify(router, org.mockito.Mockito.never()).navigateTo(org.mockito.kotlin.any())
        }
    }

    @Test
    fun `onBackClicked - calls router exit`() = runTest {
        viewModel.onBackClicked()
        verify(router).exit()
    }
}
