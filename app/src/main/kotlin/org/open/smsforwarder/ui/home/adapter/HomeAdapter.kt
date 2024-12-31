package org.open.smsforwarder.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.open.smsforwarder.databinding.ItemRecipientBinding
import org.open.smsforwarder.ui.model.ForwardingUI

class HomeAdapter(
    private val onItemEdit: (Long) -> Unit,
    private val onItemRemove: (Long) -> Unit,
) : ListAdapter<ForwardingUI, HomeViewHolder>(HomeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding =
            ItemRecipientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding, onItemEdit, onItemRemove)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
