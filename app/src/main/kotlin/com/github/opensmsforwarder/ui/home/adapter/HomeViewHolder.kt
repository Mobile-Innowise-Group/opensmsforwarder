package com.github.opensmsforwarder.ui.home.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.ItemRecipientBinding
import com.github.opensmsforwarder.model.ForwardingType
import com.github.opensmsforwarder.model.Recipient

class HomeViewHolder(
    private val binding: ItemRecipientBinding,
    private val onItemEdit: (Long) -> Unit,
    private val onItemRemove: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(recipient: Recipient) = with(binding) {
        val context = itemView.context
        buttonEditItem.setOnClickListener { onItemEdit(recipient.id) }
        title.text = recipient.title
        forwardingType.text = recipient.forwardingType?.value
        phoneGroup.isVisible =
            recipient.forwardingType == ForwardingType.SMS && recipient.recipientPhone.isNotEmpty()
        recipientPhone.text = recipient.recipientPhone
        emailGroup.isVisible =
            recipient.forwardingType == ForwardingType.EMAIL && recipient.recipientEmail.isNotEmpty()
        email.text = recipient.recipientEmail
        stepsError.isVisible = !recipient.allStepsCompleted || !recipient.isForwardSuccessful
        stepsError.text = when {
            !recipient.allStepsCompleted -> context.getString(R.string.steps_are_not_completed_error)
            !recipient.isForwardSuccessful && recipient.isEmailForwardingType ->
                context.getString(R.string.sms_forward_email_error)

            !recipient.isForwardSuccessful && recipient.isSmsForwardingType ->
                context.getString(R.string.sms_forward_phone_error)

            else -> null
        }

        buttonRemoveItem.setOnClickListener { onItemRemove(recipient.id) }
    }
}
