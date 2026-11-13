package com.citytexi.pushlog.feature.sample.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.navigation.SampleKey
import com.citytexi.pushlog.feature.sample.SampleRoute

/** Registers the sample screen for [SampleKey]. */
fun EntryProviderScope<NavKey>.sampleEntry() {
    entry<SampleKey> { SampleRoute() }
}
