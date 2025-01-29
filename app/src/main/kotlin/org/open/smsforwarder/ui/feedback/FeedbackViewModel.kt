package org.open.smsforwarder.ui.feedback

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.open.smsforwarder.R
import org.open.smsforwarder.data.repository.FeedbackRepository
import org.open.smsforwarder.domain.usecase.ValidateEmailUseCase
import org.open.smsforwarder.utils.Resources
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val router: Router,
    private val feedbackRepository: FeedbackRepository,
    private val validateEmailUseCase: ValidateEmailUseCase
) : ViewModel() {

    private var _viewState = MutableStateFlow(FeedbackState())
    val viewState = _viewState.asStateFlow()

    fun onBackClick() = router.exit()

    fun onSubmitClick(
        email: String,
        body: String,
        callback: (Boolean) -> Unit,
    ) {
        feedbackRepository.sendFeedback(email, body, callback)
        router.exit()
    }

    fun onEmailChanged(email: String) {
        val emailValidationResult = validateEmailUseCase.execute(email)
        _viewState.update {
            it.copy(
                emailInputError = emailValidationResult.errorMessage
            )
        }
    }

    fun onBodyChanged(body: String) {
        _viewState.update { state ->
            state.copy(
                bodyInputError = if (body.isBlank()) {
                    Resources.StringResource(R.string.feedback_error_blank_body)
                } else {
                    null
                }
            )
        }
    }
}
