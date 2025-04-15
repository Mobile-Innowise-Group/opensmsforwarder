package org.open.smsforwarder.ui.steps.addrule.adapter

import androidx.recyclerview.widget.RecyclerView
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.ItemRuleBinding
import org.open.smsforwarder.domain.model.Rule

class RuleViewHolder(
    private val binding: ItemRuleBinding,
    private val onItemEdit: (Rule) -> Unit,
    private val onItemRemove: (Rule) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Rule) = with(binding) {
        val context = itemView.context
        buttonEditItem.setOnClickListener { onItemEdit(item) }
        buttonEditItem.contentDescription =
            context.getString(R.string.edit_rule_named, item.textRule)
        buttonRemoveItem.setOnClickListener { onItemRemove(item) }
        buttonRemoveItem.contentDescription =
            context.getString(R.string.delete_rule_named, item.textRule)
        ruleTv.text = item.textRule
        ruleTv.contentDescription = context.getString(R.string.rule_named, item.textRule)
    }
}
