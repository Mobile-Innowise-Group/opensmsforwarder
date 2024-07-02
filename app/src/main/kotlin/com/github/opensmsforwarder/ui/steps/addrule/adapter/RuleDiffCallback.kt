package com.github.opensmsforwarder.ui.steps.addrule.adapter

import androidx.recyclerview.widget.DiffUtil
import com.github.opensmsforwarder.model.Rule

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
