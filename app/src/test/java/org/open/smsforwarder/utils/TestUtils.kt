package org.open.smsforwarder.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <T> TestScope.awaitInitialAction(
    viewState: StateFlow<T>,
    block: () -> Unit
) {
    val collectionJob = launch {
        viewState.collect { }
    }
    advanceUntilIdle()
    block()
    collectionJob.cancel()
}
