package org.open.smsforwarder.ui.steps.addrecipientdetails.addtelegramdetails

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
import org.open.smsforwarder.data.repository.ForwardingRepository
import org.open.smsforwarder.domain.usecase.ValidateBlankFieldUseCase
import org.open.smsforwarder.extension.asStateFlowWithInitialAction
import org.open.smsforwarder.extension.getStringProvider
import org.open.smsforwarder.extension.launchAndCancelPrevious
import org.open.smsforwarder.navigation.Screens
import org.open.smsforwarder.ui.mapper.toDomain
import org.open.smsforwarder.ui.mapper.toTelegramDetailsUi

@HiltViewModel(assistedFactory = AddTelegramDetailsViewModel.Factory::class)
class AddTelegramDetailsViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val forwardingRepository: ForwardingRepository,
    private val validateBlankFieldUseCase: ValidateBlankFieldUseCase,
    private val router: Router,
) : ViewModel(), DefaultLifecycleObserver {

    private var _viewState = MutableStateFlow(AddTelegramDetailsState())
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
                        forwarding.toTelegramDetailsUi()
                    }
                }
        }
    }

    fun onTelegramApiTokenChanged(apiToken: String) {
        val apiTokenValidationResult = validateBlankFieldUseCase.execute(apiToken)
        _viewState.update {
            it.copy(
                telegramApiToken = apiToken,
                inputErrorApiToken = apiTokenValidationResult.errorType?.getStringProvider()
            )
        }
    }

    fun onTelegramChatIdChanged(chatId: String) {
        val chatIdValidationResult = validateBlankFieldUseCase.execute(chatId)
        _viewState.update {
            it.copy(
                telegramChatId = chatId,
                inputErrorChatId = chatIdValidationResult.errorType?.getStringProvider()
            )
        }
    }

    fun onNextClicked() {
        router.navigateTo(Screens.addForwardingRuleFragment(id))
    }

    fun onBackClicked() {
        router.exit()
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): AddTelegramDetailsViewModel
    }
}
