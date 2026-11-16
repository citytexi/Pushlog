package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import com.citytexi.pushlog.core.designsystem.component.button.PushlogActionButtonSize
import com.citytexi.pushlog.core.designsystem.component.button.PushlogActionButtonVariant
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody

// The preview canvas is 320x470 and clips, so each preview holds one variant or size; 2x font fits too.

@PushlogPreview
@Composable
private fun PushlogActionButtonBrandSolidPreview() {
    PushlogTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.BRAND_SOLID) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonNeutralSolidPreview() {
    PushlogTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.NEUTRAL_SOLID) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonNeutralWeakPreview() {
    PushlogTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.NEUTRAL_WEAK) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonCriticalSolidPreview() {
    PushlogTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.CRITICAL_SOLID) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonGhostPreview() {
    PushlogTheme {
        PreviewBody { ActionButtonVariantSection(PushlogActionButtonVariant.GHOST) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonSizeSmallPreview() {
    PushlogTheme {
        PreviewBody { ActionButtonSizeSection(PushlogActionButtonSize.SMALL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonSizeMediumPreview() {
    PushlogTheme {
        PreviewBody { ActionButtonSizeSection(PushlogActionButtonSize.MEDIUM) }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonSizeLargePreview() {
    PushlogTheme {
        PreviewBody { ActionButtonSizeSection(PushlogActionButtonSize.LARGE) }
    }
}
