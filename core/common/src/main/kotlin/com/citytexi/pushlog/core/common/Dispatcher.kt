package com.citytexi.pushlog.core.common

import javax.inject.Qualifier

/** Qualifier selecting a [PushlogDispatcher]. Bindings live in [com.citytexi.pushlog.core.common.di.DispatcherModule]. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(
    val value: PushlogDispatcher,
)
