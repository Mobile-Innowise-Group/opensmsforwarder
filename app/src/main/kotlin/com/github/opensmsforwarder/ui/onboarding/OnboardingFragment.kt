package com.github.opensmsforwarder.ui.onboarding

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.databinding.FragmentOnboardingBinding
import com.github.opensmsforwarder.extension.applyFillAnimation
import com.github.opensmsforwarder.extension.observeWithLifecycle
import com.github.opensmsforwarder.extension.unsafeLazy
import com.github.opensmsforwarder.ui.dialog.warning.WarningDialog
import com.github.opensmsforwarder.ui.onboarding.OnboardingState.Companion.slides
import com.github.opensmsforwarder.ui.onboarding.adapter.OnboardingSliderAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val binding by viewBinding(FragmentOnboardingBinding::bind)
    private val viewModel: OnboardingViewModel by viewModels()
    private val adapter by unsafeLazy { OnboardingSliderAdapter() }

    private var fillAnimation: ValueAnimator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupClickListeners()
        setupObservers()
    }

    private fun setupView() {
        adapter.setData(slides)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.dotsIndicator, binding.viewPager) { _, _ -> }.attach()
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.slidePage(position, adapter.itemCount)
            }
        })
    }

    private fun setupObservers() {
        viewModel.viewState.observeWithLifecycle(viewLifecycleOwner) { uiState ->
            renderState(uiState)
        }

        viewModel.viewEffect.observeWithLifecycle(viewLifecycleOwner) { effect ->
            handleEffect(effect)
        }
    }

    private fun startButtonFillAnimation() {
        binding.buttonNext.isEnabled = false
        adapter.setCheckboxClickable(false)
        binding.pbCountdown.setBackgroundDrawableColor(
            getColor(
                requireContext(),
                R.color.stroke_light
            )
        )
        fillAnimation = ValueAnimator.ofFloat(0f, 100f).applyFillAnimation(
            durationMillis = 3000L,
            onUpdate = { animatedValue ->
                binding.pbCountdown.setProgressPercentage(animatedValue.toDouble(), false)
            },
            onEnd = {
                binding.buttonNext.isEnabled = true
                adapter.setCheckboxClickable(true)
            }
        )
    }

    private fun stopButtonFillAnimation() {
        binding.pbCountdown.setBackgroundDrawableColor(
            getColor(
                requireContext(),
                R.color.blue_light_theme
            )
        )
        fillAnimation?.cancel()
        fillAnimation = null
    }

    private fun renderState(onboardingState: OnboardingState) {
        binding.buttonBack.isVisible = onboardingState.isBackButtonVisible
        binding.buttonNext.text = getString(onboardingState.nextButtonRes)
        if (onboardingState.isLastSlide) startButtonFillAnimation() else stopButtonFillAnimation()
    }

    private fun handleEffect(effect: OnboardingEffect) {
        when (effect) {
            is WarningEffect -> {
                val dialog = WarningDialog.newInstance(
                    getString(R.string.scammer_warning_label),
                    getString(R.string.acknowledge_risks_message)
                )
                dialog.show(childFragmentManager, WarningDialog.TAG)
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            buttonNext.setOnClickListener { onNextButtonClick() }
            buttonBack.setOnClickListener { onBackButtonClick() }
        }
    }

    private fun onNextButtonClick() {
        if (binding.viewPager.currentItem + 1 == adapter.itemCount) {
            val currentSlide = adapter.getItem(binding.viewPager.currentItem)
            viewModel.finishOnboarding(currentSlide.isChecked)
        } else {
            binding.viewPager.currentItem++
        }
    }

    private fun onBackButtonClick() {
        binding.viewPager.currentItem--
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fillAnimation?.cancel()
        fillAnimation = null
    }
}
