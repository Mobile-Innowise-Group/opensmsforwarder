package com.github.opensmsforwarder.ui.steps.choosemethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.opensmsforwarder.data.RecipientsRepository
import com.github.opensmsforwarder.data.RecipientsRepository.Companion.NO_ID
import com.github.opensmsforwarder.model.ForwardingType
import com.github.opensmsforwarder.model.Recipient
import com.github.opensmsforwarder.navigation.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseForwardingMethodViewModel @Inject constructor(
    private val recipientsRepository: RecipientsRepository,
    private val router: Router,
) : ViewModel() {

    private val _viewState: MutableStateFlow<Recipient> = MutableStateFlow(Recipient())
    val viewState: StateFlow<Recipient> = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            if (isNewRecipientCreating()) {
                val id = recipientsRepository.insertOrUpdateRecipient(viewState.value)
                recipientsRepository.setCurrentRecipientId(id)
            }
            recipientsRepository
                .getCurrentRecipientFlow()
                .collect { recipient ->
                    _viewState.update { recipient }
                }
        }
    }

    fun onTitleChanged(title: String) {
        _viewState.update {
            it.copy(title = title)
        }
    }

    fun onForwardingMethodChanged(forwardingType: ForwardingType?) {
        _viewState.update {
            it.copy(forwardingType = forwardingType)
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onNextClicked() {
        with(viewState.value) {
            val recipient = copy(
                recipientPhone = getPhoneToSave(),
                recipientEmail = getEmailToSave()
            )

            viewModelScope.launch {
                recipientsRepository.insertOrUpdateRecipient(recipient)
                router.navigateTo(Screens.addRecipientFragment())
            }
        }
    }

    private fun isNewRecipientCreating(): Boolean =
        recipientsRepository.getCurrentRecipientId() == NO_ID
}
