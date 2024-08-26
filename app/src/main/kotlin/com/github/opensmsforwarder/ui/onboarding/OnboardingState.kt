package com.github.opensmsforwarder.ui.onboarding

import androidx.annotation.StringRes
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.extension.unsafeLazy
import com.github.opensmsforwarder.model.OnboardingPagerSlide

data class OnboardingState(
    val isBackButtonVisible: Boolean = false,
    val isLastSlide: Boolean = false,
    @StringRes val nextButtonRes: Int = R.string.onboarding_fragment_next,
) {
    companion object {
        val slides by unsafeLazy {
            listOf(
                OnboardingPagerSlide(
                    titleId = R.string.app_name,
                    subtitleId = R.string.sms_forwarding_steps_description,
                    imageId = R.drawable.steps_info,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_first_step_label,
                    subtitleId = R.string.onboarding_first_step_description,
                    imageId = R.drawable.step_1,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_second_step_label,
                    subtitleId = R.string.onboarding_second_step_description,
                    imageId = R.drawable.step_2,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_third_step_label,
                    subtitleId = R.string.onboarding_third_step_description,
                    imageId = R.drawable.step_3,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.scammer_warning_label,
                    subtitleId = R.string.scammer_warning_message,
                    imageId = R.drawable.ic_warning,
                )
            )
        }
    }
}
