package com.github.opensmsforwarder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.opensmsforwarder.analytics.AnalyticsEvents.BATTERY_OPTIMIZATION_DIALOG_NAVIGATED
import com.github.opensmsforwarder.analytics.AnalyticsEvents.PERMISSIONS_DIALOG_NAVIGATED
import com.github.opensmsforwarder.analytics.AnalyticsEvents.PERMISSIONS_RATIONALE_DIALOG_NAVIGATED
import com.github.opensmsforwarder.analytics.AnalyticsEvents.RECIPIENT_CREATION_CLICKED
import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.data.RecipientsRepository
import com.github.opensmsforwarder.data.RecipientsRepository.Companion.NO_ID
import com.github.opensmsforwarder.data.RulesRepository
import com.github.opensmsforwarder.navigation.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recipientsRepository: RecipientsRepository,
    private val rulesRepository: RulesRepository,
    private val router: Router,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private val _viewState: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val viewState: StateFlow<HomeState> = _viewState.asStateFlow()

    private val _viewEffect: Channel<HomeEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<HomeEffect> = _viewEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                recipientsRepository.getRecipientsFlow(),
                rulesRepository.getRulesFlow()
            ) { recipients, rules ->
                recipients to rules
            }
                .collect { result ->
                    _viewState.update {
                        it.copy(
                            recipients = result.first,
                            rules = result.second
                        )
                    }
                }
        }
    }

    fun onAddNewRecipientClicked() {
        analyticsTracker.trackEvent(RECIPIENT_CREATION_CLICKED)
        recipientsRepository.setCurrentRecipientId(NO_ID)
        router.navigateTo(Screens.chooseForwardingMethodFragment())
    }

    fun onItemEditClicked(id: Long) {
        recipientsRepository.setCurrentRecipientId(id)
        router.navigateTo(Screens.chooseForwardingMethodFragment())
    }

    fun onItemRemoveClicked(id: Long) {
        _viewEffect.trySend(DeleteEffect(id))
    }

    fun onBatteryOptimizationWarningClicked() {
        analyticsTracker.trackEvent(BATTERY_OPTIMIZATION_DIALOG_NAVIGATED)
        _viewEffect.trySend(BatteryWarningEffect)
    }

    fun onRemoveConfirmed(id: Long) {
        viewModelScope.launch {
            recipientsRepository.deleteRecipient(id)
        }
    }

    fun onGoToSettingsRequired() {
        analyticsTracker.trackEvent(PERMISSIONS_DIALOG_NAVIGATED)
        _viewEffect.trySend(GoToSettingsEffect)
    }

    fun onPermissionsRationaleRequired() {
        analyticsTracker.trackEvent(PERMISSIONS_RATIONALE_DIALOG_NAVIGATED)
        _viewEffect.trySend(PermissionsRationalEffect)
    }
}
