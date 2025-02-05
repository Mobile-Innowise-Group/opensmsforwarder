package org.open.smsforwarder.ui.history

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentForwardingHistoryBinding
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.showToast
import org.open.smsforwarder.extension.unsafeLazy
import org.open.smsforwarder.ui.history.adapter.SmsHistoryAdapter

@AndroidEntryPoint
class ForwardingHistoryFragment : Fragment(R.layout.fragment_forwarding_history) {

    private val binding by viewBinding(FragmentForwardingHistoryBinding::bind)
    private val viewModel: ForwardingHistoryViewModel by viewModels()
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

    private fun renderState(state: ForwardingHistoryState) {
        adapter.submitList(state.historyItems)
        with(binding) {
            emptyStateText.isVisible = state.historyItems.isEmpty()
            historyItems.isVisible = state.historyItems.isNotEmpty()
        }
    }

    private fun handleEffect(effect: ForwardingHistoryEffect) {
        when (effect) {
            is ForwardingHistoryEffect.RetryEffect -> {
                showToast(R.string.forwarding_history_retrying)
            }
        }
    }

    private fun setUpAdapter() {
        binding.historyItems.adapter = adapter
    }
}
