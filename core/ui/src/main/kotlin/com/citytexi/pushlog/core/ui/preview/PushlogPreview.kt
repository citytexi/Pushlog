package com.citytexi.pushlog.core.ui.preview

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

/**
 * Light, dark and 200% font previews. Wrap the body in `PushlogPreviewTheme { }` (core:designsystem): it follows uiMode via
 * `isSystemInDarkTheme()` and paints the theme background, which `showBackground` (always white) does not.
 */
@Preview(name = "Light", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Preview(name = "Light font 2x", showBackground = true, uiMode = UI_MODE_NIGHT_NO, fontScale = 2f)
annotation class PushlogPreview
