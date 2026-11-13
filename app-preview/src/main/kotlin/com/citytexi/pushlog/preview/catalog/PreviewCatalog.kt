package com.citytexi.pushlog.preview.catalog

import com.citytexi.pushlog.preview.component.LoadingContentPreviewKey
import com.citytexi.pushlog.preview.component.PushlogButtonPreviewKey
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
        PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogButton", PushlogButtonPreviewKey),
        PreviewCatalogItem(PreviewCategory.COMPONENT, "LoadingContent", LoadingContentPreviewKey),
    )
