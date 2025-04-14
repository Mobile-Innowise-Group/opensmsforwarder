package org.open.smsforwarder.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.open.smsforwarder.data.repository.HistoryRepository
import org.open.smsforwarder.extension.asStateFlowWithInitialAction
import org.open.smsforwarder.extension.launchAndCancelPrevious
import org.open.smsforwarder.ui.mapper.toHistoryUi
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val router: Router,
) : ViewModel() {

    private var _viewState = MutableStateFlow(HistoryState())
    val viewState = _viewState.asStateFlowWithInitialAction(viewModelScope) { loadData() }


    fun onBackClicked() {
        router.exit()
    }

    private fun loadData() {
        launchAndCancelPrevious {
            historyRepository
                .getForwardingHistoryFlow()
                .collect { result ->
                    _viewState.update {
                        it.copy(historyItems = result.map { resultItem -> resultItem.toHistoryUi() })
                    }
                }
        }
    }
}
