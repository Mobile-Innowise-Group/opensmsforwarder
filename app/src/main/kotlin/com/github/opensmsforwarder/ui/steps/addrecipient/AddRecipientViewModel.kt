package com.github.opensmsforwarder.ui.steps.addrecipient

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.data.AuthRepository
import com.github.opensmsforwarder.data.RecipientsRepository
import com.github.opensmsforwarder.helper.GoogleSignInHelper
import com.github.opensmsforwarder.navigation.Screens
import com.github.opensmsforwarder.processing.validator.EmailValidator
import com.github.opensmsforwarder.processing.validator.PhoneValidator
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRecipientViewModel @Inject constructor(
    private val recipientsRepository: RecipientsRepository,
    private val authRepository: AuthRepository,
    private val googleSignInHelper: GoogleSignInHelper,
    private val phoneValidator: PhoneValidator,
    private val emailValidator: EmailValidator,
    private val router: Router,
) : ViewModel() {

    private var _viewState: MutableStateFlow<AddRecipientViewState> =
        MutableStateFlow(AddRecipientViewState())
    val viewState: StateFlow<AddRecipientViewState> = _viewState.asStateFlow()

    private val _viewEffect: Channel<AddRecipientViewEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<AddRecipientViewEffect> = _viewEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            recipientsRepository
                .getCurrentRecipientFlow()
                .collect { recipient ->
                    _viewState.update {
                        it.copy(recipient = recipient)
                    }
                }
        }
    }

    fun onSignInClicked() {
        _viewEffect.trySend(GoogleSignInViewEffect(signInIntent = googleSignInHelper.signInIntent))
    }

    fun onSignOutClicked() {
        viewModelScope.launch {
            authRepository
                .signOut(viewState.value.recipient)
                .onFailure {
                    _viewEffect.trySend(GoogleSignInErrorViewEffect(R.string.error_google_sign_out))
                }
        }
    }

    fun onPhoneChanged(phone: String) {
        val isValid = phoneValidator.isValid(phone)
        val phoneInputError = if (!isValid && phone.isNotBlank()) {
            R.string.error_phone_number_is_not_valid
        } else {
            null
        }
        _viewState.update {
            it.copy(
                recipient = it.recipient.copy(recipientPhone = phone),
                phoneInputError = phoneInputError
            )
        }
    }

    fun onEmailChanged(email: String) {
        val isValid = emailValidator.isValid(email)
        val emailInputError = if (!isValid && email.isNotBlank()) {
            R.string.error_email_is_not_valid
        } else {
            null
        }
        _viewState.update {
            it.copy(
                recipient = it.recipient.copy(recipientEmail = email),
                emailInputError = emailInputError
            )
        }
    }

    fun onSignInResultReceived(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            viewModelScope.launch {
                googleSignInHelper
                    .getGoogleSignInAccount(data = result.data)
                    .onSuccess { googleAccount ->
                        authRepository
                            .exchangeAuthCodeForTokensAnd(googleAccount.serverAuthCode)
                            .onSuccess { response ->
                                val recipient = viewState.value.recipient.copy(
                                    senderEmail = googleAccount.email,
                                    isForwardSuccessful = true
                                )
                                recipientsRepository.insertOrUpdateRecipient(recipient)
                                authRepository.saveTokensForRecipient(
                                    viewState.value.recipient.id,
                                    response.accessToken,
                                    response.refreshToken
                                )
                            }
                            .onFailure {
                                _viewEffect.trySend(
                                    GoogleSignInErrorViewEffect(R.string.error_google_sign_in)
                                )
                            }
                    }
                    .onFailure {
                        _viewEffect.trySend(
                            GoogleSignInErrorViewEffect(R.string.error_google_sign_in)
                        )
                    }
            }
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onNextClicked() {
        viewModelScope.launch {
            recipientsRepository.insertOrUpdateRecipient(viewState.value.recipient)
            router.navigateTo(Screens.addForwardingRuleFragment())
        }
    }
}
