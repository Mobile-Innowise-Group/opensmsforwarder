package org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentAddEmailDetailsBinding
import org.open.smsforwarder.extension.assistedViewModels
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.bindTextChangesTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.setAccessibilityFocus
import org.open.smsforwarder.extension.setTextIfChanged
import org.open.smsforwarder.extension.setTextIfChangedKeepState
import org.open.smsforwarder.extension.setVisibilityIfChanged
import org.open.smsforwarder.extension.showToast
import javax.inject.Inject


@AndroidEntryPoint
class AddEmailDetailsFragment : Fragment(R.layout.fragment_add_email_details) {

    private val binding by viewBinding(FragmentAddEmailDetailsBinding::bind)
    private val viewModel by assistedViewModels<AddEmailDetailsViewModel, AddEmailDetailsViewModel.Factory> { factory ->
        factory.create(requireArguments().getLong(ID_KEY))
    }

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                    val serverAuthCode = account.serverAuthCode
                    val email = account.email
                    if (serverAuthCode != null && email != null) {
                        viewModel.exchangeTokens(serverAuthCode, email)
                    } else {
                        showToast(R.string.error_google_sign_in)
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                            showToast(R.string.error_sign_in_cancelled)
                        }

                        GoogleSignInStatusCodes.NETWORK_ERROR -> {
                            showToast(R.string.error_network)
                        }

                        GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> {
                            showToast(R.string.error_sign_in_in_progress)
                        }

                        GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
                            showToast(R.string.error_sign_in_failed)
                        }

                        else -> {
                            showToast(getString(R.string.error_google_sign_in) + ": " + e.message)
                        }
                    }
                } catch (e: IllegalStateException) {
                    showToast(getString(R.string.error_google_sign_in) + ": " + e.message)
                } catch (e: SecurityException) {
                    showToast(getString(R.string.error_google_sign_in) + ": " + e.message)
                }
            } else {
                showToast(R.string.error_google_sign_in)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        binding.step2.setAccessibilityFocus()
    }

    private fun startGoogleSignIn() {
        // Sign out first to force account selection
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }
    }

    private fun setListeners() {
        with(binding) {
            arrowBackIv bindClicksTo viewModel::onBackClicked
            recipientEmailEt bindTextChangesTo viewModel::onEmailChanged
            signInBtn bindClicksTo { startGoogleSignIn() }
            signOutBtn bindClicksTo viewModel::onSignOutClicked
            nextBtn bindClicksTo viewModel::onNextClicked
        }
    }

    private fun setObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner, action = ::handleEffect)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    private fun renderState(state: AddEmailDetailsState) {
        with(binding) {
            signInTv.setVisibilityIfChanged(state.signInTvVisible)
            senderEmailTv.setVisibilityIfChanged(state.senderEmailVisible)
            senderEmailTv.setTextIfChanged(getString(R.string.signed_in_as, state.senderEmail))
            signInBtn.setVisibilityIfChanged(state.sigInBtnVisible)
            signOutBtn.setVisibilityIfChanged(state.signOutBtnVisible)
            recipientEmailEt.setTextIfChangedKeepState(state.recipientEmail)
            nextBtn.isEnabled = state.nextButtonEnabled
            recipientEmailLayout.error = state.inputErrorProvider?.asString(requireContext())
        }
    }

    private fun handleEffect(effect: AddEmailDetailsViewEffect) {
        when (effect) {
            is GoogleSignInErrorViewEffect -> {
                if (effect.error != null && effect.errorMessageRes != null) {
                    showToast(getString(effect.errorMessageRes, effect.error.message))
                } else {
                    effect.errorMessageRes?.let(::showToast)
                }
            }

            is GoogleSignInViewEffect -> handleSignInEffect()
        }
    }

    private fun handleSignInEffect() {
        // This method is now empty as the logic has been moved to the ViewModel
    }

    companion object {
        fun newInstance(id: Long): AddEmailDetailsFragment =
            AddEmailDetailsFragment().apply {
                arguments = bundleOf(ID_KEY to id)
            }

        private const val ID_KEY = "ID_KEY"
    }
}
