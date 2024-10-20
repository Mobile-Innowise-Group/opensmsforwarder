package com.github.opensmsforwarder.ui.steps.addrule

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.FragmentAddForwardingRuleBinding
import com.github.opensmsforwarder.extension.bindClicksTo
import com.github.opensmsforwarder.extension.bindTextChangesTo
import com.github.opensmsforwarder.extension.observeWithLifecycle
import com.github.opensmsforwarder.extension.unsafeLazy
import com.github.opensmsforwarder.ui.dialog.delete.DeleteDialog
import com.github.opensmsforwarder.ui.dialog.delete.DeleteDialogListener
import com.github.opensmsforwarder.ui.dialog.edit.EditDialog
import com.github.opensmsforwarder.ui.dialog.edit.EditDialogListener
import com.github.opensmsforwarder.ui.steps.addrule.adapter.RulesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddForwardingRuleFragment : Fragment(R.layout.fragment_add_forwarding_rule),
    DeleteDialogListener, EditDialogListener {

    private val binding by viewBinding(FragmentAddForwardingRuleBinding::bind)
    private val viewModel: AddForwardingRuleViewModel by viewModels()
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
            finishBtn bindClicksTo viewModel::onFinishClicked
            arrowBack bindClicksTo viewModel::onBackClicked
            buttonAddRule bindClicksTo {
                viewModel.onAddRuleClicked(messagePatternEt.text.toString())
                messagePatternEt.setText("")
            }
            messagePatternEt bindTextChangesTo {
                viewModel.onNewRuleEntered(it)
            }
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner) { effect ->
            handleEffect(effect)
        }
    }

    private fun renderState(state: AddForwardingRuleState) {
        with(binding) {
            adapter.submitList(state.rules)
            rulesEmpty.isVisible = state.rules.isEmpty()
            finishBtn.isEnabled = state.rules.isNotEmpty()
            messagePatternLayout.error = state.errorMessage?.let {
                getString(it)
            }
            buttonAddRule.isEnabled = state.isAddRuleButtonEnabled
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

    override fun onButtonDeleteClicked(id: Long) {
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
}
