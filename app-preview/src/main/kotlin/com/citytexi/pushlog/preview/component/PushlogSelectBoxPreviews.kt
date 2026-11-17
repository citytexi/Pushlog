package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody

// The preview canvas is 320x470 and clips, so each preview holds one section; 2x font fits too.

@PushlogPreview
@Composable
private fun PushlogSelectBoxSinglePreview() {
    PushlogPreviewTheme {
        PreviewBody { SelectBoxSingleSection() }
    }
}

@PushlogPreview
@Composable
private fun PushlogSelectBoxMultiplePreview() {
    PushlogPreviewTheme {
        PreviewBody { SelectBoxMultipleSection() }
    }
}

@PushlogPreview
@Composable
private fun PushlogSelectBoxNoControlPreview() {
    PushlogPreviewTheme {
        PreviewBody { SelectBoxNoControlSection() }
    }
}

@PushlogPreview
@Composable
private fun PushlogSelectBoxPrefixSuffixPreview() {
    PushlogPreviewTheme {
        PreviewBody { SelectBoxPrefixSuffixSection() }
    }
}

@PushlogPreview
@Composable
private fun PushlogSelectBoxVerticalPreview() {
    PushlogPreviewTheme {
        PreviewBody { SelectBoxVerticalSection() }
    }
}

@PushlogPreview
@Composable
private fun PushlogSelectBoxLongTextPreview() {
    PushlogPreviewTheme {
        PreviewBody { SelectBoxLongTextSection() }
    }
}
