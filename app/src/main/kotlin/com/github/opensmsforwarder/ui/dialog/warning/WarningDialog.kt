package com.github.opensmsforwarder.ui.dialog.warning

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.opensmsforwarder.R

class WarningDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments?.getString(ARG_MESSAGE).orEmpty()
        val title = arguments?.getString(ARG_TITLE).orEmpty()

        return AlertDialog.Builder(requireContext(),android.R.style.Theme_Material_Light_Dialog_Alert)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
            .create()
    }

    companion object {
        const val TAG = "WARNING_DIALOG_TAG"
        private const val ARG_MESSAGE = "MESSAGE"
        private const val ARG_TITLE = "TITLE"

        fun newInstance(title: String, message: String): WarningDialog {
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
            }
            return WarningDialog().apply {
                arguments = args
            }
        }
    }
}
