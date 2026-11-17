package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBox
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBoxGroup
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBoxLayout
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBoxType
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private val OneColumnLabels = listOf("Morning", "Afternoon", "Evening")

private val TwoColumnLabels = listOf("Mail", "Chat", "News", "Ad")

private val ThreeColumnLabels = listOf("1", "2", "3", "4", "5", "6")

/** Registers the PushlogSelectBoxGroup screen for [PushlogSelectBoxGroupPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogSelectBoxGroupPreviewEntry(onBack: () -> Unit) {
    entry<PushlogSelectBoxGroupPreviewKey> {
        PreviewScaffold(title = "PushlogSelectBoxGroup", onBack = onBack) { PushlogSelectBoxGroupPreviewContent() }
    }
}

/** PushlogSelectBoxGroup with one, two, and three columns; draws no background of its own. */
@Composable
fun PushlogSelectBoxGroupPreviewContent() {
    PreviewBody {
        SelectBoxGroupOneColumnSection()
        SelectBoxGroupTwoColumnsSection()
        SelectBoxGroupThreeColumnsSection()
    }
}

/** One column keeps each box at its own height and lays boxes out horizontally. */
@Composable
private fun SelectBoxGroupOneColumnSection() {
    var selected by remember { mutableIntStateOf(0) }
    PreviewSectionTitle("1 column")
    PushlogSelectBoxGroup {
        OneColumnLabels.forEachIndexed { index, label ->
            PushlogSelectBox(
                selected = selected == index,
                onClick = { selected = index },
                type = PushlogSelectBoxType.SINGLE,
                label = label,
                description = if (index == 0) "Before noon" else null,
            )
        }
    }
}

/** Boxes in a row share the tallest height; the last box overrides the vertical default. */
@Composable
private fun SelectBoxGroupTwoColumnsSection() {
    val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
    val selected = remember { mutableStateListOf(true, false, false, false) }
    PreviewSectionTitle("2 columns")
    PushlogSelectBoxGroup(columns = 2) {
        TwoColumnLabels.forEachIndexed { index, label ->
            PushlogSelectBox(
                selected = selected[index],
                onClick = { selected[index] = !selected[index] },
                type = PushlogSelectBoxType.MULTIPLE,
                label = label,
                description = if (index == 0) "Inbox and replies" else null,
                prefixIcon = plus,
                layout = if (index == TwoColumnLabels.lastIndex) PushlogSelectBoxLayout.HORIZONTAL else null,
            )
        }
    }
}

/** Single-character labels, so a three-column cell still fits them at 2x font. */
@Composable
private fun SelectBoxGroupThreeColumnsSection() {
    var selected by remember { mutableIntStateOf(0) }
    PreviewSectionTitle("3 columns")
    PushlogSelectBoxGroup(columns = 3) {
        ThreeColumnLabels.forEachIndexed { index, label ->
            PushlogSelectBox(
                selected = selected == index,
                onClick = { selected = index },
                type = PushlogSelectBoxType.SINGLE,
                label = label,
            )
        }
    }
}

// The preview canvas is 320x470 and clips, so each preview holds one section; 2x font fits too.
@PushlogPreview
@Composable
private fun PushlogSelectBoxGroupOneColumnPreview() {
    PushlogPreviewTheme {
        PreviewBody { SelectBoxGroupOneColumnSection() }
    }
}

@PushlogPreview
@Composable
private fun PushlogSelectBoxGroupTwoColumnsPreview() {
    PushlogPreviewTheme {
        PreviewBody { SelectBoxGroupTwoColumnsSection() }
    }
}

@PushlogPreview
@Composable
private fun PushlogSelectBoxGroupThreeColumnsPreview() {
    PushlogPreviewTheme {
        PreviewBody { SelectBoxGroupThreeColumnsSection() }
    }
}
