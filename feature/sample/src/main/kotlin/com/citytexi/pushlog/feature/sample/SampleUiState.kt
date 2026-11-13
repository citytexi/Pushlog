package com.citytexi.pushlog.feature.sample

import com.citytexi.pushlog.core.ui.mvi.UiState
import com.citytexi.pushlog.domain.model.SampleItem

data class SampleUiState(
    val items: List<SampleItem> = emptyList(),
    val lastRefreshedAt: Long? = null,
    val isRefreshing: Boolean = false,
) : UiState
