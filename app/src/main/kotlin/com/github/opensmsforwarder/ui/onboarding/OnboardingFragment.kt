package com.github.opensmsforwarder.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.FragmentOnboardingBinding
import com.github.opensmsforwarder.extension.bindClicksTo
import com.github.opensmsforwarder.extension.bindPageChangesTo
import com.github.opensmsforwarder.extension.observeWithLifecycle
import com.github.opensmsforwarder.extension.showOkDialog
import com.github.opensmsforwarder.extension.unsafeLazy
import com.github.opensmsforwarder.ui.onboarding.OnboardingState.Companion.slides
import com.github.opensmsforwarder.ui.onboarding.adapter.OnboardingSliderAdapter
import com.github.opensmsforwarder.utils.ButtonFillAnimator
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val binding by viewBinding(FragmentOnboardingBinding::bind)
    private val viewModel: OnboardingViewModel by viewModels()
    private val adapter by unsafeLazy { OnboardingSliderAdapter() }

    private var buttonFillAnimator: ButtonFillAnimator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupClickListeners()
        setupObservers()
    }

    private fun setupView() {
        with(binding) {
            adapter.setData(slides)
            onboardingVp.adapter = adapter
            TabLayoutMediator(dotsIndicator, onboardingVp) { _, _ -> }.attach()
            buttonFillAnimator = ButtonFillAnimator(
                button = nextBtn,
                lifecycle = viewLifecycleOwner.lifecycle,
                onAnimationStart = { acknowledgeChBx.isEnabled = false },
                onAnimationEnd = { acknowledgeChBx.isEnabled = true }
            )
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            nextBtn bindClicksTo {
                viewModel.onNextButtonClicked(
                    onboardingVp.currentItem,
                    acknowledgeChBx.isChecked
                )
            }
            backBtn bindClicksTo viewModel::onPreviousButtonClicked
            skipAllBtn bindClicksTo viewModel::onSkipAllButtonClicked
            onboardingVp bindPageChangesTo viewModel::onSlidePage
        }
    }

    private fun setupObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner, action = ::renderState)
        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner, action = ::handleEffect)
    }

    private fun renderState(onboardingState: OnboardingState) {
        with(binding) {
            backBtn.isVisible = onboardingState.currentStep >= 2
            stepLabel.text = getString(R.string.onboarding_step_label, onboardingState.currentStep)
            skipAllBtn.isVisible = !onboardingState.isLastSlide
            nextBtn.text = getString(onboardingState.nextButtonRes)
            acknowledgeChBx.isVisible = onboardingState.isLastSlide
            buttonFillAnimator?.startAnimationWithPrecondition(onboardingState::isLastSlide)
        }
    }

    private fun handleEffect(effect: OnboardingEffect) {
        when (effect) {
            is WarningEffect -> {
                requireActivity().showOkDialog(
                    title = getString(R.string.privacy_info_heading),
                    message = getString(R.string.acknowledge_risks_message),
                    dialogStyle = R.style.SmsAlertDialog,
                )
            }

            NextPageEffect -> binding.onboardingVp.currentItem++
            PreviousPageEffect -> binding.onboardingVp.currentItem--
            SkipAllEffect -> binding.onboardingVp.currentItem = adapter.itemCount - 1
        }
    }
}
