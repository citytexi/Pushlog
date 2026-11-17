package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.switches.PushlogSwitchmark
import com.citytexi.pushlog.core.designsystem.component.switches.PushlogSwitchmarkSize
import com.citytexi.pushlog.core.designsystem.component.switches.PushlogSwitchmarkTone
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

/** Registers the PushlogSwitchmark screen for [PushlogSwitchmarkPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogSwitchmarkPreviewEntry(onBack: () -> Unit) {
    entry<PushlogSwitchmarkPreviewKey> {
        PreviewScaffold(title = "PushlogSwitchmark", onBack = onBack) { PushlogSwitchmarkPreviewContent() }
    }
}

/** PushlogSwitchmark per tone and state, then per size; draws no background of its own. */
@Composable
fun PushlogSwitchmarkPreviewContent() {
    PreviewBody {
        PushlogSwitchmarkTone.entries.forEach { SwitchmarkToneSection(it) }
        PushlogSwitchmarkSize.entries.forEach { SwitchmarkSizeSection(it) }
    }
}

/**
 * Off, on, disabled off, disabled on at the default LARGE size. The first mark is tappable the way
 * the mark's KDoc describes for standalone use: the caller adds the tap area and shares its source.
 */
@Composable
private fun SwitchmarkToneSection(tone: PushlogSwitchmarkTone) {
    PreviewSectionTitle("tone ${tone.name}")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3)) {
        var checked by remember { mutableStateOf(false) }
        val source = remember { MutableInteractionSource() }
        PushlogSwitchmark(
            checked = checked,
            modifier =
                Modifier.toggleable(
                    value = checked,
                    interactionSource = source,
                    indication = null,
                    role = Role.Switch,
                    onValueChange = { checked = it },
                ),
            tone = tone,
            interactionSource = source,
        )
        PushlogSwitchmark(checked = true, tone = tone)
        PushlogSwitchmark(checked = false, tone = tone, enabled = false)
        PushlogSwitchmark(checked = true, tone = tone, enabled = false)
    }
}

/** Off and on. */
@Composable
private fun SwitchmarkSizeSection(size: PushlogSwitchmarkSize) {
    PreviewSectionTitle("size ${size.name}")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3)) {
        PushlogSwitchmark(checked = false, size = size)
        PushlogSwitchmark(checked = true, size = size)
    }
}

// The preview canvas is 320x470 and clips, so each preview holds one tone or size; 2x font fits too.
@PushlogPreview
@Composable
private fun PushlogSwitchmarkNeutralPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchmarkToneSection(PushlogSwitchmarkTone.NEUTRAL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchmarkBrandPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchmarkToneSection(PushlogSwitchmarkTone.BRAND) }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchmarkSizeSmallPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchmarkSizeSection(PushlogSwitchmarkSize.SMALL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchmarkSizeMediumPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchmarkSizeSection(PushlogSwitchmarkSize.MEDIUM) }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchmarkSizeLargePreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchmarkSizeSection(PushlogSwitchmarkSize.LARGE) }
    }
}
