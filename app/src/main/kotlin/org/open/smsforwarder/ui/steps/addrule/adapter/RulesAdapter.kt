package org.open.smsforwarder.ui.steps.addrule.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.open.smsforwarder.databinding.ItemRuleBinding
import org.open.smsforwarder.domain.model.Rule

class RulesAdapter(
    private val onItemEdit: (Rule) -> Unit,
    private val onItemRemove: (Rule) -> Unit
) : ListAdapter<Rule, RuleViewHolder>(RuleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
        val binding =
            ItemRuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RuleViewHolder(binding, onItemEdit, onItemRemove)
    }

    override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
