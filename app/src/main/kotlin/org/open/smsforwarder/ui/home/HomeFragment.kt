package org.open.smsforwarder.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.analytics.AnalyticsEvents.BATTERY_OPTIMIZATION_DIALOG_GO_TO_SETTINGS_CLICKED
import org.open.smsforwarder.analytics.AnalyticsEvents.REQUEST_PERMISSIONS
import org.open.smsforwarder.analytics.AnalyticsTracker
import org.open.smsforwarder.databinding.FragmentHomeBinding
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.showOkDialog
import org.open.smsforwarder.extension.unsafeLazy
import org.open.smsforwarder.ui.dialog.delete.DeleteDialog
import org.open.smsforwarder.ui.dialog.delete.DeleteDialogListener
import org.open.smsforwarder.ui.home.adapter.HomeAdapter
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

    private val permissions = mutableListOf<String>().apply {
        add(Manifest.permission.RECEIVE_SMS)
        add(Manifest.permission.SEND_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.all { it.value } -> viewModel.onPermissionsGranted()
                shouldShowRationale() -> viewModel.onPermissionsRationaleRequired()
                else -> viewModel.onPermissionsPermanentlyDenied()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setListeners()
        setObservers()
        setSpans()
    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }

    override fun onDestroyView() {
        binding.forwardings.adapter = null
        super.onDestroyView()
    }

    private fun setAdapter() {
        binding.forwardings.adapter = adapter
        binding.forwardings.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun setListeners() {
        binding.powerManagementWarning bindClicksTo viewModel::onBatteryOptimizationWarningClicked
        binding.feedback bindClicksTo viewModel::onFeedbackClicked
        binding.startNewForwardingBtn bindClicksTo viewModel::onNewForwardingClicked
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner, action = ::handleEffect)
    }

    private fun setSpans() {
        val span = makeClickableSpan(
            getString(R.string.permission_description_permanently),
            getString(R.string.go_to_settings)
        ) {
            viewModel.onGoToSettingsRequired()
        }
        binding.permissionsPermanentlyDeniedTv.movementMethod = LinkMovementMethod.getInstance()
        binding.permissionsPermanentlyDeniedTv.setText(span, TextView.BufferType.SPANNABLE)
    }

    private fun renderState(state: HomeState) {
        with(binding) {
            adapter.submitList(state.forwardings)
            powerManagementWarning.isVisible =
                state.hasAtLeastOneCompletedItem() && batteryOptimizationDisabled()
            forwardings.isVisible = state.forwardings.isNotEmpty()
            emptyStateText.isVisible = state.forwardings.isEmpty()
            permissionPermanentlyDeniedInfo.isVisible =
                state.hasAtLeastOneCompletedItem() && state.needToShowPermissionPermanentInfo
        }
    }

    private fun shouldShowRationale(): Boolean =
        permissions.any(::shouldShowRequestPermissionRationale)

    override fun onButtonRemoveClicked(id: Long) {
        viewModel.onRemoveConfirmed(id)
    }

    private fun handleEffect(effect: HomeEffect) {
        when (effect) {
            is DeleteEffect ->
                DeleteDialog
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

            GoToSettingsEffect -> startActivity(getSystemSettingsIntent())

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

    private fun getSystemSettingsIntent(): Intent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(URI_SCHEMA, requireActivity().packageName, null)
        )

    private fun requestPermissions() {
        analyticsTracker.trackEvent(REQUEST_PERMISSIONS)
        permissionLauncher.launch(permissions)
    }

    private fun makeClickableSpan(
        text: String,
        phrase: String,
        listener: () -> Unit
    ): SpannableString {
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {

            override fun onClick(view: View) {
                listener.invoke()
            }
        }
        val start = text.indexOf(phrase)
        val end = start + phrase.length
        spannableString.setSpan(
            clickableSpan,
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    companion object {
        private const val URI_SCHEMA = "package"
    }
}
