package org.open.smsforwarder.ui.steps.addrecipientdetails.addgooglechatdetails

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentAddGoogleChatDetailsBinding
import org.open.smsforwarder.extension.assistedViewModels
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.bindTextChangesTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.setTextIfChangedKeepState
import org.open.smsforwarder.extension.showTooltip

@AndroidEntryPoint
class AddGoogleChatDetailsFragment : Fragment(R.layout.fragment_add_google_chat_details) {

    private val binding by viewBinding(FragmentAddGoogleChatDetailsBinding::bind)
    private val viewModel by
    assistedViewModels<AddGoogleChatDetailsViewModel, AddGoogleChatDetailsViewModel.Factory> { factory ->
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
            googleChatWebHookEt bindTextChangesTo viewModel::onGoogleChatWebHookChanged
            googleChatIv bindClicksTo { googleChatIv.showTooltip(R.string.google_chat_web_hook_tooltip) }
            nextBtn bindClicksTo viewModel::onNextClicked
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    private fun renderState(state: AddGoogleChatDetailsState) {
        with(binding) {
            googleChatWebHookEt.setTextIfChangedKeepState(state.googleChatWebHook)
            googleChatWebHookLayout.error = state.inputErrorGoogleChat?.asString(requireContext())
            nextBtn.isEnabled = state.nextButtonEnabled
        }
    }

    companion object {
        fun newInstance(id: Long): AddGoogleChatDetailsFragment =
            AddGoogleChatDetailsFragment().apply {
                arguments = bundleOf(ID_KEY to id)
            }

        private const val ID_KEY = "ID_KEY"
    }
}
