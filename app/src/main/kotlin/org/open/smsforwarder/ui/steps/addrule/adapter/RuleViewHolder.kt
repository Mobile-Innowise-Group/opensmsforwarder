package org.open.smsforwarder.ui.steps.addrule.adapter

import androidx.recyclerview.widget.RecyclerView
import org.open.smsforwarder.databinding.ItemRuleBinding
import org.open.smsforwarder.domain.model.Rule

class RuleViewHolder(
    private val binding: ItemRuleBinding,
    private val onItemEdit: (Rule) -> Unit,
    private val onItemRemove: (Rule) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Rule) = with(binding) {
        buttonEditItem.setOnClickListener { onItemEdit(item) }
        buttonRemoveItem.setOnClickListener { onItemRemove(item) }
        ruleTv.text = item.textRule
    }
}
