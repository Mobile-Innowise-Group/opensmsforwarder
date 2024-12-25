package com.github.opensmsforwarder.ui.steps.choosemethod

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.FragmentChooseForwardingMethodBinding
import com.github.opensmsforwarder.domain.model.Forwarding
import com.github.opensmsforwarder.domain.model.ForwardingType
import com.github.opensmsforwarder.extension.assistedViewModels
import com.github.opensmsforwarder.extension.bindCheckChangesTo
import com.github.opensmsforwarder.extension.bindClicksTo
import com.github.opensmsforwarder.extension.bindTextChangesTo
import com.github.opensmsforwarder.extension.observeWithLifecycle
import com.github.opensmsforwarder.extension.setTextIfChangedKeepState
import com.github.opensmsforwarder.extension.setValueIfChanged
import com.github.opensmsforwarder.extension.setVisibilityIfChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseForwardingMethodFragment : Fragment(R.layout.fragment_choose_forwarding_method) {

    private val binding by viewBinding(FragmentChooseForwardingMethodBinding::bind)
    private val viewModel by assistedViewModels<ChooseForwardingMethodViewModel, ChooseForwardingMethodViewModel.Factory> { factory ->
        factory.create(requireArguments().getLong(ID_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
    }

    private fun setListeners() {
        with(binding) {
            titleEt bindTextChangesTo viewModel::onTitleChanged
            emailRb bindCheckChangesTo { viewModel.onForwardingMethodChanged(ForwardingType.EMAIL) }
            smsRb bindCheckChangesTo { viewModel.onForwardingMethodChanged(ForwardingType.SMS) }
            arrowBackIv bindClicksTo viewModel::onBackClicked
            nextBtn bindClicksTo viewModel::onNextClicked
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    private fun renderState(state: Forwarding) {
        with(binding) {
            nextBtn.isEnabled = state.forwardingType != null
            titleEt.setTextIfChangedKeepState(state.title)
            emailRb.setValueIfChanged(state.isEmailForwardingType)
            smsRb.setValueIfChanged(state.isSmsForwardingType)
            smsInfoTv.setVisibilityIfChanged(state.isSmsForwardingType)
        }
    }

    companion object {
        fun newInstance(id: Long): ChooseForwardingMethodFragment =
            ChooseForwardingMethodFragment().apply {
                arguments = bundleOf(ID_KEY to id)
            }

        private const val ID_KEY = "ID_KEY"
    }
}
