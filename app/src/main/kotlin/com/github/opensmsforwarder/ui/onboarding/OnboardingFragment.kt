package com.github.opensmsforwarder.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.FragmentOnboardingBinding
import com.github.opensmsforwarder.extension.bindClicksTo
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

    private lateinit var buttonFillAnimator: ButtonFillAnimator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupClickListeners()
        setupButtonAnimation()
        setupObservers()
    }

    private fun setupView() {
        adapter.setData(slides)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.dotsIndicator, binding.viewPager) { _, _ -> }.attach()
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.onSlidePage(position, adapter.itemCount)
            }
        })
    }

    private fun setupClickListeners() {
        with(binding) {
            buttonNext bindClicksTo ::onNextButtonClick
            buttonBack bindClicksTo ::onBackButtonClick
        }
    }

    private fun setupButtonAnimation() {
        buttonFillAnimator = ButtonFillAnimator(
            button = binding.buttonNext,
            lifecycle = viewLifecycleOwner.lifecycle,
            onAnimationStart = {
                adapter.setCheckboxClickable(false)
            },
            onAnimationEnd = {
                adapter.setCheckboxClickable(true)
            }
        )
    }

    private fun setupObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner) { uiState ->
            renderState(uiState)
        }

        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner) { effect ->
            handleEffect(effect)
        }
    }

    private fun renderState(onboardingState: OnboardingState) {
        binding.buttonBack.isVisible = onboardingState.isBackButtonVisible
        binding.buttonNext.text = getString(onboardingState.nextButtonRes)
        if (onboardingState.isLastSlide) buttonFillAnimator.startAnimation() else buttonFillAnimator.stopAnimation()
    }

    private fun handleEffect(effect: OnboardingEffect) {
        when (effect) {
            is WarningEffect -> {
                requireActivity().showOkDialog(
                    title = getString(R.string.scammer_warning_label),
                    message = getString(R.string.acknowledge_risks_message),
                    dialogStyle = R.style.SmsAlertDialog,
                )
            }
        }
    }

    private fun onNextButtonClick() {
        if (binding.viewPager.currentItem + 1 == adapter.itemCount) {
            val currentSlide = adapter.getItem(binding.viewPager.currentItem)
            viewModel.onFinishOnboarding(currentSlide.isChecked)
        } else {
            binding.viewPager.currentItem++
        }
    }

    private fun onBackButtonClick() {
        binding.viewPager.currentItem--
    }
}
