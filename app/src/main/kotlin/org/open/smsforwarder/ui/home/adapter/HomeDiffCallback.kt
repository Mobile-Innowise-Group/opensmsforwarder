package org.open.smsforwarder.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import org.open.smsforwarder.ui.model.ForwardingUI

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
