package com.citytexi.pushlog.feature.sample

import com.citytexi.pushlog.core.ui.mvi.UiSideEffect

sealed interface SampleSideEffect : UiSideEffect {
    /** A refresh failed. The Route shows it in a snackbar; the list stays as it was. */
    data class ShowError(
        val error: SampleError,
    ) : SampleSideEffect
}
