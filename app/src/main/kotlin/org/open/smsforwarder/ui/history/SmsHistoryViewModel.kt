package org.open.smsforwarder.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.open.smsforwarder.data.repository.HistoryRepository
import org.open.smsforwarder.extension.asStateFlowWithInitialAction
import org.open.smsforwarder.extension.launchAndCancelPrevious
import org.open.smsforwarder.processing.processor.ForwardingProcessor
import org.open.smsforwarder.ui.mapper.toHistoryUi
import javax.inject.Inject

@HiltViewModel
class SmsHistoryViewModel @Inject constructor(
    private val router: Router,
    private val historyRepository: HistoryRepository,
    private val forwardingProcessor: ForwardingProcessor
) : ViewModel() {

    private var _viewState = MutableStateFlow(SmsHistoryState())
    val viewState = _viewState.asStateFlowWithInitialAction(viewModelScope) { loadData() }

    private val _viewEffect: Channel<SmsHistoryEffect> = Channel(Channel.BUFFERED)
    val viewEffect: Flow<SmsHistoryEffect> = _viewEffect.receiveAsFlow()

    fun onBackClicked() {
        router.exit()
    }

    fun onRetryClicked(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                historyRepository.getForwardedMessageById(id)?.let { historyItem ->
                    forwardingProcessor.process(arrayOf(historyItem.message), id)
                }
            }
        }
        _viewEffect.trySend(SmsHistoryEffect.RetryEffect(id))
    }

    private fun loadData() {
        launchAndCancelPrevious {
            historyRepository
                .getForwardedMessagesFlow()
                .collect { result ->
                    _viewState.update {
                        it.copy(historyItems = result.map { resultItem -> resultItem.toHistoryUi() })
                    }
                }
        }
    }
}
