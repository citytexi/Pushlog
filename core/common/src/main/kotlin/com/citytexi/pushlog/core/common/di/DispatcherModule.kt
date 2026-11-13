package com.citytexi.pushlog.core.common.di

import com.citytexi.pushlog.core.common.Dispatcher
import com.citytexi.pushlog.core.common.PushlogDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Binds the [Dispatcher] qualifiers. It lives next to them in `core:common` (not in `data`), so
 * any module that injects a dispatcher, such as `core:datastore`, works without `data`.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    @Dispatcher(PushlogDispatcher.IO)
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(PushlogDispatcher.Default)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
