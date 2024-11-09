package com.github.opensmsforwarder.extension

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnticipateInterpolator
import android.view.animation.ScaleAnimation
import androidx.core.splashscreen.SplashScreenViewProvider

fun SplashScreenViewProvider.playSplashAnimation(
    fromX: Float = 1f,
    toX: Float = 1.3f,
    fromY: Float = 1f,
    toY: Float = 1.3f,
    pivotValue: Float = 0.5f,
    interpolatorTension: Float = 5f,
    scaleAnimationDuration: Long = 1000,
    fadeoutAnimationDuration: Long = 500,
    defaultOffset: Long = 1000,
    onAnimationEnd: () -> Unit
) {

    val scaleAnimation: Animation = ScaleAnimation(
        fromX, toX,
        fromY, toY,
        Animation.RELATIVE_TO_SELF, pivotValue,
        Animation.RELATIVE_TO_SELF, pivotValue
    ).apply {
        interpolator = AnticipateInterpolator(interpolatorTension)
        repeatMode = Animation.REVERSE
        repeatCount = 1
        duration = scaleAnimationDuration
    }

    val fadeOutAnimation = AlphaAnimation(1f, 0f).apply {
        startOffset = defaultOffset
        duration = fadeoutAnimationDuration
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                this@playSplashAnimation.remove()
                onAnimationEnd.invoke()
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })
    }

    this.iconView.startAnimation(scaleAnimation)
    this.view.startAnimation(fadeOutAnimation)
}
