package org.open.smsforwarder.ui.feedback

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val router: Router,
) : ViewModel() {

    fun exit() = router.exit()
}
