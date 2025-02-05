package org.open.smsforwarder.analytics

object AnalyticsEvents {

    const val ONBOARDING_SCREEN_START = "onboarding_start"
    const val HOME_SCREEN_START = "home_screen_direct_navigation"
    const val ONBOARDING_COMPLETE = "onboarding_complete"
    const val ONBOARDING_WARNING_SHOW = "onboarding_checkbox_warning_dialog_shown"
    const val FORWARDING_CREATION_CLICKED = "recipient_creation_clicked"
    const val RECIPIENT_CREATION_STEP2_NEXT_CLICKED = "recipient_creation_step2_next_clicked"
    const val RECIPIENT_CREATION_STEP1_NEXT_CLICKED = "recipient_creation_step1_next_clicked"
    const val RULE_ADD_CLICKED = "rule_add_clicked"
    const val RECIPIENT_CREATION_FINISHED = "recipient_creation_finished"
    const val BATTERY_OPTIMIZATION_DIALOG_NAVIGATED = "battery_optimization_dialog_navigated"
    const val BATTERY_OPTIMIZATION_DIALOG_GO_TO_SETTINGS_CLICKED = "battery_optimization_dialog_go_to_settings_clicked"
    const val FIRST_RECIPIENT_CREATION_DIALOG_GO_TO_SETTINGS_CLICKED = "first_recipient_creation_go_to_settings_clicked"
    const val REQUEST_PERMISSIONS = "request_permissions"
    const val PERMISSIONS_RATIONALE_DIALOG_NAVIGATED = "permissions_rationale_dialog_navigated"
    const val PERMISSIONS_DIALOG_NAVIGATED = "permissions_dialog_navigated"
    const val PERMISSIONS_DIALOG_GO_TO_SETTINGS_CLICKED = "permissions_dialog_go_to_settings_clicked"
}
