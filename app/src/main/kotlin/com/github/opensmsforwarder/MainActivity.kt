package com.github.opensmsforwarder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.opensmsforwarder.databinding.ActivityMainBinding
import com.github.opensmsforwarder.navigation.AnimatedAppNavigator
import com.github.opensmsforwarder.navigation.Screens.splashFragment
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Replace
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val navigator = AnimatedAppNavigator(this, R.id.container)

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToHomeScreenIfNeed()
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

    private fun navigateToHomeScreenIfNeed() {
        if (isContainerEmpty()) {
            navigator.applyCommands(arrayOf(Replace(splashFragment())))
        }
    }

    private fun isContainerEmpty(): Boolean = binding.container.childCount == 0
}
