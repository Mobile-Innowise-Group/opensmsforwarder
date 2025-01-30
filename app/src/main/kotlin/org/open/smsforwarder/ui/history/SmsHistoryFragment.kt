package org.open.smsforwarder.ui.history

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentSmsHistoryBinding
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.unsafeLazy
import org.open.smsforwarder.ui.history.adapter.SmsHistoryAdapter

@AndroidEntryPoint
class SmsHistoryFragment : Fragment(R.layout.fragment_sms_history) {

    private val binding by viewBinding(FragmentSmsHistoryBinding::bind)
    private val viewModel: SmsHistoryViewModel by viewModels()
    private val adapter by unsafeLazy {
        SmsHistoryAdapter(
            onRetry = viewModel::onRetryClicked
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        setUpListeners()
        setObservers()
    }

    private fun setUpListeners() {
        with(binding) {
            arrowBackIv bindClicksTo viewModel::onBackClicked
        }
    }

    private fun setObservers() {
        viewModel.apply {
            viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
            viewEffect.observeWithLifecycle(viewLifecycleOwner, action = ::handleEffect)
        }
    }

    private fun renderState(state: SmsHistoryState) {
        adapter.submitList(state.historyItems)
        with(binding) {
            emptyStateText.isVisible = state.historyItems.isEmpty()
            historyItems.isVisible = state.historyItems.isNotEmpty()
        }
    }

    private fun handleEffect(effect: SmsHistoryEffect) {
        when (effect) {
            is SmsHistoryEffect.RetryEffect -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.sms_history_retrying),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setUpAdapter() {
        binding.historyItems.adapter = adapter
    }
}
