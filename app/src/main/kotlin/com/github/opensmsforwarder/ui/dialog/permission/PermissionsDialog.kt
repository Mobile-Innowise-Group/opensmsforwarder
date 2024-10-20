package com.github.opensmsforwarder.ui.dialog.permission

import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.analytics.AnalyticsEvents.PERMISSIONS_DIALOG_GO_TO_SETTINGS_CLICKED
import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.extension.notificationsPermissionGranted
import com.github.opensmsforwarder.extension.smsReceivePermissionGranted
import com.github.opensmsforwarder.extension.smsSendPermissionGranted
import javax.inject.Inject

class PermissionsDialog : DialogFragment() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val systemSettingsStartForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (requireActivity().smsReceivePermissionGranted() && requireActivity().smsSendPermissionGranted()
            && requireActivity().notificationsPermissionGranted()
        ) {
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCancelable(false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = requireNotNull(arguments) { "No arguments passed to PermissionsDialog" }
        val messageRes = arguments.getInt(MESSAGE_RES)
        val positiveButtonRes = arguments.getInt(POSITIVE_BUTTON_RES)

        return AlertDialog.Builder(requireActivity(), R.style.SmsAlertDialog)
            .setTitle(getString(R.string.app_name))
            .setMessage(getString(messageRes))
            .setPositiveButton(positiveButtonRes) { _, _ ->
                analyticsTracker.trackEvent(PERMISSIONS_DIALOG_GO_TO_SETTINGS_CLICKED)
                systemSettingsStartForResultLauncher.launch(getSystemSettingsIntent())
            }
            .create()
    }

    private fun getSystemSettingsIntent(): Intent =
        Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(URI_SCHEMA, requireActivity().packageName, null)
        )

    fun message(
        @StringRes messageRes: Int,
    ): PermissionsDialog = apply {
        arguments?.putInt(MESSAGE_RES, messageRes) ?: run {
            arguments = bundleOf(MESSAGE_RES to messageRes)
        }
    }

    fun positiveButton(
        @StringRes positiveButtonRes: Int,
    ): PermissionsDialog = apply {
        arguments?.putInt(POSITIVE_BUTTON_RES, positiveButtonRes) ?: run {
            arguments = bundleOf(POSITIVE_BUTTON_RES to positiveButtonRes)
        }
    }

    companion object {
        const val TAG = "PERMISSIONS_DIALOG_TAG"
        private const val URI_SCHEMA = "package"
        private const val MESSAGE_RES = "MESSAGE_RES"
        private const val POSITIVE_BUTTON_RES = "POSITIVE_BUTTON_RES"
    }
}
