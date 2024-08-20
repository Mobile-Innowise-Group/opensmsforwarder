package com.github.opensmsforwarder

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.github.opensmsforwarder.databinding.ActivityMainBinding
import com.github.opensmsforwarder.extension.showOkDialog
import com.github.opensmsforwarder.extension.smsReceivePermissionGranted
import com.github.opensmsforwarder.extension.smsSendPermissionGranted
import com.github.opensmsforwarder.navigation.AnimatedAppNavigator
import com.github.opensmsforwarder.navigation.Screens.splashFragment
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Replace
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val navigator = AnimatedAppNavigator(this, R.id.container)

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    private val systemSettingsStartForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (smsReceivePermissionGranted() && smsSendPermissionGranted()) {
            navigateToHomeScreenIfNeed()
        }
    }

    private val permissionsResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.RECEIVE_SMS] == true &&
                        permissions[Manifest.permission.SEND_SMS] == true -> {
                    navigateToHomeScreenIfNeed()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) -> {
                    showPermissionsRational()
                }

                else -> showError(isShow = true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goToSystemSettings.setOnClickListener {
            systemSettingsStartForResultLauncher.launch(getSystemSettingsIntent())
        }

        if (smsReceivePermissionGranted() && smsSendPermissionGranted()) {
            if (savedInstanceState == null) {
                navigateToHomeScreenIfNeed()
            }
            // fix back press when error is shown not on root screen
        } else {
            requestPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun getSystemSettingsIntent(): Intent =
        Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(URI_SCHEMA, packageName, null)
        )

    private fun navigateToHomeScreenIfNeed() {
        showError(isShow = false)
        if (isContainerEmpty()) {
            navigator.applyCommands(arrayOf(Replace(splashFragment())))
        }
    }

    private fun showPermissionsRational() {
        showOkDialog(
            title = getString(R.string.permissions_rationale_title),
            message = getString(R.string.permissions_rationale_message),
            okClickAction = { requestPermissions() }
        )
    }

    private fun requestPermissions() {
        permissionsResultLauncher.launch(
            arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
            )
        )
    }

    private fun showError(isShow: Boolean) {
        binding.error.isVisible = isShow
        binding.container.isVisible = !isShow
    }

    private fun isContainerEmpty(): Boolean = binding.container.childCount == 0

    companion object {
        private const val URI_SCHEMA = "package"
    }
}
