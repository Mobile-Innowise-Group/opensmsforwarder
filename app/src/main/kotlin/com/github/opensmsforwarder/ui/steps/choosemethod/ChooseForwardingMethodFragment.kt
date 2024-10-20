package com.github.opensmsforwarder.ui.steps.choosemethod

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.FragmentChooseForwardingMethodBinding
import com.github.opensmsforwarder.extension.bindCheckChangesTo
import com.github.opensmsforwarder.extension.bindClicksTo
import com.github.opensmsforwarder.extension.bindTextChangesTo
import com.github.opensmsforwarder.extension.observeWithLifecycle
import com.github.opensmsforwarder.extension.setTextIfChangedKeepState
import com.github.opensmsforwarder.extension.setValueIfChanged
import com.github.opensmsforwarder.extension.setVisibilityIfChanged
import com.github.opensmsforwarder.model.ForwardingType
import com.github.opensmsforwarder.model.Recipient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseForwardingMethodFragment : Fragment(R.layout.fragment_choose_forwarding_method) {

    private val binding by viewBinding(FragmentChooseForwardingMethodBinding::bind)
    private val viewModel: ChooseForwardingMethodViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObservers()
    }

    private fun setListeners() {
        with(binding) {
            titleEt bindTextChangesTo viewModel::onTitleChanged
            radioButtonEmail bindCheckChangesTo { viewModel.onForwardingMethodChanged(ForwardingType.EMAIL) }
            radioButtonSms bindCheckChangesTo { viewModel.onForwardingMethodChanged(ForwardingType.SMS) }
            arrowBack bindClicksTo viewModel::onBackClicked
            nextBtn bindClicksTo viewModel::onNextClicked
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: Recipient) {
        with(binding) {
            nextBtn.isEnabled = state.forwardingType != null
            titleEt.setTextIfChangedKeepState(state.title)
            radioButtonEmail.setValueIfChanged(state.isEmailForwardingType)
            radioButtonSms.setValueIfChanged(state.isSmsForwardingType)
            smsInfoTv.setVisibilityIfChanged(state.isSmsForwardingType)
        }
    }
}
