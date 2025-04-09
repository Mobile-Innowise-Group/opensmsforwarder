package org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails

import android.app.Activity
import android.app.PendingIntent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import android.view.accessibility.AccessibilityEvent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.tasks.Task
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
import org.open.smsforwarder.utils.runCatchingWithCancellation
import javax.inject.Inject


@AndroidEntryPoint
class AddEmailDetailsFragment : Fragment(R.layout.fragment_add_email_details) {

    private val binding by viewBinding(FragmentAddEmailDetailsBinding::bind)
    private val viewModel by assistedViewModels<AddEmailDetailsViewModel, AddEmailDetailsViewModel.Factory> { factory ->
        factory.create(requireArguments().getLong(ID_KEY))
    }

    @Inject
    lateinit var credentialManager: CredentialManager

    @Inject
    lateinit var credentialRequest: GetCredentialRequest

    @Inject
    lateinit var authorizationResult: Task<AuthorizationResult>

    private val sigInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            handleResult(result)
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

    private fun handleResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val authorizationResult: AuthorizationResult =
                Identity.getAuthorizationClient(requireActivity()).getAuthorizationResultFromIntent(
                    result.data
                )
            val authServer = authorizationResult.serverAuthCode
            authServer?.let { viewModel.exchangeTokens(it) }
        } else {
            //TODO add more suitable way of the handling CANCELLED scenario
            showToast(R.string.error_google_sign_in)
        }
    }

    private fun setListeners() {
        with(binding) {
            arrowBackIv bindClicksTo viewModel::onBackClicked
            recipientEmailEt bindTextChangesTo viewModel::onEmailChanged
            signInBtn bindClicksTo viewModel::onSignInClicked
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
        tryToSignOut {
            viewModel.handleSignIn {
                runCatchingWithCancellation {
                    credentialManager.getCredential(requireActivity(), credentialRequest)
                }
                    .onSuccess {
                        finishAuthorizationFlow()
                    }
                    .onFailure {
                        showToast(
                            getString(
                                R.string.error_get_credentials_template,
                                it.message
                            )
                        )
                    }
            }
        }
    }

    private fun finishAuthorizationFlow() {
        authorizationResult.addOnSuccessListener { authorizationResult ->
            val pendingIntent: PendingIntent? =
                authorizationResult.pendingIntent
            try {
                pendingIntent?.intentSender?.let {
                    sigInLauncher.launch(
                        IntentSenderRequest.Builder(it).build()
                    )
                }
            } catch (e: SendIntentException) {
                showToast(
                    getString(
                        R.string.error_authorization_template,
                        e.message
                    )
                )
            }
        }
            .addOnFailureListener {
                showToast(
                    getString(
                        R.string.error_authorization_template,
                        it.message
                    )
                )
            }
    }

    private fun tryToSignOut(block: () -> Unit) {
        Identity.getSignInClient(requireActivity())
            .signOut()
            .addOnCompleteListener {
                block()
            }
            .addOnFailureListener { e: Exception? ->
                showToast(R.string.error_google_sign_out)
            }
    }

    companion object {
        fun newInstance(id: Long): AddEmailDetailsFragment =
            AddEmailDetailsFragment().apply {
                arguments = bundleOf(ID_KEY to id)
            }

        private const val ID_KEY = "ID_KEY"
    }
}
