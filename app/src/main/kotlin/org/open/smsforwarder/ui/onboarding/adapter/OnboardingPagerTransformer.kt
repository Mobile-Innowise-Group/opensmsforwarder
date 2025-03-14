package org.open.smsforwarder.ui.onboarding.adapter

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textview.MaterialTextView
import org.open.smsforwarder.R

private const val TITLE_PARALLAX_FACTOR = 0.45f
private const val SUBTITLE_PARALLAX_FACTOR = 2.5f

class OnboardingPagerTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        if (position >= -1 && position <= 1) {
            page.findViewById<MaterialTextView>(R.id.text_title).translationX =
                position * page.width / TITLE_PARALLAX_FACTOR
            page.findViewById<MaterialTextView>(R.id.text_subtitle).translationX =
                position * page.width / SUBTITLE_PARALLAX_FACTOR
        }
    }
}
