package org.open.smsforwarder.ui.history.adapter

import androidx.recyclerview.widget.DiffUtil
import org.open.smsforwarder.ui.model.HistoryUI

class SmsHistoryDiffCallback : DiffUtil.ItemCallback<HistoryUI>() {
    override fun areItemsTheSame(oldItem: HistoryUI, newItem: HistoryUI) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: HistoryUI, newItem: HistoryUI) =
        oldItem.isForwardingSuccessful == newItem.isForwardingSuccessful &&
                oldItem.date == newItem.date &&
                oldItem.message == newItem.message
}
