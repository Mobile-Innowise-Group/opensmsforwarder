package com.github.opensmsforwarder.extension

import android.view.View
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.google.android.material.radiobutton.MaterialRadioButton

inline infix fun MaterialRadioButton.bindCheckChangesTo(crossinline block: () -> Unit) {
    setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            block()
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
