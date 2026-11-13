package com.citytexi.pushlog.preview.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.catalog.PreviewCatalogItem
import com.citytexi.pushlog.preview.catalog.PreviewCategory
import com.citytexi.pushlog.preview.catalog.previewCatalog
import com.citytexi.pushlog.preview.navigation.PreviewHomeKey

/** Registers the catalog home for [PreviewHomeKey]. */
fun EntryProviderScope<NavKey>.previewHomeEntry(onOpen: (NavKey) -> Unit) {
    entry<PreviewHomeKey> { PreviewHomeContent(catalog = previewCatalog, onOpen = onOpen) }
}

/** Catalog list grouped by [PreviewCategory]; draws no background of its own. */
@Composable
fun PreviewHomeContent(
    catalog: List<PreviewCatalogItem>,
    onOpen: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gutter = PushlogTheme.dimension.spacingX.globalGutter
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = PushlogTheme.dimension.x4),
    ) {
        item {
            Text(
                text = "Pushlog Preview",
                modifier = Modifier.padding(horizontal = gutter),
                style = PushlogTheme.typography.screenTitle,
                color = PushlogTheme.color.fg.neutral,
            )
        }
        if (catalog.isEmpty()) {
            item {
                Text(
                    text = "등록된 화면이 없다",
                    modifier = Modifier.padding(horizontal = gutter, vertical = PushlogTheme.dimension.x3),
                    style = PushlogTheme.typography.t5Regular,
                    color = PushlogTheme.color.fg.neutralSubtle,
                )
            }
        }
        PreviewCategory.entries.forEach { category ->
            val rows = catalog.filter { it.category == category }
            if (rows.isNotEmpty()) {
                item {
                    Text(
                        text = category.title(),
                        modifier =
                            Modifier.padding(
                                start = gutter,
                                end = gutter,
                                top = PushlogTheme.dimension.x6,
                                bottom = PushlogTheme.dimension.x2,
                            ),
                        style = PushlogTheme.typography.t4Bold,
                        color = PushlogTheme.color.fg.neutralMuted,
                    )
                }
                items(rows) { row ->
                    Text(
                        text = row.title,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable { onOpen(row.key) }
                                .padding(horizontal = gutter, vertical = PushlogTheme.dimension.x3),
                        style = PushlogTheme.typography.t5Regular,
                        color = PushlogTheme.color.fg.neutral,
                    )
                }
            }
        }
    }
}

private fun PreviewCategory.title(): String =
    when (this) {
        PreviewCategory.FOUNDATION -> "Foundation"
        PreviewCategory.COMPONENT -> "Component"
    }

@PushlogPreview
@Composable
private fun PreviewHomeContentPreview() {
    PushlogTheme {
        PreviewHomeContent(
            catalog = listOf(PreviewCatalogItem(PreviewCategory.FOUNDATION, "Color", PreviewHomeKey)),
            onOpen = {},
        )
    }
}
