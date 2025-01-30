package org.open.smsforwarder.ui.history

import org.open.smsforwarder.ui.model.HistoryUI

data class SmsHistoryState(
    val historyItems: List<HistoryUI> = emptyList()
)
