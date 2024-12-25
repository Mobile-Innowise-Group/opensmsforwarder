package com.github.opensmsforwarder.ui.steps.addrecipientdetails.addemaildetails

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.FragmentAddEmailDetailsBinding
import com.github.opensmsforwarder.extension.assistedViewModels
import com.github.opensmsforwarder.extension.bindClicksTo
import com.github.opensmsforwarder.extension.bindTextChangesTo
import com.github.opensmsforwarder.extension.observeWithLifecycle
import com.github.opensmsforwarder.extension.setTextIfChanged
import com.github.opensmsforwarder.extension.setTextIfChangedKeepState
import com.github.opensmsforwarder.extension.setVisibilityIfChanged
import com.github.opensmsforwarder.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEmailDetailsFragment : Fragment(R.layout.fragment_add_email_details) {

    private val binding by viewBinding(FragmentAddEmailDetailsBinding::bind)
    private val viewModel by assistedViewModels<AddEmailDetailsViewModel, AddEmailDetailsViewModel.Factory> { factory ->
        factory.create(requireArguments().getLong(ID_KEY))
    }

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
            arrowBackIv bindClicksTo viewModel::onBackClicked
            recipientEmailEt bindTextChangesTo viewModel::onEmailChanged
            signInBtn bindClicksTo viewModel::onSignInClicked
            signOutBtn bindClicksTo viewModel::onSignOutClicked
            nextBtn bindClicksTo viewModel::onNextClicked
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner, action = ::handleEffect)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    private fun renderState(state: AddEmailDetailsState) {
        with(binding) {
            signInTv.setVisibilityIfChanged(state.signInTvVisible)
            senderEmailTv.setVisibilityIfChanged(state.senderEmailVisible)
            senderEmailTv.setTextIfChanged(getString(R.string.signed_in_as, state.senderEmail))
            signInBtn.setVisibilityIfChanged(state.sigInBtnVisible)
            signOutBtn.setVisibilityIfChanged(state.signOutBtnVisible)
            recipientEmailEt.setTextIfChangedKeepState(state.recipientEmail)
            nextBtn.isEnabled = state.nextButtonEnabled
            recipientEmailLayout.error = state.inputError?.asString(requireContext())
        }
    }

    private fun handleEffect(effect: AddEmailDetailsViewEffect) {
        when (effect) {
            is GoogleSignInErrorViewEffect -> effect.errorMessage?.let(::showToast)
            is GoogleSignInViewEffect -> effect.signInIntent?.let(googleSigInLauncher::launch)
        }
    }

    companion object {
        fun newInstance(id: Long): AddEmailDetailsFragment =
            AddEmailDetailsFragment().apply {
                arguments = bundleOf(ID_KEY to id)
            }

        private const val ID_KEY = "ID_KEY"
    }
}
