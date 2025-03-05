package org.open.smsforwarder.ui.steps.addrecipientdetails.addphonedetails

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.open.smsforwarder.analytics.AnalyticsEvents.RECIPIENT_CREATION_STEP2_NEXT_CLICKED
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.domain.usecase.ValidatePhoneUseCase
import org.open.smsforwarder.extension.asStateFlowWithInitialAction
import org.open.smsforwarder.extension.getErrorStringProvider
import org.open.smsforwarder.extension.launchAndCancelPrevious
import org.open.smsforwarder.navigation.Screens
import org.open.smsforwarder.ui.mapper.toDomain
import org.open.smsforwarder.ui.mapper.toPhoneDetailsPresentation

@HiltViewModel(assistedFactory = AddPhoneDetailsViewModel.Factory::class)
class AddPhoneDetailsViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val forwardingRepository: ForwardingRepository,
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val router: Router,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel(), DefaultLifecycleObserver {

    private var _viewState = MutableStateFlow(AddPhoneDetailsState())
    val viewState = _viewState.asStateFlowWithInitialAction(viewModelScope) { loadData() }

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
                        forwarding.toPhoneDetailsPresentation()
                    }
                }
        }
    }

    fun onPhoneChanged(phoneNumber: String) {
        val phoneValidationResult = validatePhoneUseCase.execute(phoneNumber)
        _viewState.update {
            it.copy(
                recipientPhone = phoneNumber,
                inputErrorProvider = phoneValidationResult.errorType?.getErrorStringProvider()
            )
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
        fun create(id: Long): AddPhoneDetailsViewModel
    }
}
