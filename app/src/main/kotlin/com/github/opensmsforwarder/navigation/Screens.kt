package com.github.opensmsforwarder.navigation

import com.github.opensmsforwarder.ui.home.HomeFragment
import com.github.opensmsforwarder.ui.onboarding.OnboardingFragment
import com.github.opensmsforwarder.ui.steps.addrecipientdetails.addemaildetails.AddEmailDetailsFragment
import com.github.opensmsforwarder.ui.steps.addrecipientdetails.addphonedetails.AddPhoneDetailsFragment
import com.github.opensmsforwarder.ui.steps.addrule.AddForwardingRuleFragment
import com.github.opensmsforwarder.ui.steps.choosemethod.ChooseForwardingMethodFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {
    fun homeFragment() = FragmentScreen { HomeFragment() }

    fun chooseForwardingMethodFragment(id: Long) =
        FragmentScreen { ChooseForwardingMethodFragment.newInstance(id) }

    fun addPhoneDetailsFragment(id: Long) =
        FragmentScreen { AddPhoneDetailsFragment.newInstance(id) }

    fun addEmailDetailsFragment(id: Long) =
        FragmentScreen { AddEmailDetailsFragment.newInstance(id) }

    fun addForwardingRuleFragment(id: Long) =
        FragmentScreen { AddForwardingRuleFragment.newInstance(id) }

    fun onboardingFragment() = FragmentScreen { OnboardingFragment() }
}
