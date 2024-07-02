package com.github.opensmsforwarder.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.github.opensmsforwarder.model.Recipient

class HomeDiffCallback : DiffUtil.ItemCallback<Recipient>() {

    override fun areContentsTheSame(
        oldItem: Recipient,
        newItem: Recipient,
    ) = oldItem == newItem

    override fun areItemsTheSame(
        oldItem: Recipient,
        newItem: Recipient,
    ) = oldItem.id == newItem.id
}
