package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.segmentedcontrol.PushlogSegmentedControl
import com.citytexi.pushlog.core.designsystem.component.segmentedcontrol.PushlogSegmentedControlOption
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private val ViewLabels = listOf("All", "Unread", "Starred", "Muted")
private val WrappingLabels = listOf("New items", "Old items")

/** Registers the PushlogSegmentedControl screen for [PushlogSegmentedControlPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogSegmentedControlPreviewEntry(onBack: () -> Unit) {
    entry<PushlogSegmentedControlPreviewKey> {
        PreviewScaffold(title = "PushlogSegmentedControl", onBack = onBack) { PushlogSegmentedControlPreviewContent() }
    }
}

/** PushlogSegmentedControl with 2, 3 and 4 options, stretched, and narrow with wrapping labels; draws no background of its own. */
@Composable
fun PushlogSegmentedControlPreviewContent() {
    PreviewBody {
        SegmentedControlCountSection()
        SegmentedControlWidthSection()
    }
}

/** One selectable control per option count. */
@Composable
internal fun SegmentedControlCountSection() {
    // One control per allowed count: 2 up to every label.
    for (count in 2..ViewLabels.size) {
        PreviewSectionTitle("$count options")
        SelectableSegmentedControl(ViewLabels.take(count))
    }
}

/** fillMaxWidth stretches cells and indicator; a 200dp width shrinks cells and wraps the labels. */
@Composable
internal fun SegmentedControlWidthSection() {
    PreviewSectionTitle("fillMaxWidth")
    SelectableSegmentedControl(ViewLabels.take(2), Modifier.fillMaxWidth())
    PreviewSectionTitle("200dp, wrapping labels")
    SelectableSegmentedControl(WrappingLabels, Modifier.width(200.dp))
}

@Composable
private fun SelectableSegmentedControl(
    labels: List<String>,
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableIntStateOf(0) }
    PushlogSegmentedControl(
        options = labels.map { PushlogSegmentedControlOption(it) },
        selectedIndex = selected,
        onSelectedIndexChange = { selected = it },
        modifier = modifier,
    )
}

@PushlogPreview
@Composable
private fun PushlogSegmentedControlCountPreview() {
    PushlogPreviewTheme {
        PreviewBody { SegmentedControlCountSection() }
    }
}

@PushlogPreview
@Composable
private fun PushlogSegmentedControlWidthPreview() {
    PushlogPreviewTheme {
        PreviewBody { SegmentedControlWidthSection() }
    }
}
