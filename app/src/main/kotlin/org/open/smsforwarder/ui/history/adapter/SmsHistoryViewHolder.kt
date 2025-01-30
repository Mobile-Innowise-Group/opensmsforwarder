package org.open.smsforwarder.ui.history.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.ItemHistoryBinding
import org.open.smsforwarder.ui.model.HistoryUI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsHistoryViewHolder(
    private val binding: ItemHistoryBinding,
    private val onRetry: (Long) -> Unit
):  ViewHolder(binding.root) {

    fun bind(model: HistoryUI) = with(binding) {
        val context = itemView.context
        date.text = convertLongToTime(model.date)
        message.text = model.message

        retry.setOnClickListener {
            onRetry(model.id)
        }

        if (model.isForwardingSuccessful == true) {
            status.text = context.getString(R.string.history_item_status_success)
            status.setTextColor(context.getColor(R.color.green))
            retry.visibility = View.GONE
        } else {
            status.text = context.getString(R.string.history_item_status_fail)
            status.setTextColor(context.getColor(R.color.red))
            retry.visibility = View.VISIBLE
        }
    }

    private fun convertLongToTime(time: Long?): String? =
        runCatching {
            time?.let { timestamp ->
                DATE_FORMAT.get()?.format(Date(timestamp))
            }
        }.getOrDefault(null)

    private companion object {
        private val DATE_FORMAT = ThreadLocal.withInitial {
            SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())
        }
    }
}
