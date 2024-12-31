package org.open.smsforwarder.ui.steps.addrule.adapter

import androidx.recyclerview.widget.RecyclerView
import org.open.smsforwarder.databinding.ItemRuleBinding
import org.open.smsforwarder.domain.model.Rule

class RuleViewHolder(
    private val binding: ItemRuleBinding,
    private val onItemEdit: (Rule) -> Unit,
    private val onItemRemove: (Rule) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(rule: Rule) = with(binding) {
        buttonEditItem.setOnClickListener { onItemEdit(rule) }
        buttonRemoveItem.setOnClickListener { onItemRemove(rule) }
        ruleTv.text = rule.textRule
    }
}
