package com.citytexi.pushlog.domain.repository

import com.citytexi.pushlog.domain.model.SampleItem
import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    fun observeItems(): Flow<List<SampleItem>>

    fun observeLastRefreshedAt(): Flow<Long?>

    /** Pulls items from the network into the cache. Throws on failure; `RefreshSampleItemsUseCase` wraps it. */
    suspend fun refresh()
}
