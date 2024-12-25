package com.github.opensmsforwarder.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.withCreationCallback

fun Fragment.showToast(@StringRes messageResId: Int) {
    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_LONG).show()
}

inline fun <reified VM : ViewModel, VMF> Fragment.assistedViewModels(
    crossinline block: (VMF) -> VM
): Lazy<VM> =
    viewModels<VM>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<VMF> { factory ->
                block(factory)
            }
        }
    )
