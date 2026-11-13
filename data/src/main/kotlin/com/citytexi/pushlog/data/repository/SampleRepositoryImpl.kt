package com.citytexi.pushlog.data.repository

import androidx.room.withTransaction
import com.citytexi.pushlog.core.common.Dispatcher
import com.citytexi.pushlog.core.common.PushlogDispatcher
import com.citytexi.pushlog.core.database.PushlogDatabase
import com.citytexi.pushlog.core.database.dao.SampleItemDao
import com.citytexi.pushlog.core.database.model.SampleItemEntity
import com.citytexi.pushlog.core.datastore.SettingsDataSource
import com.citytexi.pushlog.core.network.SampleApi
import com.citytexi.pushlog.domain.model.SampleItem
import com.citytexi.pushlog.domain.repository.SampleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Offline-first repository: the database is the source of truth, [refresh] pulls from the network. */
class SampleRepositoryImpl(
    private val database: PushlogDatabase,
    private val api: SampleApi,
    private val dao: SampleItemDao,
    private val settings: SettingsDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val clock: () -> Long,
) : SampleRepository {
    // Hilt cannot inject the function-typed `clock`, so Hilt uses this constructor.
    @Inject
    constructor(
        database: PushlogDatabase,
        api: SampleApi,
        dao: SampleItemDao,
        settings: SettingsDataSource,
        @Dispatcher(PushlogDispatcher.IO) ioDispatcher: CoroutineDispatcher,
    ) : this(database, api, dao, settings, ioDispatcher, System::currentTimeMillis)

    override fun observeItems(): Flow<List<SampleItem>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeLastRefreshedAt(): Flow<Long?> = settings.lastRefreshedAt

    // Retrofit suspend calls, Room and DataStore are already main-safe. withContext(ioDispatcher)
    // is kept on purpose: the DTO-to-entity mapping and any blocking work added later stay off the
    // main thread, and tests swap the dispatcher for a test one.
    // Throws on failure (HttpStatusException, IOException, ...): RefreshSampleItemsUseCase wraps it in Result.
    override suspend fun refresh() =
        withContext(ioDispatcher) {
            val entities = mapHttpErrors { api.getItems() }.map { SampleItemEntity(it.id, it.title) }
            // One transaction: a failed write rolls back, so the cache is never left empty.
            database.withTransaction {
                dao.clear()
                dao.upsertAll(entities)
            }
            // Written only after the transaction commits, so a failed write never records a
            // refresh time. If this write itself fails, the items are already saved and refresh
            // throws; the next refresh records the time.
            settings.setLastRefreshedAt(clock())
        }
}

private fun SampleItemEntity.toDomain() = SampleItem(id = id, title = title)
