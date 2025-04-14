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
import org.open.smsforwarder.extension.unsafeLazy
import org.open.smsforwarder.ui.history.adapter.HistoryAdapter

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_forwarding_history) {

    private val binding by viewBinding(FragmentForwardingHistoryBinding::bind)
    private val viewModel: HistoryViewModel by viewModels()
    private val adapter by unsafeLazy { HistoryAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpAdapter()
        setUpListeners()
        setObservers()
    }

    private fun setUpListeners() {
        binding.arrowBackIv bindClicksTo viewModel::onBackClicked
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
    }

    private fun renderState(state: HistoryState) {
        adapter.submitList(state.historyItems)
        binding.emptyStateText.isVisible = state.isEmptyStateTextVisible
        binding.historyItems.isVisible = state.isHistoryItemsVisible
    }

    private fun setUpAdapter() {
        binding.historyItems.adapter = adapter
    }
}
