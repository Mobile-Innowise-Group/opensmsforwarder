package org.open.smsforwarder

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.terrakok.cicerone.NavigatorHolder
import dagger.hilt.android.AndroidEntryPoint
import org.open.smsforwarder.databinding.ActivityMainBinding
import org.open.smsforwarder.extension.playCustomSplashAnimation
import org.open.smsforwarder.navigation.AnimatedAppNavigator
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
        setUpSplashScreen()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setUpSplashScreen() {
        installSplashScreen().setOnExitAnimationListener { splashScreenProvider ->
            splashScreenProvider.playCustomSplashAnimation(
                onAnimationEnded = {
                    viewModel.onInit(binding.container.childCount)
                }
            )
        }
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
