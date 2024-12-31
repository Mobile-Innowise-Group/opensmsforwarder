package org.open.smsforwarder.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.open.smsforwarder.R

class ButtonFillAnimator(
    private val button: TextView,
    lifecycle: Lifecycle,
    private val onAnimationStart: () -> Unit,
    private val onAnimationEnd: () -> Unit,
    private val animationDuration: Long = DEFAULT_ANIMATION_DURATION,
    private val animationStart: Int = DEFAULT_ANIMATION_START,
    private val animationEnd: Int = DEFAULT_ANIMATION_END
) : DefaultLifecycleObserver {

    private var fillAnimator: ValueAnimator? = null

    init {
        lifecycle.addObserver(this)
    }

    private fun createAnimator(clipDrawable: ClipDrawable): ValueAnimator {
        return ValueAnimator.ofInt(animationStart, animationEnd).apply {
            duration = animationDuration
            addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Int
                clipDrawable.level = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    button.isEnabled = true
                    onAnimationEnd.invoke()
                }
            })
        }
    }

    fun startAnimationWithPrecondition(precondition: () -> Boolean) {
        if (precondition()) startAnimation() else stopAnimation()
    }

    private fun startAnimation() {
        fillAnimator?.cancel()
        button.isEnabled = false
        onAnimationStart.invoke()
        button.setBackgroundResource(R.drawable.animate_button_background)

        val background = button.background as LayerDrawable
        val clipDrawable = background.getDrawable(1) as ClipDrawable
        clipDrawable.level = 0

        fillAnimator = createAnimator(clipDrawable).apply {
            start()
        }
    }

    private fun stopAnimation() {
        button.setBackgroundResource(R.drawable.rounded_corners_background)
        fillAnimator?.cancel()
        fillAnimator = null
    }

    override fun onDestroy(owner: LifecycleOwner) {
        stopAnimation()
    }

    private companion object {
        const val DEFAULT_ANIMATION_DURATION = 7000L
        const val DEFAULT_ANIMATION_START = 0
        const val DEFAULT_ANIMATION_END = 10000
    }
}
