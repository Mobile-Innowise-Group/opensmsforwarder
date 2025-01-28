package org.open.smsforwarder.ui.feedback

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentFeedbackBinding
import org.open.smsforwarder.extension.bindClicksTo

@AndroidEntryPoint
class FeedbackFragment : Fragment(R.layout.fragment_feedback) {

    private val binding by viewBinding(FragmentFeedbackBinding::bind)
    private val viewModel: FeedbackViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {
            cancelBtn bindClicksTo viewModel::exit
            submitBtn.setOnClickListener {
                sendFeedbackLetter(emailInputField.text.toString(), bodyInputField.text.toString())
            }
        }
    }

    private fun sendFeedbackLetter(email: String, body: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(EMAIL_URI)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
            putExtra(
                Intent.EXTRA_SUBJECT, getString(
                    R.string.feedback_letter_subject,
                    email
                )
            )
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(emailIntent)
    }

    companion object {
        const val SUPPORT_EMAIL = "support.smsforwarder@innowise.com"
        const val EMAIL_URI = "mailto:"
    }
}
