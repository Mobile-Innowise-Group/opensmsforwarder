package com.github.opensmsforwarder.ui.dialog.delete

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.DialogDeleteBinding
import com.github.opensmsforwarder.extension.bindClicksTo

class DeleteDialog : DialogFragment(R.layout.dialog_delete) {

    private val binding by viewBinding(DialogDeleteBinding::bind)
    private var listener: DeleteDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)

        listener = parentFragment as? DeleteDialogListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
    }

    @Suppress("SpreadOperator")
    private fun initViews() {
        with(binding) {
            val arguments = requireNotNull(arguments) { "No arguments passed to DeleteDialog" }
            require(arguments.getLong(ITEM_ID, -1L) != -1L) { "No itemId passed to DeleteDialog" }
            val titleRes = arguments.getInt(TITLE_RES, R.string.empty)
            val messageRes = arguments.getInt(MESSAGE_RES, R.string.empty)
            val messageArgs = arguments.getStringArray(MESSAGE_ARGS)
            val positiveButtonRes = arguments.getInt(POSITIVE_BUTTON_RES, R.string.delete)
            val negativeButtonRes = arguments.getInt(NEGATIVE_BUTTON_RES, R.string.cancel)

            titleTv.text = getString(titleRes)
            messageTv.text = messageArgs
                ?.let { getString(messageRes, *messageArgs) }
                ?: getString(messageRes)
            positiveBtn.text = getString(positiveButtonRes)
            negativeBtn.text = getString(negativeButtonRes)
        }
    }

    private fun initListeners() {
        with(binding) {
            positiveBtn bindClicksTo {
                dismiss()
                listener?.onButtonDeleteClicked(getItemId())
            }

            negativeBtn bindClicksTo {
                dismiss()
            }
        }
    }

    fun title(
        @StringRes titleRes: Int,
    ): DeleteDialog = apply {
        arguments?.putInt(TITLE_RES, titleRes) ?: run {
            arguments = bundleOf(TITLE_RES to titleRes)
        }
    }

    fun message(
        @StringRes messageRes: Int,
    ): DeleteDialog = apply {
        arguments?.putInt(MESSAGE_RES, messageRes) ?: run {
            arguments = bundleOf(MESSAGE_RES to messageRes)
        }
    }

    fun message(
        @StringRes messageRes: Int,
        vararg args: String,
    ): DeleteDialog = apply {
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
    ): DeleteDialog = apply {
        arguments?.putInt(POSITIVE_BUTTON_RES, positiveButtonRes) ?: run {
            arguments = bundleOf(POSITIVE_BUTTON_RES to positiveButtonRes)
        }
    }

    fun negativeButton(
        @StringRes negativeButtonRes: Int,
    ): DeleteDialog = apply {
        arguments?.putInt(NEGATIVE_BUTTON_RES, negativeButtonRes) ?: run {
            arguments = bundleOf(NEGATIVE_BUTTON_RES to negativeButtonRes)
        }
    }

    private fun getItemId(): Long {
        val itemId = arguments?.getLong(ITEM_ID, -1L) ?: -1L
        require(itemId != -1L) { "No itemId found in DeleteDialog" }
        return itemId
    }

    companion object {
        const val TAG = "DELETE_DIALOG_TAG"
        private const val ITEM_ID = "ITEM_ID"
        private const val TITLE_RES = "TITLE_RES"
        private const val MESSAGE_RES = "MESSAGE_RES"
        private const val MESSAGE_ARGS = "MESSAGE_ARGS"
        private const val POSITIVE_BUTTON_RES = "POSITIVE_BUTTON_RES"
        private const val NEGATIVE_BUTTON_RES = "NEGATIVE_BUTTON_RES"

        fun create(
            itemId: Long,
        ): DeleteDialog = DeleteDialog().apply { arguments = bundleOf(ITEM_ID to itemId) }
    }
}
