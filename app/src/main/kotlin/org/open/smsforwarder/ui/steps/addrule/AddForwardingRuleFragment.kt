package org.open.smsforwarder.ui.steps.addrule

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentAddForwardingRuleBinding
import org.open.smsforwarder.extension.assistedViewModels
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.bindTextChangesTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.unsafeLazy
import org.open.smsforwarder.ui.dialog.delete.DeleteDialog
import org.open.smsforwarder.ui.dialog.delete.DeleteDialogListener
import org.open.smsforwarder.ui.dialog.edit.EditDialog
import org.open.smsforwarder.ui.dialog.edit.EditDialogListener
import org.open.smsforwarder.ui.steps.addrule.adapter.RulesAdapter

@AndroidEntryPoint
class AddForwardingRuleFragment : Fragment(R.layout.fragment_add_forwarding_rule),
    DeleteDialogListener, EditDialogListener {

    private val binding by viewBinding(FragmentAddForwardingRuleBinding::bind)
    private val viewModel: AddForwardingRuleViewModel by
    assistedViewModels<AddForwardingRuleViewModel, AddForwardingRuleViewModel.Factory> { factory ->
        factory.create(requireArguments().getLong(ID_KEY))
    }
    private val adapter by unsafeLazy {
        RulesAdapter(
            onItemEdit = { viewModel.onItemEditClicked(it) },
            onItemRemove = { viewModel.onItemRemoveClicked(it) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setListeners()
        setObservers()
    }

    private fun setAdapter() {
        with(binding) {
            rulesRv.adapter = adapter
            rulesRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setListeners() {
        with(binding) {
            arrowBackIv bindClicksTo viewModel::onBackClicked
            messagePatternEt bindTextChangesTo viewModel::onNewRuleEntered
            buttonAddRuleBtn bindClicksTo {
                viewModel.onAddRuleClicked(messagePatternEt.text.toString())
                messagePatternEt.setText("")
            }
            finishBtn bindClicksTo viewModel::onFinishClicked
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner, action = ::handleEffect)
    }

    private fun renderState(state: AddForwardingRuleState) {
        with(binding) {
            adapter.submitList(state.rules)
            emptyTv.isVisible = state.rules.isEmpty()
            finishBtn.isEnabled = state.rules.isNotEmpty()
            messagePatternLayout.error = state.errorMessage?.let { getString(it) }
            buttonAddRuleBtn.isEnabled = state.isAddRuleButtonEnabled
        }
    }

    private fun handleEffect(effect: ForwardingRuleEffect) {
        when (effect) {
            is ForwardingDeleteRuleEffect ->
                DeleteDialog
                    .create(effect.rule.id)
                    .title(R.string.delete_title)
                    .message(R.string.delete_message, effect.rule.textRule)
                    .show(childFragmentManager, DeleteDialog.TAG)

            is ForwardingEditRuleEffect ->
                EditDialog
                    .create(effect.rule.id, effect.rule.textRule)
                    .title(R.string.edit_rule_title)
                    .message(R.string.edit_rule_message, effect.rule.textRule)
                    .show(childFragmentManager, EditDialog.TAG)
        }
    }

    override fun onButtonRemoveClicked(id: Long) {
        viewModel.onItemRemoved(id)
    }

    override fun onButtonEditClicked(itemId: Long, newValue: String) {
        viewModel.onItemEdited(itemId, newValue)
    }

    override fun getInputErrorOrNull(itemId: Long, newValue: String, oldValue: String): Int? =
        when {
            viewModel.isPatternExists(newValue) && newValue != oldValue -> R.string.add_rule_error
            newValue.isBlank() -> R.string.blank_rule_error
            else -> null
        }

    companion object {
        fun newInstance(id: Long): AddForwardingRuleFragment =
            AddForwardingRuleFragment().apply {
                arguments = bundleOf(ID_KEY to id)
            }

        private const val ID_KEY = "ID_KEY"
    }
}
