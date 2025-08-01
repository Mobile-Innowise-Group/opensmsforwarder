package org.open.smsforwarder.ui.history

import org.open.smsforwarder.ui.model.HistoryUI

data class HistoryState(
    val historyItems: List<HistoryUI> = emptyList(),
) {

    val isHistoryItemsVisible: Boolean
        get() = historyItems.isNotEmpty()

    val isEmptyStateTextVisible: Boolean
        get() = historyItems.isEmpty()
}
