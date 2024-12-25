package com.github.opensmsforwarder.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

inline fun <reified T> Flow<T>.observeWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: (T) -> Unit,
): Job = lifecycleOwner.lifecycleScope.launch {
    flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState).collect(action)
}

inline fun <T> MutableStateFlow<T>.asStateFlowWithInitialAction(
    scope: CoroutineScope,
    stopTimeoutMillis: Long = 5000,
    crossinline initAction: () -> Unit,
): StateFlow<T> =
    onStart { initAction() }
        .stateIn(
            scope,
            SharingStarted.WhileSubscribed(stopTimeoutMillis),
            value
        )
