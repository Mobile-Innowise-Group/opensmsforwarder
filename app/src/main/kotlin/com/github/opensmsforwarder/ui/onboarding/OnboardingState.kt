package com.github.opensmsforwarder.ui.onboarding

import androidx.annotation.StringRes
import com.github.opensmsforwarder.R
import com.github.opensmsforwarder.extension.unsafeLazy
import com.github.opensmsforwarder.model.OnboardingPagerSlide

data class OnboardingState(
    val isBackButtonVisible: Boolean = false,
    val isLastSlide: Boolean = false,
    @StringRes val nextButtonRes: Int = R.string.onboarding_fragment_next,
    val slidePosition: Int = 1
) {
    companion object {
        val slides by unsafeLazy {
            listOf(
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_first_step_heading,
                    subtitleId = R.string.onboarding_first_step_description,
                    imageId = R.drawable.step_1,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_second_step_heading,
                    subtitleId = R.string.onboarding_second_step_description,
                    imageId = R.drawable.step_2,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_third_step_heading,
                    subtitleId = R.string.onboarding_third_step_description,
                    imageId = R.drawable.step_3,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.privacy_info_heading,
                    subtitleId = R.string.privacy_info_description,
                    imageId = R.drawable.privacy_info_image,
                )
            )
        }
    }
}
