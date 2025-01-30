package org.open.smsforwarder.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.open.smsforwarder.databinding.ItemHistoryBinding
import org.open.smsforwarder.ui.model.HistoryUI

class SmsHistoryAdapter(
    private val onRetry: (Long) -> Unit
): ListAdapter<HistoryUI, SmsHistoryViewHolder>(SmsHistoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsHistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SmsHistoryViewHolder(binding, onRetry)
    }

    override fun onBindViewHolder(holder: SmsHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
