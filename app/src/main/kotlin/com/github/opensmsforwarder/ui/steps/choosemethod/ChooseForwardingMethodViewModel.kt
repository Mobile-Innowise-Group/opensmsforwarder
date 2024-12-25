package com.github.opensmsforwarder.ui.steps.choosemethod

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.opensmsforwarder.analytics.AnalyticsEvents.RECIPIENT_CREATION_STEP1_NEXT_CLICKED
import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.data.repository.ForwardingRepository
import com.github.opensmsforwarder.domain.model.Forwarding
import com.github.opensmsforwarder.domain.model.ForwardingType
import com.github.opensmsforwarder.extension.asStateFlowWithInitialAction
import com.github.opensmsforwarder.extension.launchAndCancelPrevious
import com.github.opensmsforwarder.navigation.Screens
import com.github.terrakok.cicerone.Router
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChooseForwardingMethodViewModel.Factory::class)
class ChooseForwardingMethodViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val forwardingRepository: ForwardingRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val router: Router,
) : ViewModel(), DefaultLifecycleObserver {

    private val _viewState = MutableStateFlow(Forwarding())
    val viewState = _viewState.asStateFlowWithInitialAction(viewModelScope) { loadData() }

    override fun onPause(owner: LifecycleOwner) {
        viewModelScope.launch {
            forwardingRepository.insertOrUpdateForwarding(viewState.value)
        }
        super.onPause(owner)
    }

    private fun loadData() {
        launchAndCancelPrevious {
            forwardingRepository
                .getForwardingByIdFlow(id)
                .collect { recipient ->
                    _viewState.update { recipient }
                }
        }
    }

    fun onTitleChanged(title: String) {
        _viewState.update { it.copy(title = title) }
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
