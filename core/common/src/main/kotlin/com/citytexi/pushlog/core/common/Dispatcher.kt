package com.citytexi.pushlog.core.common

import javax.inject.Qualifier

/** Coroutine dispatchers that can be injected through [Dispatcher]. */
enum class PushlogDispatcher { IO, Default }

/** Qualifier selecting a [PushlogDispatcher]. Bindings live in [com.citytexi.pushlog.core.common.di.DispatcherModule]. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(
    val value: PushlogDispatcher,
)
