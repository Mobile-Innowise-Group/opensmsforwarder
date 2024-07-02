package com.github.opensmsforwarder.ui.home

sealed interface HomeEffect

data class DeleteEffect(val id: Long): HomeEffect

data object BatteryWarningEffect: HomeEffect
