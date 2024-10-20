package com.github.opensmsforwarder.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.analytics.AnalyticsEvents.BATTERY_OPTIMIZATION_DIALOG_GO_TO_SETTINGS_CLICKED
import com.github.opensmsforwarder.analytics.AnalyticsEvents.REQUEST_PERMISSIONS
import com.github.opensmsforwarder.analytics.AnalyticsTracker
import com.github.opensmsforwarder.databinding.FragmentHomeBinding
import com.github.opensmsforwarder.extension.bindClicksTo
import com.github.opensmsforwarder.extension.notificationsPermissionGranted
import com.github.opensmsforwarder.extension.observeWithLifecycle
import com.github.opensmsforwarder.extension.showOkDialog
import com.github.opensmsforwarder.extension.smsReceivePermissionGranted
import com.github.opensmsforwarder.extension.smsSendPermissionGranted
import com.github.opensmsforwarder.extension.unsafeLazy
import com.github.opensmsforwarder.ui.dialog.delete.DeleteDialog
import com.github.opensmsforwarder.ui.dialog.delete.DeleteDialogListener
import com.github.opensmsforwarder.ui.dialog.permission.PermissionsDialog
import com.github.opensmsforwarder.ui.home.adapter.HomeAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), DeleteDialogListener {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by viewModels()
    private val adapter by unsafeLazy {
        HomeAdapter(
            onItemEdit = viewModel::onItemEditClicked,
            onItemRemove = viewModel::onItemRemoveClicked
        )
    }

    private val permissionsResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) -> {
                    showPermissionsRationaleDialog()
                }

                permissions[Manifest.permission.RECEIVE_SMS] != true ||
                        permissions[Manifest.permission.SEND_SMS] != true -> navigateGoToSettingsDialog()

            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        setListeners()
        setObservers()
    }

    override fun onDestroyView() {
        binding.recipients.adapter = null
        super.onDestroyView()
    }

    private fun setAdapter() {
        binding.recipients.adapter = adapter
        binding.recipients.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun setListeners() {
        binding.powerManagementWarning bindClicksTo viewModel::onBatteryOptimizationWarningClicked
        binding.addNewRecipient bindClicksTo viewModel::onAddNewRecipientClicked
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner) { effect ->
            handleEffect(effect)
        }
    }

    private fun renderState(state: HomeState) {
        adapter.submitList(state.recipients)
        binding.powerManagementWarning.isVisible =
            state.rules.isNotEmpty() && batteryOptimizationDisabled()
        binding.recipients.isVisible = state.recipients.isNotEmpty()
        binding.textEmpty.isVisible = state.recipients.isEmpty()
        checkPermissions(state.recipients.isNotEmpty())
    }

    override fun onButtonDeleteClicked(id: Long) {
        viewModel.onRemoveConfirmed(id)
    }

    private fun handleEffect(effect: HomeEffect) {
        when (effect) {
            is DeleteEffect -> DeleteDialog
                .create(effect.id)
                .title(R.string.remove_title)
                .message(R.string.remove_message)
                .positiveButton(R.string.ok)
                .show(childFragmentManager, DeleteDialog.TAG)

            BatteryWarningEffect ->
                requireActivity().showOkDialog(
                    title = requireActivity().getString(R.string.battery_save_warning_title),
                    message = requireActivity().getString(R.string.battery_save_warning_message),
                    buttonNameRes = R.string.go_to_settings,
                    dialogStyle = R.style.SmsAlertDialog,
                    okClickAction = {
                        if (batteryOptimizationDisabled()) {
                            analyticsTracker.trackEvent(
                                BATTERY_OPTIMIZATION_DIALOG_GO_TO_SETTINGS_CLICKED
                            )
                            val intent =
                                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                            requireActivity().startActivity(intent)
                        }
                    }
                )

            GoToSettingsEffect ->
                PermissionsDialog().message(R.string.permissions_error)
                    .positiveButton(R.string.go_to_settings)
                    .show(childFragmentManager, PermissionsDialog.TAG)

            PermissionsRationalEffect ->
                requireActivity().showOkDialog(
                    title = getString(R.string.permissions_rationale_title),
                    message = getString(R.string.permissions_rationale_message),
                    dialogStyle = R.style.SmsAlertDialog,
                    okClickAction = { requestPermissions() }
                )
        }
    }

    private fun batteryOptimizationDisabled(): Boolean {
        val powerManager =
            requireContext().applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        return !powerManager.isIgnoringBatteryOptimizations(requireContext().packageName)
    }

    private fun requestPermissions() {
        analyticsTracker.trackEvent(REQUEST_PERMISSIONS)
        permissionsResultLauncher.launch(
            mutableListOf<String>().apply {
                add(Manifest.permission.RECEIVE_SMS)
                add(Manifest.permission.SEND_SMS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.POST_NOTIFICATIONS)
                }
            }.toTypedArray()
        )
    }

    private fun checkPermissions(isNotEmptyRecipientList: Boolean) {
        if (isNotEmptyRecipientList && (!requireActivity().smsReceivePermissionGranted()
                    || !requireActivity().smsSendPermissionGranted() || !requireActivity().notificationsPermissionGranted())
        ) {
            requestPermissions()
        }
    }

    private fun showPermissionsRationaleDialog() {
        viewModel.onPermissionsRationaleRequired()
    }

    private fun navigateGoToSettingsDialog() {
        viewModel.onGoToSettingsRequired()
    }
}
