package org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails

import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
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
import org.open.smsforwarder.extension.getErrorStringProvider
import org.open.smsforwarder.extension.launchAndCancelPrevious
import org.open.smsforwarder.navigation.Screens
import org.open.smsforwarder.ui.mapper.toDomain
import org.open.smsforwarder.ui.mapper.toEmailDetailsPresentation

@HiltViewModel(assistedFactory = AddEmailDetailsViewModel.Factory::class)
class AddEmailDetailsViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val forwardingRepository: ForwardingRepository,
    private val authRepository: AuthRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val router: Router,
) : ViewModel(), DefaultLifecycleObserver {

    /**
     * In-memory cache for logged user's email
     */
    private var email: String? = null

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
                        forwarding.toEmailDetailsPresentation()
                    }
                }
        }
    }

    fun onSignInClicked() {
        _viewEffect.trySend(GoogleSignInViewEffect)
    }

    fun handleSignIn(block: suspend () -> Result<GetCredentialResponse>) {
        viewModelScope.launch {
            block()
                .onSuccess { credentialsResponse ->
                    handleCredentialResponse(credentialsResponse)
                }
                .onFailure { error ->
                    _viewEffect.trySend(
                        GoogleSignInErrorViewEffect(R.string.error_get_credentials_template, error)
                    )
                }
        }
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
                inputErrorProvider = emailValidationResult.errorType?.getErrorStringProvider()
            )
        }
    }


    fun exchangeTokens(serverAuth: String) {
        viewModelScope.launch {
            authRepository
                .exchangeAuthCodeForTokensAnd(serverAuth)
                .onSuccess { response ->
                    val recipient = viewState.value.copy(
                        senderEmail = email
                    )
                    forwardingRepository.insertOrUpdateForwarding(recipient.toDomain())
                    authRepository.saveTokens(
                        viewState.value.id,
                        response.accessToken,
                        response.refreshToken
                    )
                }
                .onFailure { error ->
                    _viewEffect.trySend(
                        GoogleSignInErrorViewEffect(R.string.error_authorization_template, error)
                    )
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

    private fun handleCredentialResponse(credentialResponse: GetCredentialResponse) {
        when (val credential = credentialResponse.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        email = googleIdTokenCredential.id
                    } catch (e: GoogleIdTokenParsingException) {
                        _viewEffect.trySend(
                            GoogleSignInErrorViewEffect(R.string.error_get_credentials_template, e)
                        )
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): AddEmailDetailsViewModel
    }
}
