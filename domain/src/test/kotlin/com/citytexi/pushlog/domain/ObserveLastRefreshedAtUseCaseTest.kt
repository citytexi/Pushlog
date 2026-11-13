package com.citytexi.pushlog.domain

import com.citytexi.pushlog.domain.testing.FakeSampleRepository
import com.citytexi.pushlog.domain.usecase.ObserveLastRefreshedAtUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveLastRefreshedAtUseCaseTest {
    @Test
    fun `invoke emits repository last refreshed time`() =
        runTest {
            val repository = FakeSampleRepository(lastRefreshedAt = 42L)

            assertEquals(42L, ObserveLastRefreshedAtUseCase(repository)().first())
        }
}
