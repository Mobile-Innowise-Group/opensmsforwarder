package org.open.smsforwarder.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import org.open.smsforwarder.ui.feedback.FeedbackFragment
import org.open.smsforwarder.ui.history.HistoryFragment
import org.open.smsforwarder.ui.home.HomeFragment
import org.open.smsforwarder.ui.onboarding.OnboardingFragment
import org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails.AddEmailDetailsFragment
import org.open.smsforwarder.ui.steps.addrecipientdetails.addtelegramdetails.AddTelegramDetailsFragment
import org.open.smsforwarder.ui.steps.addrule.AddForwardingRuleFragment
import org.open.smsforwarder.ui.steps.choosemethod.ChooseForwardingMethodFragment

object Screens {
    fun homeFragment() = FragmentScreen { HomeFragment() }

    fun chooseForwardingMethodFragment(id: Long) =
        FragmentScreen { ChooseForwardingMethodFragment.newInstance(id) }

    fun addEmailDetailsFragment(id: Long) =
        FragmentScreen { AddEmailDetailsFragment.newInstance(id) }

    fun addTelegramDetailsFragment(id: Long) =
        FragmentScreen { AddTelegramDetailsFragment.newInstance(id) }

    fun addForwardingRuleFragment(id: Long) =
        FragmentScreen { AddForwardingRuleFragment.newInstance(id) }

    fun onboardingFragment() = FragmentScreen { OnboardingFragment() }

    fun historyFragment() = FragmentScreen { HistoryFragment() }

    fun feedbackFragment() = FragmentScreen { FeedbackFragment() }
}
