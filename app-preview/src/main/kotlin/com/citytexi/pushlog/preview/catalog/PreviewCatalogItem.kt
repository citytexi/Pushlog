package com.citytexi.pushlog.preview.catalog

import androidx.navigation3.runtime.NavKey

/** One row of the home list; [key] must also be registered in `previewEntryProvider`. */
data class PreviewCatalogItem(
    val category: PreviewCategory,
    val title: String,
    val key: NavKey,
)
