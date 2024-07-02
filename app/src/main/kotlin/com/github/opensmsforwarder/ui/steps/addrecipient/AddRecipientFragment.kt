package com.github.opensmsforwarder.ui.steps.addrecipient

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.FragmentAddRecipientBinding
import com.github.opensmsforwarder.extension.bindClicksTo
import com.github.opensmsforwarder.extension.bindTextChangesTo
import com.github.opensmsforwarder.extension.observeWithLifecycle
import com.github.opensmsforwarder.extension.setTextIfChanged
import com.github.opensmsforwarder.extension.setTextIfChangedKeepState
import com.github.opensmsforwarder.extension.setVisibilityIfChanged
import com.github.opensmsforwarder.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRecipientFragment : Fragment(R.layout.fragment_add_recipient) {

    private val binding by viewBinding(FragmentAddRecipientBinding::bind)
    private val viewModel: AddRecipientViewModel by viewModels()

    private val googleSigInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.onSignInResultReceived(result)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        with(binding) {
            recipientPhone bindTextChangesTo viewModel::onPhoneChanged
            recipientEmail bindTextChangesTo viewModel::onEmailChanged
            signInButton bindClicksTo viewModel::onSignInClicked
            signOutButton bindClicksTo viewModel::onSignOutClicked
            nextBtn bindClicksTo viewModel::onNextClicked
            imageBack bindClicksTo viewModel::onBackClicked
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner) { effect ->
            handleEffect(effect)
        }
    }

    private fun renderState(state: AddRecipientViewState) {
        with(binding) {
            val recipient = state.recipient
            fromLabel.setVisibilityIfChanged(recipient.isEmailForwardingType && !recipient.hasSenderEmail)

            senderEmail.setTextIfChanged(getString(R.string.signed_in_as, recipient.senderEmail))
            senderEmail.setVisibilityIfChanged(recipient.hasSenderEmail && recipient.isEmailForwardingType)

            signInButton.setVisibilityIfChanged(!recipient.hasSenderEmail && recipient.isEmailForwardingType)
            signOutButton.setVisibilityIfChanged(recipient.hasSenderEmail && recipient.isEmailForwardingType)

            textEnterEmail.setVisibilityIfChanged(recipient.isEmailForwardingType)
            textEnterPhone.setVisibilityIfChanged(recipient.isSmsForwardingType)

            recipientPhoneLayout.setVisibilityIfChanged(recipient.isSmsForwardingType)
            if (recipientPhoneLayout.isVisible) {
                recipientPhone.setTextIfChangedKeepState(recipient.recipientPhone)
            }

            recipientEmailLayout.setVisibilityIfChanged(recipient.isEmailForwardingType)
            if (recipientEmailLayout.isVisible) {
                recipientEmail.setTextIfChangedKeepState(recipient.recipientEmail)
            }
            nextBtn.isEnabled = if (state.recipient.isSmsForwardingType) {
                state.phoneInputError == null && state.recipient.recipientPhone.isNotBlank()
            } else {
                state.emailInputError == null
                        && state.recipient.hasSenderEmail
                        && state.recipient.recipientEmail.isNotBlank()
            }
            recipientEmailLayout.error = state.emailInputError?.let {
                getString(it)
            }
            recipientPhoneLayout.error = state.phoneInputError?.let {
                getString(it)
            }
        }
    }

    private fun handleEffect(effect: AddRecipientViewEffect) {
        when (effect) {
            is GoogleSignInViewEffect -> effect.signInIntent?.let(googleSigInLauncher::launch)
            is GoogleSignInErrorViewEffect -> effect.errorMessage?.let(::showToast)
        }
    }
}
