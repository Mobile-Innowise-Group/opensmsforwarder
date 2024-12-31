package org.open.smsforwarder.extension

import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.radiobutton.MaterialRadioButton

inline infix fun MaterialRadioButton.bindCheckChangesTo(crossinline block: (CompoundButton) -> Unit) {
    setOnCheckedChangeListener { view, isChecked ->
        if (isChecked) {
            block(view)
        }
    }
}

inline infix fun EditText.bindTextChangesTo(crossinline block: (String) -> Unit) {
    doOnTextChanged { title, _, _, _ ->
        block(title.toString())
    }
}

inline infix fun View.bindClicksTo(crossinline block: () -> Unit) {
    setOnClickListener {
        block()
    }
}

inline infix fun ViewPager2.bindPageChangesTo(crossinline block: (Int) -> Unit) {

    registerOnPageChangeCallback(object : OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            block(position)
        }
    })
}
