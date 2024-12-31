package org.open.smsforwarder.ui.onboarding.adapter

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingPagerSlide(
    @StringRes val titleId: Int,
    @StringRes val subtitleId: Int,
    @DrawableRes val imageId: Int,
)
