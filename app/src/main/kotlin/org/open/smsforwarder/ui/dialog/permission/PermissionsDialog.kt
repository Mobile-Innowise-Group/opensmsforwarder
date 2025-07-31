package org.open.smsforwarder.ui.dialog.permission

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import org.open.smsforwarder.R
import org.open.smsforwarder.analytics.AnalyticsEvents.PERMISSIONS_DIALOG_GO_TO_SETTINGS_CLICKED
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.extension.notificationsPermissionGranted
import org.open.smsforwarder.extension.smsReceivePermissionGranted
import javax.inject.Inject

class PermissionsDialog : DialogFragment() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val systemSettingsStartForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (requireActivity().smsReceivePermissionGranted()
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
