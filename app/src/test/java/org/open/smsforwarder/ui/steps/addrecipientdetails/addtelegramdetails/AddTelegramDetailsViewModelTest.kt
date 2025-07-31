package org.open.smsforwarder.ui.steps.addrecipientdetails.addtelegramdetails

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.argThat
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.domain.ValidationError
import org.open.smsforwarder.domain.ValidationResult
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.domain.usecase.ValidateBlankFieldUseCase
import org.open.smsforwarder.navigation.Screens
import org.open.smsforwarder.ui.mapper.toDomain
import org.open.smsforwarder.utils.awaitInitialAction

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AddTelegramDetailsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    lateinit var repository: ForwardingRepository

    @Mock
    lateinit var router: com.github.terrakok.cicerone.Router

    @Mock
    lateinit var validateBlankFieldUseCase: ValidateBlankFieldUseCase

    private lateinit var viewModel: AddTelegramDetailsViewModel
    private val testId = 42L

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(repository.getForwardingByIdFlow(testId))
            .thenReturn(
                MutableStateFlow(
                    Forwarding(
                        id = testId,
                        forwardingType = ForwardingType.TELEGRAM
                    )
                )
            )

        viewModel = AddTelegramDetailsViewModel(
            id = testId,
            forwardingRepository = repository,
            validateBlankFieldUseCase = validateBlankFieldUseCase,
            router = router
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onTelegramApiTokenChanged - updates state with validation error`() = runTest {
        whenever(validateBlankFieldUseCase.execute("")).thenReturn(
            ValidationResult(
                successful = false,
                errorType = ValidationError.BLANK_FIELD
            )
        )
        awaitInitialAction(viewModel.viewState) {
            viewModel.onTelegramApiTokenChanged("")

            val state = viewModel.viewState.value
            assertEquals("", state.telegramApiToken)
            assertNotNull(state.inputErrorApiToken)
        }
    }

    @Test
    fun `onTelegramChatIdChanged - updates state with validation error`() = runTest {
        whenever(validateBlankFieldUseCase.execute("chatId")).thenReturn(ValidationResult(successful = true))
        awaitInitialAction(viewModel.viewState) {
            viewModel.onTelegramChatIdChanged("chatId")

            val state = viewModel.viewState.value
            assertEquals("chatId", state.telegramChatId)
            assertNull(state.inputErrorChatId)
        }
    }

    @Test
    fun `onNextClicked - navigates to addForwardingRuleFragment`() = runTest {
        viewModel.onNextClicked()
        verify(router).navigateTo(argThat { screenKey == Screens.addForwardingRuleFragment(testId).screenKey })
    }

    @Test
    fun `onBackClicked - exits router`() = runTest {
        viewModel.onBackClicked()
        verify(router).exit()
    }

    @Test
    fun `onPause - saves current state to repository`() = runTest {
        val owner = mock<LifecycleOwner>()
        val expected = viewModel.viewState.value.toDomain()

        viewModel.onPause(owner)

        verify(repository).insertOrUpdateForwarding(expected)
    }
}
