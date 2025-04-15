package org.open.smsforwarder.extension

import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.isVisible

fun TextView.setTextIfChanged(newValue: String?) {
    val oldValue: String? = text?.toString()
    if (oldValue != newValue) {
        text = newValue
    }
}

fun EditText.setTextIfChangedKeepState(newValue: String) {
    val oldValue: String? = text?.toString()
    if (oldValue != newValue) {
        setTextKeepState(newValue)
    }
}

fun View.setVisibilityIfChanged(newValue: Boolean) {
    val oldValue: Boolean = isVisible
    if (oldValue != newValue) {
        isVisible = newValue
    }
}

fun RadioButton.setValueIfChanged(newValue: Boolean) {
    val oldValue: Boolean = isChecked
    if (oldValue != newValue) {
        isChecked = newValue
    }
}

fun View.setAccessibilityFocus() {
    this.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
    this.requestFocus()
}
