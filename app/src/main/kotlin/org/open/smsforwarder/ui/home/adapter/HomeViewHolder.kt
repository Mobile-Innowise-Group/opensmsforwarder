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

    fun bind(state: ForwardingUI) = with(binding) {
        val context = itemView.context
        title.isVisible = state.title.isNotBlank()
        title.text = state.title
        forwardingType.text = state.forwardingType?.value
        phoneGroup.isVisible = state.isSmsBlockCompleted()
        recipientPhoneEt.text = state.recipientPhone
        emailGroup.isVisible = state.isEmailBlockCompleted()
        email.text = state.recipientEmail
        error.isVisible = !state.allStepsCompleted || state.error.isNotEmpty()
        error.text = when {
            !state.allStepsCompleted -> context.getString(R.string.steps_are_not_completed_error)
            state.error.isNotEmpty() -> state.error
            else -> null
        }

        buttonEditItem.setOnClickListener { onItemEdit(state.id) }
        buttonRemoveItem.setOnClickListener { onItemRemove(state.id) }
    }
}
