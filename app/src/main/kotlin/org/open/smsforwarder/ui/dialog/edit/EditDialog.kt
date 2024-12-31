package org.open.smsforwarder.ui.dialog.edit

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.DialogEditRuleBinding
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.bindTextChangesTo

class EditDialog : DialogFragment(R.layout.dialog_edit_rule) {

    private val binding by viewBinding(DialogEditRuleBinding::bind)
    private var listener: EditDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)

        listener = parentFragment as? EditDialogListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
    }

    @Suppress("SpreadOperator")
    private fun initViews() {
        with(binding) {
            val arguments = requireNotNull(arguments) { "No arguments passed to EditDialog" }
            require(arguments.getLong(ITEM_ID, -1L) != -1L) { "No itemId passed to EditDialog" }
            require(
                arguments.getString(VALUE_TO_EDIT, "").isNotEmpty()
            ) { "No value passed to EditDialog" }
            val titleRes = arguments.getInt(TITLE_RES, R.string.empty)
            val messageRes = arguments.getInt(MESSAGE_RES, R.string.empty)
            val messageArgs = arguments.getStringArray(MESSAGE_ARGS)
            val valueToEdit = arguments.getString(VALUE_TO_EDIT, "")
            val positiveButtonRes = arguments.getInt(POSITIVE_BUTTON_RES, R.string.save)
            val negativeButtonRes = arguments.getInt(NEGATIVE_BUTTON_RES, R.string.cancel)

            titleTv.text = getString(titleRes)
            messageTv.text = messageArgs
                ?.let { getString(messageRes, *messageArgs) }
                ?: getString(messageRes)
            editRule.setText(valueToEdit)
            positiveBtn.text = getString(positiveButtonRes)
            negativeBtn.text = getString(negativeButtonRes)
        }
    }

    private fun initListeners() {
        with(binding) {
            positiveBtn bindClicksTo {
                dismiss()
                listener?.onButtonEditClicked(getItemId(), editRule.text.toString())
            }

            negativeBtn bindClicksTo {
                dismiss()
            }

            editRule bindTextChangesTo { newValue ->
                val oldValue = arguments?.getString(VALUE_TO_EDIT, "") ?: ""
                val inputError = listener?.getInputErrorOrNull(getItemId(), newValue, oldValue)
                layourEditRule.error = inputError?.let { getString(it) }
                positiveBtn.isEnabled = inputError == null
            }
        }
    }

    fun title(
        @StringRes titleRes: Int,
    ): EditDialog = apply {
        arguments?.putInt(TITLE_RES, titleRes) ?: run {
            arguments = bundleOf(TITLE_RES to titleRes)
        }
    }

    fun message(
        @StringRes messageRes: Int,
    ): EditDialog = apply {
        arguments?.putInt(MESSAGE_RES, messageRes) ?: run {
            arguments = bundleOf(MESSAGE_RES to messageRes)
        }
    }

    fun message(
        @StringRes messageRes: Int,
        vararg args: String,
    ): EditDialog = apply {
        arguments?.let { bundle ->
            bundle.putInt(MESSAGE_RES, messageRes)
            bundle.putStringArray(MESSAGE_ARGS, arrayOf(*args))
        } ?: run {
            arguments = bundleOf(
                MESSAGE_RES to messageRes,
                MESSAGE_ARGS to arrayOf(*args)
            )
        }
    }

    fun positiveButton(
        @StringRes positiveButtonRes: Int,
    ): EditDialog = apply {
        arguments?.putInt(POSITIVE_BUTTON_RES, positiveButtonRes) ?: run {
            arguments = bundleOf(POSITIVE_BUTTON_RES to positiveButtonRes)
        }
    }

    fun negativeButton(
        @StringRes negativeButtonRes: Int,
    ): EditDialog = apply {
        arguments?.putInt(NEGATIVE_BUTTON_RES, negativeButtonRes) ?: run {
            arguments = bundleOf(NEGATIVE_BUTTON_RES to negativeButtonRes)
        }
    }

    private fun getItemId(): Long {
        val itemId = arguments?.getLong(ITEM_ID, -1L) ?: -1L
        require(itemId != -1L) { "No itemId found in EditDialog" }
        return itemId
    }

    companion object {
        const val TAG = "EDIT_DIALOG_TAG"
        private const val ITEM_ID = "ITEM_ID"
        private const val VALUE_TO_EDIT = "VALUE_TO_EDIT"
        private const val TITLE_RES = "TITLE_RES"
        private const val MESSAGE_RES = "MESSAGE_RES"
        private const val MESSAGE_ARGS = "MESSAGE_ARGS"
        private const val POSITIVE_BUTTON_RES = "POSITIVE_BUTTON_RES"
        private const val NEGATIVE_BUTTON_RES = "NEGATIVE_BUTTON_RES"

        fun create(
            itemId: Long,
            valueToEdit: String,
        ) = EditDialog().apply {
            arguments = bundleOf(
                ITEM_ID to itemId,
                VALUE_TO_EDIT to valueToEdit
            )
        }
    }
}
