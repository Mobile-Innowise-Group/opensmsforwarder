package org.open.smsforwarder.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.open.smsforwarder.databinding.ItemHistoryBinding
import org.open.smsforwarder.ui.model.HistoryUI

class HistoryAdapter : ListAdapter<HistoryUI, HistoryViewHolder>(SmsHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
