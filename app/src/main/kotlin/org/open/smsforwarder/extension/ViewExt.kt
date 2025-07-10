package org.open.smsforwarder.extension

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.graphics.toColorInt
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

@SuppressLint("ClickableViewAccessibility")
fun View.showTooltip(
    @StringRes messageResId: Int,
    textColor: Int = Color.WHITE,
    backgroundColor: Int = "#7E49FF".toColorInt(),
    cornerRadiusValue: Float = 24f,
    leftPadding: Int = 20,
    topPadding: Int = 10,
    rightPadding: Int = 20,
    bottomPadding: Int = 10
) {
    val context = this.context

    val background = GradientDrawable().apply {
        setColor(backgroundColor)
        cornerRadius = cornerRadiusValue
    }

    val tooltipText = TextView(context).apply {
        text = context.getString(messageResId)
        setTextColor(textColor)
        setBackground(background)
        setPadding(
            leftPadding,
            topPadding,
            rightPadding,
            bottomPadding
        )
    }

    var popupWindow: PopupWindow? = PopupWindow(
        tooltipText,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        false
    ).apply {
        isOutsideTouchable = true
        isClippingEnabled = true
    }

    val location = IntArray(2)
    this.getLocationOnScreen(location)

    popupWindow?.showAtLocation(
        this,
        Gravity.NO_GRAVITY,
        location[0],
        location[1] + this.height
    )

    this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View) {
            popupWindow?.takeIf { it.isShowing }?.dismiss()
        }

        override fun onViewAttachedToWindow(v: View) = Unit
    })

    popupWindow?.contentView?.setOnTouchListener { _, _ ->
        popupWindow?.takeIf { it.isShowing }?.dismiss()
        true
    }

    popupWindow?.setOnDismissListener {
        popupWindow = null
    }
}
