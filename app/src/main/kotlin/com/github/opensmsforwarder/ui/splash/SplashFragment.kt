package com.github.opensmsforwarder.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.opensmsforwarder.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment:  Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onScreenInit()
    }
}
