package org.open.smsforwarder.ui.steps.choosemethod

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentChooseForwardingMethodBinding
import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.ForwardingType
import org.open.smsforwarder.extension.assistedViewModels
import org.open.smsforwarder.extension.bindCheckChangesTo
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.bindTextChangesTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.setAccessibilityFocus
import org.open.smsforwarder.extension.setTextIfChangedKeepState
import org.open.smsforwarder.extension.setValueIfChanged

@AndroidEntryPoint
class ChooseForwardingMethodFragment : Fragment(R.layout.fragment_choose_forwarding_method) {

    private val binding by viewBinding(FragmentChooseForwardingMethodBinding::bind)
    private val viewModel: ChooseForwardingMethodViewModel by
    assistedViewModels<ChooseForwardingMethodViewModel, ChooseForwardingMethodViewModel.Factory> { factory ->
        factory.create(requireArguments().getLong(ID_KEY))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        binding.step1.setAccessibilityFocus()
    }

    private fun setListeners() {
        with(binding) {
            titleEt bindTextChangesTo viewModel::onTitleChanged
            emailRb bindCheckChangesTo { viewModel.onForwardingMethodChanged(ForwardingType.EMAIL) }
            telegramRb bindCheckChangesTo { viewModel.onForwardingMethodChanged(ForwardingType.TELEGRAM) }
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
            titleEt.setTextIfChangedKeepState(state.title)
            emailRb.setValueIfChanged(state.isEmailForwardingType)
            telegramRb.setValueIfChanged(state.isTelegramForwardingType)
            nextBtn.isEnabled = state.forwardingType != null
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
