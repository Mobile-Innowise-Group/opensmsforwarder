package org.open.smsforwarder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.open.smsforwarder.analytics.AnalyticsEvents.BATTERY_OPTIMIZATION_DIALOG_NAVIGATED
import org.open.smsforwarder.analytics.AnalyticsEvents.FORWARDING_CREATION_CLICKED
import org.open.smsforwarder.analytics.AnalyticsEvents.PERMISSIONS_DIALOG_NAVIGATED
import org.open.smsforwarder.analytics.AnalyticsEvents.PERMISSIONS_RATIONALE_DIALOG_NAVIGATED
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.data.repository.RulesRepository
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.extension.asStateFlowWithInitialAction
import org.open.smsforwarder.extension.launchAndCancelPrevious
import org.open.smsforwarder.navigation.Screens
import org.open.smsforwarder.ui.mapper.mergeWithRules
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val forwardingRepository: ForwardingRepository,
    private val rulesRepository: RulesRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val router: Router,
) : ViewModel() {

    private val _viewState = MutableStateFlow(HomeState())
    val viewState = _viewState.asStateFlowWithInitialAction(viewModelScope) { loadData() }

    private val _viewEffect: Channel<HomeEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<HomeEffect> = _viewEffect.receiveAsFlow()

    private fun loadData() {
        launchAndCancelPrevious {
            combine(
                forwardingRepository.getForwardingFlow(),
                rulesRepository.getRulesFlow(),
                List<Forwarding>::mergeWithRules
            )
                .collect { result ->
                    _viewState.update { it.copy(forwardings = result.forwardings) }
                }
        }
    }

    fun onNewForwardingClicked() {
        viewModelScope.launch {
            analyticsTracker.trackEvent(FORWARDING_CREATION_CLICKED)
            val id = forwardingRepository.createNewForwarding()
            router.navigateTo(Screens.chooseForwardingMethodFragment(id))
        }
    }

    fun onItemEditClicked(id: Long) {
        router.navigateTo(Screens.chooseForwardingMethodFragment(id))
    }

    fun onItemRemoveClicked(id: Long) {
        _viewEffect.trySend(DeleteEffect(id))
    }

    fun onRemoveConfirmed(id: Long) {
        viewModelScope.launch {
            forwardingRepository.deleteForwarding(id)
        }
    }

    fun onBatteryOptimizationWarningClicked() {
        analyticsTracker.trackEvent(BATTERY_OPTIMIZATION_DIALOG_NAVIGATED)
        _viewEffect.trySend(BatteryWarningEffect)
    }

    fun onGoToSettingsRequired() {
        analyticsTracker.trackEvent(PERMISSIONS_DIALOG_NAVIGATED)
        _viewEffect.trySend(GoToSettingsEffect)
    }

    fun onPermissionsRationaleRequired() {
        analyticsTracker.trackEvent(PERMISSIONS_RATIONALE_DIALOG_NAVIGATED)
        _viewEffect.trySend(PermissionsRationalEffect)
    }

    fun onPermissionsGranted() {
        _viewState.update { it.copy(needToShowPermissionPermanentInfo = false) }
    }

    fun onPermissionsPermanentlyDenied() {
        _viewState.update { it.copy(needToShowPermissionPermanentInfo = true) }
    }

    fun onFeedbackClicked() {
        router.navigateTo(Screens.feedbackFragment())
    }
}
