package org.open.smsforwarder.ui.feedback

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentFeedbackBinding
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.bindTextChangesTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.showToast

@AndroidEntryPoint
class FeedbackFragment : Fragment(R.layout.fragment_feedback) {

    private val binding by viewBinding(FragmentFeedbackBinding::bind)
    private val viewModel: FeedbackViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        with(binding) {
            cancelBtn bindClicksTo viewModel::onBackClick
            emailInputField bindTextChangesTo viewModel::onEmailChanged
            bodyInputField bindTextChangesTo viewModel::onBodyChanged
            submitBtn.setOnClickListener {
                viewModel.onSubmitClick(
                    emailInputField.text.toString(),
                    bodyInputField.text.toString()
                ) { success ->
                    displayResult(success)
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
    }

    private fun renderState(state: FeedbackState) {
        with(binding) {
            submitBtn.isEnabled = state.submitButtonEnabled
            layoutEmailInputField.error = state.emailInputError?.asString(requireContext())
            layoutBodyInputField.error = state.bodyInputError?.asString(requireContext())
        }
    }

    private fun displayResult(success: Boolean) {
        if (success) {
            showToast(R.string.feedback_success)
        } else {
            showToast(R.string.feedback_failure)
        }
    }
}
