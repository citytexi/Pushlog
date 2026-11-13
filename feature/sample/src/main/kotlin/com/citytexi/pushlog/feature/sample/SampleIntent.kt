package com.citytexi.pushlog.feature.sample

import com.citytexi.pushlog.core.ui.mvi.UiIntent

sealed interface SampleIntent : UiIntent {
    data object Refresh : SampleIntent
}
