package com.github.opensmsforwarder.ui.steps.addrecipientdetails.addphonedetails

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.FragmentAddPhoneDetailsBinding
import com.github.opensmsforwarder.extension.assistedViewModels
import com.github.opensmsforwarder.extension.bindClicksTo
import com.github.opensmsforwarder.extension.bindTextChangesTo
import com.github.opensmsforwarder.extension.observeWithLifecycle
import com.github.opensmsforwarder.extension.setTextIfChangedKeepState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPhoneDetailsFragment : Fragment(R.layout.fragment_add_phone_details) {

    private val binding by viewBinding(FragmentAddPhoneDetailsBinding::bind)
    private val viewModel by assistedViewModels<AddPhoneDetailsViewModel, AddPhoneDetailsViewModel.Factory> { factory ->
        factory.create(requireArguments().getLong(ID_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        with(binding) {
            arrowBackIv bindClicksTo viewModel::onBackClicked
            recipientPhoneEt bindTextChangesTo viewModel::onPhoneChanged
            nextBtn bindClicksTo viewModel::onNextClicked
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    private fun renderState(state: AddPhoneDetailsState) {
        with(binding) {
            recipientPhoneEt.setTextIfChangedKeepState(state.recipientPhone)
            recipientPhoneLayout.error = state.inputError?.asString(requireContext())
            nextBtn.isEnabled = state.nextButtonEnabled
        }
    }

    companion object {
        fun newInstance(id: Long): AddPhoneDetailsFragment =
            AddPhoneDetailsFragment().apply {
                arguments = bundleOf(ID_KEY to id)
            }

        private const val ID_KEY = "ID_KEY"
    }
}
