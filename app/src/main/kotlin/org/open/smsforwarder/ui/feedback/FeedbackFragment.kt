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
            cancelBtn bindClicksTo viewModel::onBackClicked
            emailEt bindTextChangesTo viewModel::onEmailChanged
            bodyEt bindTextChangesTo viewModel::onBodyChanged
            submitBtn.setOnClickListener {
                viewModel.onSubmitClicked(
                    emailEt.text.toString(),
                    bodyEt.text.toString()
                )
            }
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner, action = ::handleEffect)
    }

    private fun renderState(state: FeedbackState) {
        with(binding) {
            submitBtn.isEnabled = state.submitButtonEnabled
            emailEtLayout.error = state.emailInputError?.asString(requireContext())
            bodyEtLayout.error = state.bodyInputError?.asString(requireContext())
        }
    }

    private fun handleEffect(effect: FeedbackEffect) {
        when (effect) {
            is SubmitResultEffect ->
                showToast(effect.messageRes)
        }
    }
}
