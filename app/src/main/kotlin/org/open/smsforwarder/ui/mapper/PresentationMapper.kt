package org.open.smsforwarder.ui.mapper

import org.open.smsforwarder.domain.model.Forwarding
import org.open.smsforwarder.domain.model.Rule
import org.open.smsforwarder.ui.home.HomeState
import org.open.smsforwarder.ui.model.ForwardingUI
import org.open.smsforwarder.ui.steps.addrecipientdetails.addemaildetails.AddEmailDetailsState
import org.open.smsforwarder.ui.steps.addrecipientdetails.addphonedetails.AddPhoneDetailsState
import org.open.smsforwarder.ui.steps.choosemethod.ChooseForwardingMethodState

fun Forwarding.toEmailDetailsPresentation(): AddEmailDetailsState {
    val signInTvVisible = isEmailForwardingType && senderEmail.isNullOrEmpty()
    val senderEmailVisible = !senderEmail.isNullOrEmpty() && isEmailForwardingType
    val sigInBtnVisible = senderEmail.isNullOrEmpty() && isEmailForwardingType
    val signOutBtnVisible = !senderEmail.isNullOrEmpty() && isEmailForwardingType
    return AddEmailDetailsState(
        id = id,
        title = title,
        forwardingType = forwardingType,
        signInTvVisible = signInTvVisible,
        senderEmailVisible = senderEmailVisible,
        senderEmail = senderEmail,
        recipientEmail = recipientEmail,
        sigInBtnVisible = sigInBtnVisible,
        signOutBtnVisible = signOutBtnVisible
    )
}

fun Forwarding.toPhoneDetailsPresentation(): AddPhoneDetailsState =
    AddPhoneDetailsState(
        id = id,
        title = title,
        forwardingType = forwardingType,
        recipientPhone = recipientPhone
    )

fun Forwarding.toChooseForwardingMethodPresentation(): ChooseForwardingMethodState =
    ChooseForwardingMethodState(
        id = id,
        title = title,
        forwardingType = forwardingType,
    )

fun List<Forwarding>.mergeWithRules(rules: List<Rule>): HomeState {
    val forwardingUI = mutableListOf<ForwardingUI>()
    forEach { forwarding ->
        forwardingUI.add(
            ForwardingUI(
                id = forwarding.id,
                title = forwarding.title,
                forwardingType = forwarding.forwardingType,
                senderEmail = forwarding.senderEmail,
                recipientPhone = forwarding.recipientPhone,
                recipientEmail = forwarding.recipientEmail,
                error = forwarding.error,
                atLeastOneRuleAdded = rules.any { forwarding.id == it.forwardingId }
            )
        )
    }
    return HomeState(forwardingUI)
}

fun AddPhoneDetailsState.toDomain() =
    Forwarding(
        id = id,
        title = title,
        forwardingType = forwardingType,
        recipientPhone = recipientPhone
    )

fun AddEmailDetailsState.toDomain() =
    Forwarding(
        id = id,
        title = title,
        forwardingType = forwardingType,
        senderEmail = senderEmail,
        recipientEmail = recipientEmail
    )

fun ChooseForwardingMethodState.toDomain() =
    Forwarding(
        id = id,
        title = title.trim(),
        forwardingType = forwardingType,
    )
