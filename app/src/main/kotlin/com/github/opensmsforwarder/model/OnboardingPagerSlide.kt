package com.github.opensmsforwarder.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingPagerSlide(
    @StringRes val titleId: Int,
    @StringRes val subtitleId: Int,
    @DrawableRes val imageId: Int,
    val isLastSlide: Boolean = false,
    var isChecked: Boolean = false
)
