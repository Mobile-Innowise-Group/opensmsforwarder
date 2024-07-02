package com.github.opensmsforwarder.ui.dialog.edit

interface EditDialogListener {
    fun onButtonEditClicked(itemId: Long, newValue: String)

    fun getInputErrorOrNull(itemId: Long, newValue: String, oldValue: String): Int?
}
