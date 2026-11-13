package com.citytexi.pushlog.preview.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.citytexi.pushlog.preview.foundation.colorPreviewEntry
import com.citytexi.pushlog.preview.foundation.materialPreviewEntry
import com.citytexi.pushlog.preview.foundation.shapePreviewEntry
import com.citytexi.pushlog.preview.foundation.typographyPreviewEntry
import com.citytexi.pushlog.preview.home.previewHomeEntry

/**
 * Registers every preview screen. Not composable, so `PreviewEntryProviderTest` builds it on the JVM
 * and catches a catalog key that was never registered here.
 */
fun previewEntryProvider(
    onOpen: (NavKey) -> Unit,
    onBack: () -> Unit,
): (NavKey) -> NavEntry<NavKey> =
    entryProvider {
        previewHomeEntry(onOpen)
        colorPreviewEntry(onBack)
        typographyPreviewEntry(onBack)
        shapePreviewEntry(onBack)
        materialPreviewEntry(onBack)
    }
