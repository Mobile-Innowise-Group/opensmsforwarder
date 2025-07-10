package org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.open.smsforwarder.R
import org.open.smsforwarder.analytics.AnalyticsEvents.RECIPIENT_CREATION_STEP2_NEXT_CLICKED
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.AuthRepository
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.domain.usecase.ValidateEmailUseCase
import org.open.smsforwarder.extension.asStateFlowWithInitialAction
import org.open.smsforwarder.extension.getStringProvider
import org.open.smsforwarder.extension.launchAndCancelPrevious
import org.open.smsforwarder.helper.GoogleSignInHelper
import org.open.smsforwarder.navigation.Screens
import org.open.smsforwarder.ui.mapper.toDomain
import org.open.smsforwarder.ui.mapper.toEmailDetailsUi

@HiltViewModel(assistedFactory = AddEmailDetailsViewModel.Factory::class)
class AddEmailDetailsViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val forwardingRepository: ForwardingRepository,
    private val authRepository: AuthRepository,
    private val googleSignInHelper: GoogleSignInHelper,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val router: Router,
) : ViewModel(), DefaultLifecycleObserver {

    private var _viewState = MutableStateFlow(AddEmailDetailsState())
    val viewState = _viewState.asStateFlowWithInitialAction(viewModelScope) { loadData() }

    private val _viewEffect: Channel<AddEmailDetailsViewEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<AddEmailDetailsViewEffect> = _viewEffect.receiveAsFlow()

    override fun onPause(owner: LifecycleOwner) {
        viewModelScope.launch {
            forwardingRepository.insertOrUpdateForwarding(viewState.value.toDomain())
        }
        super.onPause(owner)
    }

    private fun loadData() {
        launchAndCancelPrevious {
            forwardingRepository
                .getForwardingByIdFlow(id)
                .collect { forwarding ->
                    _viewState.update {
                        forwarding.toEmailDetailsUi()
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
                .signOut(viewState.value.toDomain())
                .onFailure {
                    _viewEffect.trySend(
                        GoogleSignInErrorViewEffect(R.string.error_google_sign_out)
                    )
                }
        }
    }

    fun onEmailChanged(email: String) {
        val emailValidationResult = validateEmailUseCase.execute(email)
        _viewState.update {
            it.copy(
                recipientEmail = email,
                inputErrorProvider = emailValidationResult.errorType?.getStringProvider()
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
                                val recipient = viewState.value.copy(
                                    senderEmail = googleAccount.email
                                )
                                forwardingRepository.insertOrUpdateForwarding(recipient.toDomain())
                                authRepository.saveTokens(
                                    viewState.value.id,
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

    fun onNextClicked() {
        analyticsTracker.trackEvent(RECIPIENT_CREATION_STEP2_NEXT_CLICKED)
        router.navigateTo(Screens.addForwardingRuleFragment(id))
    }

    fun onBackClicked() {
        router.exit()
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): AddEmailDetailsViewModel
    }
}
