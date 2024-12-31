package org.open.smsforwarder.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

sealed class Resources {

    class DynamicString(val value: String) : Resources(), StringProvider {
        override fun asString(context: Context): String = value
    }

    class StringResource(@StringRes val resId: Int) : Resources(), StringProvider {
        override fun asString(context: Context): String = context.getString(resId)
    }

    @Suppress("SpreadOperator")
    class VarArgStringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : Resources(), StringProvider {
        override fun asString(context: Context): String = context.getString(resId, *args)
    }

    class DrawableResource(@DrawableRes val resId: Int) : Resources(), DrawableProvider {
        override fun asDrawable(context: Context): Drawable? =
            ContextCompat.getDrawable(context, resId)
    }

    class ColorResource(@ColorRes val resId: Int) : Resources(), ColorProvider {
        override fun asColor(context: Context) = ContextCompat.getColor(context, resId)
    }

    interface StringProvider {
        fun asString(context: Context): String
    }

    interface DrawableProvider {
        fun asDrawable(context: Context): Drawable?
    }

    interface ColorProvider {
        fun asColor(context: Context): Int
    }
}
