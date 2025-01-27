package org.open.smsforwarder.ui.steps.choosemethod

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
import org.open.smsforwarder.analytics.AnalyticsEvents.RECIPIENT_CREATION_STEP1_NEXT_CLICKED
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.domain.usecase.ValidateTitleUseCase
import org.open.smsforwarder.extension.asStateFlowWithInitialAction
import org.open.smsforwarder.extension.launchAndCancelPrevious
import org.open.smsforwarder.navigation.Screens
import org.open.smsforwarder.ui.mapper.toChooseForwardingMethodPresentation
import org.open.smsforwarder.ui.mapper.toDomain

@HiltViewModel(assistedFactory = ChooseForwardingMethodViewModel.Factory::class)
class ChooseForwardingMethodViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val forwardingRepository: ForwardingRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val router: Router,
    private val validateTitleUseCase: ValidateTitleUseCase
) : ViewModel(), DefaultLifecycleObserver {

    private val _viewState = MutableStateFlow(ChooseForwardingMethodState())
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
                .collect { recipient ->
                    _viewState.update { recipient.toChooseForwardingMethodPresentation() }
                }
        }
    }

    fun onTitleChanged(title: String) {
        val validationResult = validateTitleUseCase.execute(title)
        _viewState.update {
            it.copy(
                title = title,
                titleInputError = validationResult.errorMessage
            )
        }
    }

    fun onForwardingMethodChanged(forwardingType: ForwardingType?) {
        _viewState.update { it.copy(forwardingType = forwardingType) }
    }

    fun onNextClicked() {
        viewState.value.forwardingType?.let { forwardingType ->
            val screenToNavigate = when (forwardingType) {
                ForwardingType.SMS -> Screens.addPhoneDetailsFragment(id)
                ForwardingType.EMAIL -> Screens.addEmailDetailsFragment(id)
            }
            analyticsTracker.trackEvent(RECIPIENT_CREATION_STEP1_NEXT_CLICKED)
            router.navigateTo(screenToNavigate)
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): ChooseForwardingMethodViewModel
    }
}
