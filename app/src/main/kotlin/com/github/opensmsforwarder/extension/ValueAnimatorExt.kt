package com.github.opensmsforwarder.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

fun ValueAnimator.applyFillAnimation(
    durationMillis: Long,
    onUpdate: (Float) -> Unit,
    onEnd: () -> Unit
): ValueAnimator {
    this.duration = durationMillis
    interpolator = LinearInterpolator()
    addUpdateListener { animation -> onUpdate(animation.animatedValue as Float) }
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) = onEnd()
    })
    start()
    return this
}
