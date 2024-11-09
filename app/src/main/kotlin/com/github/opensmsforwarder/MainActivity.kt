package com.github.opensmsforwarder

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.opensmsforwarder.databinding.ActivityMainBinding
import com.github.opensmsforwarder.extension.playSplashAnimation
import com.github.opensmsforwarder.navigation.AnimatedAppNavigator
import com.github.terrakok.cicerone.NavigatorHolder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val navigator = AnimatedAppNavigator(this, R.id.container)
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setOnExitAnimationListener { splashScreenProvider ->
            splashScreenProvider.playSplashAnimation(
                onAnimationEnd = {
                    viewModel.onInit(binding.container.childCount)
                }
            )
        }
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
