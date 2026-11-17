package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import com.citytexi.pushlog.core.designsystem.component.button.PushlogActionButtonSize
import com.citytexi.pushlog.core.designsystem.component.button.PushlogActionButtonVariant
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody

// The preview canvas is 320x470 and clips, so each preview holds one variant or size; 2x font fits too.

@PushlogPreview
@Composable
private fun PushlogActionButtonBrandSolidPreview() {
    PushlogPreviewTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.BRAND_SOLID) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonNeutralSolidPreview() {
    PushlogPreviewTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.NEUTRAL_SOLID) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonNeutralWeakPreview() {
    PushlogPreviewTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.NEUTRAL_WEAK) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonCriticalSolidPreview() {
    PushlogPreviewTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.CRITICAL_SOLID) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonGhostPreview() {
    PushlogPreviewTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.GHOST) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonSizeSmallPreview() {
    PushlogPreviewTheme {
        PreviewBody { ActionButtonSizeSection(PushlogActionButtonSize.SMALL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonSizeMediumPreview() {
    PushlogPreviewTheme {
        PreviewBody { ActionButtonSizeSection(PushlogActionButtonSize.MEDIUM) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonSizeLargePreview() {
    PushlogPreviewTheme {
        PreviewBody { ActionButtonSizeSection(PushlogActionButtonSize.LARGE) }
    }
}
