package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmarkSize
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmarkTone
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmarkVariant
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody

// The preview canvas is 320x470 and clips, so each preview holds one variant and tone, one size, or the long label; 2x font fits too.

@PushlogPreview
@Composable
private fun PushlogCheckboxSquareNeutralPreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckboxVariantSection(PushlogCheckmarkVariant.SQUARE, PushlogCheckmarkTone.NEUTRAL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckboxSquareBrandPreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckboxVariantSection(PushlogCheckmarkVariant.SQUARE, PushlogCheckmarkTone.BRAND) }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckboxGhostNeutralPreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckboxVariantSection(PushlogCheckmarkVariant.GHOST, PushlogCheckmarkTone.NEUTRAL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckboxGhostBrandPreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckboxVariantSection(PushlogCheckmarkVariant.GHOST, PushlogCheckmarkTone.BRAND) }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckboxSizeMediumPreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckboxSizeSection(PushlogCheckmarkSize.MEDIUM) }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckboxSizeLargePreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckboxSizeSection(PushlogCheckmarkSize.LARGE) }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckboxLongLabelPreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckboxLongLabel() }
    }
}
