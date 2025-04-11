package org.open.smsforwarder.ui.onboarding

import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.R
import org.open.smsforwarder.databinding.FragmentOnboardingBinding
import org.open.smsforwarder.extension.bindClicksTo
import org.open.smsforwarder.extension.bindPageChangesTo
import org.open.smsforwarder.extension.observeWithLifecycle
import org.open.smsforwarder.extension.showOkDialog
import org.open.smsforwarder.extension.unsafeLazy
import org.open.smsforwarder.ui.onboarding.OnboardingState.Companion.slides
import org.open.smsforwarder.ui.onboarding.adapter.OnboardingPagerTransformer
import org.open.smsforwarder.ui.onboarding.adapter.OnboardingSliderAdapter
import org.open.smsforwarder.utils.ButtonFillAnimator

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

    override fun onResume() {
        super.onResume()
        setLabelFocus()
    }

    private fun setLabelFocus() {
        binding.stepLabel.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        binding.stepLabel.requestFocus()
    }

    private fun setupView() {
        with(binding) {
            adapter.setData(slides)
            onboardingVp.setPageTransformer(OnboardingPagerTransformer())
            onboardingVp.adapter = adapter
            dotsIndicator.tabIconTint = null
            TabLayoutMediator(dotsIndicator, onboardingVp) { tab, _ ->
                tab.setIcon(R.drawable.dots_selector)
            }.attach()
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
            stepLabel.contentDescription =
                getString(R.string.onboarding_title) + getString(R.string.comma) + stepLabel.text
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

            NextPageEffect -> {
                binding.onboardingVp.currentItem++
                setLabelFocus()
            }

            PreviousPageEffect -> {
                binding.onboardingVp.currentItem--
                setLabelFocus()
            }

            SkipAllEffect -> {
                binding.onboardingVp.currentItem = adapter.itemCount - 1
                setLabelFocus()
            }
        }
    }
}
