package org.open.smsforwarder.ui.history

import org.open.smsforwarder.ui.model.HistoryUI

data class ForwardingHistoryState(
    val historyItems: List<HistoryUI> = emptyList(),
    val isHistoryItemsVisible: Boolean = historyItems.isNotEmpty(),
    val isEmptyStateTextVisible: Boolean = historyItems.isEmpty(),
)
