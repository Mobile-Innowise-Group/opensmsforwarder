package com.github.opensmsforwarder.ui.steps.addrule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.analytics.AnalyticsEvents.RECIPIENT_CREATION_FINISHED
import com.github.opensmsforwarder.analytics.AnalyticsEvents.RULE_ADD_CLICKED
import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.data.repository.RulesRepository
import com.github.opensmsforwarder.domain.model.Rule
import com.github.opensmsforwarder.extension.asStateFlowWithInitialAction
import com.github.opensmsforwarder.extension.launchAndCancelPrevious
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

@HiltViewModel(assistedFactory = AddForwardingRuleViewModel.Factory::class)
class AddForwardingRuleViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val rulesRepository: RulesRepository,
    private val router: Router,
    private val analyticsTracker: AnalyticsTracker,
) : ViewModel() {

    private var _viewState = MutableStateFlow(AddForwardingRuleState())
    val viewState = _viewState.asStateFlowWithInitialAction(viewModelScope) { loadData() }

    private val _viewEffect: Channel<ForwardingRuleEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<ForwardingRuleEffect> = _viewEffect.receiveAsFlow()

    private fun loadData() {
        launchAndCancelPrevious {
            rulesRepository
                .getRulesByForwardingIdFlow(id)
                .collect { rules ->
                    _viewState.update {
                        it.copy(rules = rules)
                    }
                }
        }
    }

    fun onNewRuleEntered(text: String) {
        val isPatternExists = isPatternExists(text)
        _viewState.update {
            _viewState.value.copy(
                errorMessage = if (isPatternExists) R.string.add_rule_error else null,
                isAddRuleButtonEnabled = text.isNotBlank() && !isPatternExists
            )
        }
    }

    fun isPatternExists(text: String): Boolean = viewState.value.rules
        .map { it.textRule }
        .contains(text)

    fun onFinishClicked() {
        analyticsTracker.trackEvent(RECIPIENT_CREATION_FINISHED)
        router.backTo(null)
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onAddRuleClicked(rule: String) {
        analyticsTracker.trackEvent(RULE_ADD_CLICKED)
        viewModelScope.launch {
            rulesRepository.insertRule(
                Rule(
                    forwardingId = id,
                    textRule = rule
                )
            )
        }
    }

    fun onItemEditClicked(rule: Rule) {
        _viewEffect.trySend(ForwardingEditRuleEffect(rule))
    }

    fun onItemEdited(itemId: Long, newValue: String) {
        viewState.value.rules
            .find { it.id == itemId }
            ?.copy(textRule = newValue)
            ?.let { rule ->
                viewModelScope.launch {
                    rulesRepository.insertRule(rule)
                }
            }
    }

    fun onItemRemoved(ruleId: Long) {
        viewModelScope.launch {
            rulesRepository.deleteRule(ruleId)
        }
    }

    fun onItemRemoveClicked(rule: Rule) {
        _viewEffect.trySend(ForwardingDeleteRuleEffect(rule))
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): AddForwardingRuleViewModel
    }
}
