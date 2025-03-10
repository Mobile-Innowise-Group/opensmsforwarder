package org.open.smsforwarder.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.open.smsforwarder.R
import org.open.smsforwarder.data.repository.FeedbackRepository
import org.open.smsforwarder.domain.usecase.ValidateBlankFieldUseCase
import org.open.smsforwarder.domain.usecase.ValidateEmailUseCase
import org.open.smsforwarder.extension.getErrorStringProvider
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val router: Router,
    private val feedbackRepository: FeedbackRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validateBlankFieldUseCase: ValidateBlankFieldUseCase
) : ViewModel() {

    private var _viewState = MutableStateFlow(FeedbackState())
    val viewState = _viewState.asStateFlow()

    private val _viewEffect: Channel<FeedbackEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<FeedbackEffect> = _viewEffect.receiveAsFlow()

    fun onBackClicked() {
        router.exit()
    }

    fun onSubmitClicked(
        email: String,
        body: String,
    ) {
        viewModelScope.launch {
            val result = feedbackRepository.sendFeedback(email, body)
            val messageRes = when (result) {
               true -> R.string.feedback_success
               false -> R.string.feedback_failure
            }
            _viewEffect.trySend(SubmitResultEffect(messageRes))
            router.exit()
        }
    }

    fun onEmailChanged(email: String) {
        val emailValidationResult = validateEmailUseCase.execute(email)
        _viewState.update {
            it.copy(
                emailInput = email,
                emailInputErrorProvider = emailValidationResult.errorType?.getErrorStringProvider()
            )
        }
    }

    fun onBodyChanged(body: String) {
        val bodyValidationResult = validateBlankFieldUseCase.execute(body)
        _viewState.update { state ->
            state.copy(
                bodyInput = body,
                bodyInputErrorProvider = bodyValidationResult.errorType?.getErrorStringProvider()
            )
        }
    }
}
