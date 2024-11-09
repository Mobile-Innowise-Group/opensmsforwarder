package com.github.opensmsforwarder.navigation

import com.github.opensmsforwarder.ui.home.HomeFragment
import com.github.opensmsforwarder.ui.onboarding.OnboardingFragment
import com.github.opensmsforwarder.ui.steps.addrecipient.AddRecipientFragment
import com.github.opensmsforwarder.ui.steps.addrule.AddForwardingRuleFragment
import com.github.opensmsforwarder.ui.steps.choosemethod.ChooseForwardingMethodFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {
    fun homeFragment() = FragmentScreen { HomeFragment() }
    fun chooseForwardingMethodFragment() = FragmentScreen { ChooseForwardingMethodFragment() }
    fun addRecipientFragment() = FragmentScreen { AddRecipientFragment() }
    fun addForwardingRuleFragment() = FragmentScreen { AddForwardingRuleFragment() }
    fun onboardingFragment() = FragmentScreen { OnboardingFragment() }
}
