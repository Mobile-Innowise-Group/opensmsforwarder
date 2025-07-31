package org.open.smsforwarder.ui.steps.addrule

import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import org.open.smsforwarder.R
import org.open.smsforwarder.analytics.AnalyticsEvents
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.RulesRepository
import org.open.smsforwarder.domain.model.Rule
import org.open.smsforwarder.utils.awaitInitialAction

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class AddForwardingRuleViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    lateinit var rulesRepository: RulesRepository

    @Mock
    lateinit var router: Router

    @Mock
    lateinit var analyticsTracker: AnalyticsTracker

    private lateinit var viewModel: AddForwardingRuleViewModel
    private val testId = 123L

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = AddForwardingRuleViewModel(
            id = testId,
            rulesRepository = rulesRepository,
            router = router,
            analyticsTracker = analyticsTracker
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onNewRuleEntered - enables button when input is valid and not duplicate`() = runTest {
        whenever(rulesRepository.getRulesByForwardingIdFlow(testId))
            .thenReturn(MutableStateFlow(emptyList()))
        awaitInitialAction(viewModel.viewState) {
            viewModel.onNewRuleEntered("valid-rule")
            val state = viewModel.viewState.value
            assertEquals(true, state.isAddRuleButtonEnabled)
            assertEquals(null, state.errorMessage)
        }
    }

    @Test
    fun `onNewRuleEntered - disables button when input is blank`() = runTest {
        viewModel.onNewRuleEntered("  ")
        val state = viewModel.viewState.value
        assertEquals(false, state.isAddRuleButtonEnabled)
    }

    @Test
    fun `onNewRuleEntered - disables button and sets error when rule already exists`() = runTest {
        val existingRule = Rule(id = 1L, textRule = "duplicate", forwardingId = testId)
        whenever(rulesRepository.getRulesByForwardingIdFlow(testId))
            .thenReturn(MutableStateFlow(listOf(existingRule)))

        awaitInitialAction(viewModel.viewState) {
            viewModel.onNewRuleEntered("duplicate")
            val state = viewModel.viewState.value
            assertEquals(false, state.isAddRuleButtonEnabled)
            assertEquals(R.string.add_rule_error, state.errorMessage)
        }
    }

    @Test
    fun `onFinishClicked - tracks event and navigates backTo null`() = runTest {
        viewModel.onFinishClicked()
        verify(analyticsTracker).trackEvent(AnalyticsEvents.RECIPIENT_CREATION_FINISHED)
        verify(router).backTo(null)
    }

    @Test
    fun `onBackClicked - exits router`() = runTest {
        viewModel.onBackClicked()
        verify(router).exit()
    }

    @Test
    fun `onAddRuleClicked - inserts rule and tracks analytics`() = runTest {
        viewModel.onAddRuleClicked("new-rule")

        val expected = Rule(forwardingId = testId, textRule = "new-rule")
        verify(rulesRepository).insertRule(expected)
        verify(analyticsTracker).trackEvent(AnalyticsEvents.RULE_ADD_CLICKED)
    }

    @Test
    fun `onItemEditClicked - sends ForwardingEditRuleEffect`() = runTest {
        val rule = Rule(id = 42L, textRule = "to-delete", forwardingId = testId)

        viewModel.onItemRemoveClicked(rule)

        val effect = viewModel.viewEffect.take(1).toList().first()

        assertTrue(effect is ForwardingDeleteRuleEffect)
        assertEquals(rule, (effect as ForwardingDeleteRuleEffect).rule)
    }

    @Test
    fun `onItemEdited - updates existing rule`() = runTest {
        val existing = Rule(id = 1L, textRule = "old", forwardingId = testId)
        whenever(rulesRepository.getRulesByForwardingIdFlow(testId))
            .thenReturn(MutableStateFlow(listOf(existing)))

        awaitInitialAction(viewModel.viewState) {
            viewModel.onItemEdited(1L, "updated")

            val updated = existing.copy(textRule = "updated")
            verify(rulesRepository).insertRule(updated)
        }
    }

    @Test
    fun `onItemRemoved - deletes rule by id`() = runTest {
        viewModel.onItemRemoved(999L)
        verify(rulesRepository).deleteRule(999L)
    }

    @Test
    fun `onItemRemoveClicked - sends ForwardingDeleteRuleEffect`() = runTest {
        val rule = Rule(id = 42L, textRule = "to-delete", forwardingId = testId)

        viewModel.onItemRemoveClicked(rule)

        val effect = viewModel.viewEffect.take(1).toList().first()

        assertTrue(effect is ForwardingDeleteRuleEffect)
        assertEquals(rule, (effect as ForwardingDeleteRuleEffect).rule)
    }
}
