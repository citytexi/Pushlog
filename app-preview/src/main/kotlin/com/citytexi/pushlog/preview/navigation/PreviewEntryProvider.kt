package com.citytexi.pushlog.preview.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.citytexi.pushlog.preview.component.loadingContentPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogActionButtonPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogCheckboxGroupPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogCheckboxPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogCheckmarkPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogChipPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogContextualFloatingButtonPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogRadioGroupPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogRadioPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogRadiomarkPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogSegmentedControlPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogSelectBoxGroupPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogSelectBoxPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogSwitchPreviewEntry
import com.citytexi.pushlog.preview.component.pushlogSwitchmarkPreviewEntry
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
        pushlogActionButtonPreviewEntry(onBack)
        pushlogChipPreviewEntry(onBack)
        pushlogCheckmarkPreviewEntry(onBack)
        pushlogCheckboxPreviewEntry(onBack)
        pushlogCheckboxGroupPreviewEntry(onBack)
        pushlogRadiomarkPreviewEntry(onBack)
        pushlogRadioPreviewEntry(onBack)
        pushlogRadioGroupPreviewEntry(onBack)
        pushlogSwitchmarkPreviewEntry(onBack)
        pushlogSwitchPreviewEntry(onBack)
        pushlogSegmentedControlPreviewEntry(onBack)
        pushlogSelectBoxPreviewEntry(onBack)
        pushlogSelectBoxGroupPreviewEntry(onBack)
        pushlogContextualFloatingButtonPreviewEntry(onBack)
        loadingContentPreviewEntry(onBack)
    }
