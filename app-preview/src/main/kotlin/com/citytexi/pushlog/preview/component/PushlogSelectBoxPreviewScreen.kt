package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBox
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBoxFooterVisibility
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBoxLayout
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBoxType
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val DESCRIPTION = "A short description under the label."
private const val LONG_LABEL = "A long label that keeps going and wraps onto more lines"
private const val LONG_DESCRIPTION =
    "A long description that runs past two lines on a narrow card, so the second line ends with an ellipsis " +
        "instead of pushing the card taller and taller."

/** Registers the PushlogSelectBox screen for [PushlogSelectBoxPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogSelectBoxPreviewEntry(onBack: () -> Unit) {
    entry<PushlogSelectBoxPreviewKey> {
        PreviewScaffold(title = "PushlogSelectBox", onBack = onBack) { PushlogSelectBoxPreviewContent() }
    }
}

/**
 * PushlogSelectBox per type, without a control, with prefix and suffix, vertical, with long text,
 * and with a footer; draws no background of its own.
 */
@Composable
fun PushlogSelectBoxPreviewContent() {
    PreviewBody {
        SelectBoxSingleSection()
        SelectBoxMultipleSection()
        SelectBoxNoControlSection()
        SelectBoxPrefixSuffixSection()
        SelectBoxVerticalSection()
        SelectBoxLongTextSection()
        SelectBoxFooterSection()
    }
}

/** Single choice assigns the tapped index; tapping the selected box again keeps it selected. */
@Composable
internal fun SelectBoxSingleSection() {
    var selected by remember { mutableIntStateOf(0) }
    PreviewSectionTitle("SINGLE")
    PushlogSelectBox(
        selected = selected == 0,
        onClick = { selected = 0 },
        type = PushlogSelectBoxType.SINGLE,
        label = "With description",
        description = DESCRIPTION,
    )
    PushlogSelectBox(selected = false, onClick = {}, type = PushlogSelectBoxType.SINGLE, label = "Disabled", enabled = false)
    PushlogSelectBox(selected = true, onClick = {}, type = PushlogSelectBoxType.SINGLE, label = "Disabled selected", enabled = false)
}

@Composable
internal fun SelectBoxMultipleSection() {
    var selected by remember { mutableStateOf(true) }
    PreviewSectionTitle("MULTIPLE")
    PushlogSelectBox(
        selected = selected,
        onClick = { selected = !selected },
        type = PushlogSelectBoxType.MULTIPLE,
        label = "With description",
        description = DESCRIPTION,
    )
    PushlogSelectBox(selected = false, onClick = {}, type = PushlogSelectBoxType.MULTIPLE, label = "Disabled", enabled = false)
    PushlogSelectBox(selected = true, onClick = {}, type = PushlogSelectBoxType.MULTIPLE, label = "Disabled selected", enabled = false)
}

/** The mark is hidden but the role still follows the type. */
@Composable
internal fun SelectBoxNoControlSection() {
    var selected by remember { mutableIntStateOf(0) }
    PreviewSectionTitle("no control")
    listOf("First", "Second").forEachIndexed { index, label ->
        PushlogSelectBox(
            selected = selected == index,
            onClick = { selected = index },
            type = PushlogSelectBoxType.SINGLE,
            label = label,
            showControl = false,
        )
    }
}

/** The suffix sits right before the mark. */
@Composable
internal fun SelectBoxPrefixSuffixSection() {
    val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
    PreviewSectionTitle("prefix and suffix")
    listOf(true, false).forEach { initial ->
        var selected by remember { mutableStateOf(initial) }
        PushlogSelectBox(
            selected = selected,
            onClick = { selected = !selected },
            type = PushlogSelectBoxType.MULTIPLE,
            label = "Add egg",
            description = DESCRIPTION,
            prefixIcon = plus,
            suffix = { Text("+1,000원", style = PushlogTheme.typography.t4Medium, color = PushlogTheme.color.fg.neutral) },
        )
    }
}

/** The prefix icon sits above the texts, as in a multi-column group. */
@Composable
internal fun SelectBoxVerticalSection() {
    val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
    var selected by remember { mutableIntStateOf(0) }
    PreviewSectionTitle("vertical")
    listOf("Day", "Night").forEachIndexed { index, label ->
        PushlogSelectBox(
            selected = selected == index,
            onClick = { selected = index },
            type = PushlogSelectBoxType.SINGLE,
            label = label,
            modifier = Modifier.width(160.dp),
            description = DESCRIPTION,
            prefixIcon = plus,
            layout = PushlogSelectBoxLayout.VERTICAL,
        )
    }
}

/** The label wraps without limit; the description stops at two lines with an ellipsis. */
@Composable
internal fun SelectBoxLongTextSection() {
    var selected by remember { mutableStateOf(false) }
    PreviewSectionTitle("long text")
    PushlogSelectBox(
        selected = selected,
        onClick = { selected = !selected },
        type = PushlogSelectBoxType.MULTIPLE,
        label = LONG_LABEL,
        description = LONG_DESCRIPTION,
    )
}

/** Each box starts selected, so only the when-unselected box hides its footer until tapped. */
@Composable
internal fun SelectBoxFooterSection() {
    PreviewSectionTitle("footer")
    listOf(
        PushlogSelectBoxFooterVisibility.WHEN_SELECTED to "When selected",
        PushlogSelectBoxFooterVisibility.ALWAYS to "Always",
        PushlogSelectBoxFooterVisibility.WHEN_NOT_SELECTED to "When unselected",
    ).forEach { (visibility, label) ->
        var selected by remember { mutableStateOf(true) }
        PushlogSelectBox(
            selected = selected,
            onClick = { selected = !selected },
            type = PushlogSelectBoxType.MULTIPLE,
            label = label,
            footerVisibility = visibility,
            footer = { SelectBoxFooterSample() },
        )
    }
}

@Composable
private fun SelectBoxFooterSample() {
    val x5 = PushlogTheme.dimension.x5
    Box(Modifier.padding(start = x5, end = x5, bottom = x5)) {
        Text("Extra details", style = PushlogTheme.typography.t3Medium, color = PushlogTheme.color.fg.neutralMuted)
    }
}
