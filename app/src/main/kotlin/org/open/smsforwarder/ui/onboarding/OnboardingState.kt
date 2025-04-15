package org.open.smsforwarder.ui.onboarding

import androidx.annotation.StringRes
import org.open.smsforwarder.R
import org.open.smsforwarder.extension.unsafeLazy
import org.open.smsforwarder.ui.onboarding.adapter.OnboardingPagerSlide

data class OnboardingState(
    val currentStep: Int = 1,
    val isLastSlide: Boolean = false,
    @StringRes val nextButtonRes: Int = R.string.onboarding_fragment_next,
) {

    fun isLastItem(itemPosition: Int) = itemPosition == slides.size - 1

    companion object {
        val slides: List<OnboardingPagerSlide> by unsafeLazy {
            listOf(
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_step_1_heading,
                    subtitleId = R.string.onboarding_step_1_description,
                    imageId = R.drawable.logo,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_step_2_heading,
                    subtitleId = R.string.onboarding_step_2_description,
                    imageId = R.drawable.onboarding_step_2,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_step_3_heading,
                    subtitleId = R.string.onboarding_step_3_description,
                    imageId = R.drawable.onboarding_step_3,
                ),
                OnboardingPagerSlide(
                    titleId = R.string.onboarding_step_4_heading,
                    subtitleId = R.string.onboarding_step_4_description,
                    imageId = R.drawable.onboarding_step_4,
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
