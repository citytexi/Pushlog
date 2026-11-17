package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import com.citytexi.pushlog.core.designsystem.component.switches.PushlogSwitchmarkSize
import com.citytexi.pushlog.core.designsystem.component.switches.PushlogSwitchmarkTone
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody

// The preview canvas is 320x470 and clips, so each preview holds one tone, one size, the long label, or the hidden label; 2x font fits too.

@PushlogPreview
@Composable
private fun PushlogSwitchToneNeutralPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchToneSection(PushlogSwitchmarkTone.NEUTRAL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchToneBrandPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchToneSection(PushlogSwitchmarkTone.BRAND) }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchSizeSmallPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchSizeSection(PushlogSwitchmarkSize.SMALL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchSizeMediumPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchSizeSection(PushlogSwitchmarkSize.MEDIUM) }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchSizeLargePreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchSizeSection(PushlogSwitchmarkSize.LARGE) }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchLongLabelPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchLongLabel() }
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchHiddenLabelPreview() {
    PushlogPreviewTheme {
        PreviewBody { SwitchHiddenLabel() }
    }
}
