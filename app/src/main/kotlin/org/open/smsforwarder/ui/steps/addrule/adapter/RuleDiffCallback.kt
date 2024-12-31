package org.open.smsforwarder.ui.steps.addrule.adapter

import androidx.recyclerview.widget.DiffUtil
import org.open.smsforwarder.domain.model.Rule

class RuleDiffCallback : DiffUtil.ItemCallback<Rule>() {

    override fun areContentsTheSame(
        oldItem: Rule,
        newItem: Rule,
    ) = oldItem == newItem

    override fun areItemsTheSame(
        oldItem: Rule,
        newItem: Rule,
    ) = oldItem.id == newItem.id
}
