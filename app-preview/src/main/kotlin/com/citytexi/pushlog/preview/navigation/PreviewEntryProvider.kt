package com.citytexi.pushlog.preview.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.citytexi.pushlog.preview.home.previewHomeEntry

/**
 * Registers every preview screen. Not composable, so `PreviewEntryProviderTest` builds it on the JVM
 * and catches a catalog key that was never registered here.
 */
fun previewEntryProvider(onOpen: (NavKey) -> Unit): (NavKey) -> NavEntry<NavKey> =
    entryProvider {
        previewHomeEntry(onOpen)
    }
