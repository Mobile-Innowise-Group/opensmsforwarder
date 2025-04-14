package org.open.smsforwarder.ui.history.adapter

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.ItemHistoryBinding
import org.open.smsforwarder.extension.toDateTime
import org.open.smsforwarder.ui.model.HistoryUI

class HistoryViewHolder(
    private val binding: ItemHistoryBinding,
) : ViewHolder(binding.root) {

    fun bind(item: HistoryUI) = with(binding) {
        val context = itemView.context
        dateLabel.contentDescription = context.getString(
            R.string.forwarding_history_item_content_description,
            item.id.toString()
        ) + context.getString(R.string.forwarding_history_item_date)
        date.text = item.date.toDateTime()
        message.text = item.message

        if (item.isForwardingSuccessful) {
            status.text = context.getString(R.string.forwarding_history_item_status_success)
            status.setTextColor(context.getColor(R.color.green))
        } else {
            status.text = context.getString(R.string.forwarding_history_item_status_fail)
            status.setTextColor(context.getColor(R.color.red))
        }
    }
}
