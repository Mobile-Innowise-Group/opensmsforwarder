package org.open.smsforwarder.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

/**
 * AutoCancel previous launched coroutine's Job with specified name if a new one was launched again.
 * For collect() functions mostly, as they are suspend at the call site and job is not released
 * until viewmodelScope is cleared.
 */
fun ViewModel.launchAndCancelPrevious(
    coroutineName: String = "launchCancelable",
    block: suspend CoroutineScope.() -> Unit
): Job {
    val childJobs = viewModelScope.coroutineContext.job.children
    val previousJob = childJobs.find { job ->
        (job as CoroutineScope).coroutineContext[CoroutineName]?.name == coroutineName
    }
    previousJob?.cancel()

    return viewModelScope.launch(CoroutineName(coroutineName)) { block() }
}
