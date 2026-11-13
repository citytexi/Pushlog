package com.citytexi.pushlog.domain.usecase

import com.citytexi.pushlog.domain.model.SampleItem
import com.citytexi.pushlog.domain.repository.SampleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSampleItemsUseCase
    @Inject
    constructor(
        private val repository: SampleRepository,
    ) {
        operator fun invoke(): Flow<List<SampleItem>> = repository.observeItems()
    }
