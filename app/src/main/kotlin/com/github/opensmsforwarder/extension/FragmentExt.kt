package com.github.opensmsforwarder.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.showToast(@StringRes messageResId: Int) {
    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_LONG).show()
}
