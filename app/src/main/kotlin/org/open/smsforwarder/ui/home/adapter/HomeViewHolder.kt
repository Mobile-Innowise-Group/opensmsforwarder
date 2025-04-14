package org.open.smsforwarder.ui.home.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.ItemRecipientBinding
import org.open.smsforwarder.ui.model.ForwardingUI

class HomeViewHolder(
    private val binding: ItemRecipientBinding,
    private val onItemEdit: (Long) -> Unit,
    private val onItemRemove: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ForwardingUI) = with(binding) {
        val context = itemView.context
        title.isVisible = item.title.isNotBlank()
        title.text = item.title
        forwardingType.text = item.forwardingType?.value
        phoneGroup.isVisible = item.isSmsBlockCompleted()
        recipientPhoneEt.text = item.recipientPhone
        emailGroup.isVisible = item.isEmailBlockCompleted()
        email.text = item.recipientEmail
        error.isVisible = !item.allStepsCompleted || item.error.isNotEmpty()
        error.text = when {
            !item.allStepsCompleted -> context.getString(R.string.steps_are_not_completed_error)
            item.error.isNotEmpty() -> item.error
            else -> null
        }

        buttonEditItem.setOnClickListener { onItemEdit(item.id) }
        buttonRemoveItem.setOnClickListener { onItemRemove(item.id) }
    }
}
