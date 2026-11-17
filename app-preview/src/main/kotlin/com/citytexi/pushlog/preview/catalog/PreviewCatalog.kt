package com.citytexi.pushlog.preview.catalog

import com.citytexi.pushlog.preview.component.LoadingContentPreviewKey
import com.citytexi.pushlog.preview.component.PushlogActionButtonPreviewKey
import com.citytexi.pushlog.preview.component.PushlogCheckboxGroupPreviewKey
import com.citytexi.pushlog.preview.component.PushlogCheckboxPreviewKey
import com.citytexi.pushlog.preview.component.PushlogCheckmarkPreviewKey
import com.citytexi.pushlog.preview.component.PushlogChipPreviewKey
import com.citytexi.pushlog.preview.component.PushlogContextualFloatingButtonPreviewKey
import com.citytexi.pushlog.preview.component.PushlogRadioGroupPreviewKey
import com.citytexi.pushlog.preview.component.PushlogRadioPreviewKey
import com.citytexi.pushlog.preview.component.PushlogRadiomarkPreviewKey
import com.citytexi.pushlog.preview.component.PushlogSwitchPreviewKey
import com.citytexi.pushlog.preview.component.PushlogSwitchmarkPreviewKey
import com.citytexi.pushlog.preview.foundation.ColorPreviewKey
import com.citytexi.pushlog.preview.foundation.MaterialPreviewKey
import com.citytexi.pushlog.preview.foundation.ShapePreviewKey
import com.citytexi.pushlog.preview.foundation.TypographyPreviewKey

/** Screens listed on the home, in display order within each [PreviewCategory]. */
val previewCatalog: List<PreviewCatalogItem> =
    listOf(
        PreviewCatalogItem(PreviewCategory.FOUNDATION, "Color", ColorPreviewKey),
        PreviewCatalogItem(PreviewCategory.FOUNDATION, "Typography", TypographyPreviewKey),
        PreviewCatalogItem(PreviewCategory.FOUNDATION, "Shape", ShapePreviewKey),
        PreviewCatalogItem(PreviewCategory.FOUNDATION, "Material", MaterialPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogActionButton", PushlogActionButtonPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogChip", PushlogChipPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogCheckmark", PushlogCheckmarkPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogCheckbox", PushlogCheckboxPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogCheckboxGroup", PushlogCheckboxGroupPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogRadiomark", PushlogRadiomarkPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogRadio", PushlogRadioPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogRadioGroup", PushlogRadioGroupPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogSwitchmark", PushlogSwitchmarkPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogSwitch", PushlogSwitchPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogContextualFloatingButton", PushlogContextualFloatingButtonPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "LoadingContent", LoadingContentPreviewKey),
    )
