package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.switches.PushlogSwitch
import com.citytexi.pushlog.core.designsystem.component.switches.PushlogSwitchmarkSize
import com.citytexi.pushlog.core.designsystem.component.switches.PushlogSwitchmarkTone
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val LONG_LABEL = "A longer label that wraps onto a second line."

/** Registers the PushlogSwitch screen for [PushlogSwitchPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogSwitchPreviewEntry(onBack: () -> Unit) {
    entry<PushlogSwitchPreviewKey> {
        PreviewScaffold(title = "PushlogSwitch", onBack = onBack) { PushlogSwitchPreviewContent() }
    }
}

/** PushlogSwitch per tone, per size, with a long label, and with the label hidden; draws no background of its own. */
@Composable
fun PushlogSwitchPreviewContent() {
    PreviewBody {
        PushlogSwitchmarkTone.entries.forEach { SwitchToneSection(it) }
        PushlogSwitchmarkSize.entries.forEach { SwitchSizeSection(it) }
        SwitchLongLabel()
        SwitchHiddenLabel()
    }
}

/** One toggling row, then disabled Off and On rows. */
@Composable
internal fun SwitchToneSection(tone: PushlogSwitchmarkTone) {
    var checked by remember { mutableStateOf(true) }
    PreviewSectionTitle("tone ${tone.name}")
    PushlogSwitch(checked = checked, onCheckedChange = { checked = it }, label = "Toggle", tone = tone)
    PushlogSwitch(checked = false, onCheckedChange = {}, label = "Disabled off", tone = tone, enabled = false)
    PushlogSwitch(checked = true, onCheckedChange = {}, label = "Disabled on", tone = tone, enabled = false)
}

/** Off and On rows; SMALL keeps a 24dp row around its 16dp mark. */
@Composable
internal fun SwitchSizeSection(size: PushlogSwitchmarkSize) {
    PreviewSectionTitle("size ${size.name}")
    listOf(false, true).forEach { initial ->
        var checked by remember { mutableStateOf(initial) }
        PushlogSwitch(checked = checked, onCheckedChange = { checked = it }, label = "size ${size.name}", size = size)
    }
}

@Composable
internal fun SwitchLongLabel() {
    var checked by remember { mutableStateOf(true) }
    PreviewSectionTitle("long label")
    PushlogSwitch(checked = checked, onCheckedChange = { checked = it }, label = LONG_LABEL, modifier = Modifier.width(200.dp))
}

/** The label stays as the accessibility name when hidden. */
@Composable
internal fun SwitchHiddenLabel() {
    PreviewSectionTitle("hidden label")
    PushlogSwitchmarkSize.entries.forEach { size ->
        var checked by remember { mutableStateOf(true) }
        PushlogSwitch(checked = checked, onCheckedChange = { checked = it }, label = "Notifications", showLabel = false, size = size)
    }
}
