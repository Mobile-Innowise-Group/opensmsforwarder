package com.github.opensmsforwarder.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.github.opensmsforwarder.ui.model.ForwardingUI

class HomeDiffCallback : DiffUtil.ItemCallback<ForwardingUI>() {

    override fun areContentsTheSame(
        oldItem: ForwardingUI,
        newItem: ForwardingUI,
    ) = oldItem == newItem

    override fun areItemsTheSame(
        oldItem: ForwardingUI,
        newItem: ForwardingUI,
    ) = oldItem.id == newItem.id
}
