package org.open.smsforwarder.ui.steps.addrecipientdetails.addtelegramdetails

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentAddTelegramDetailsBinding
import org.open.smsforwarder.extension.assistedViewModels
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.bindTextChangesTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.setTextIfChangedKeepState
import org.open.smsforwarder.extension.showTooltip

@AndroidEntryPoint
class AddTelegramDetailsFragment : Fragment(R.layout.fragment_add_telegram_details) {

    private val binding by viewBinding(FragmentAddTelegramDetailsBinding::bind)
    private val viewModel by
    assistedViewModels<AddTelegramDetailsViewModel, AddTelegramDetailsViewModel.Factory> { factory ->
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
            telegramApiTokenEt bindTextChangesTo viewModel::onTelegramApiTokenChanged
            telegramApiTokenIv bindClicksTo { telegramApiTokenIv.showTooltip(R.string.telegram_api_token_tooltip) }
            telegramChatIdEt bindTextChangesTo viewModel::onTelegramChatIdChanged
            telegramChatIdIv bindClicksTo { telegramChatIdIv.showTooltip(R.string.telegram_chat_id_tooltip) }
            nextBtn bindClicksTo viewModel::onNextClicked
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    private fun renderState(state: AddTelegramDetailsState) {
        with(binding) {
            telegramApiTokenEt.setTextIfChangedKeepState(state.telegramApiToken)
            telegramChatIdEt.setTextIfChangedKeepState(state.telegramChatId)
            telegramApiTokenLayout.error = state.inputErrorApiToken?.asString(requireContext())
            telegramChatIdLayout.error = state.inputErrorChatId?.asString(requireContext())
            nextBtn.isEnabled = state.nextButtonEnabled
        }
    }

    companion object {
        fun newInstance(id: Long): AddTelegramDetailsFragment =
            AddTelegramDetailsFragment().apply {
                arguments = bundleOf(ID_KEY to id)
            }

        private const val ID_KEY = "ID_KEY"
    }
}
