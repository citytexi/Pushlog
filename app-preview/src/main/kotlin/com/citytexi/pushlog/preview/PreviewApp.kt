package com.citytexi.pushlog.preview

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.preview.navigation.PreviewHomeKey
import com.citytexi.pushlog.preview.navigation.previewEntryProvider
import com.citytexi.pushlog.preview.settings.PreviewControlBar
import com.citytexi.pushlog.preview.settings.PreviewFontScale
import com.citytexi.pushlog.preview.settings.PreviewThemeMode

/**
 * Root of the preview app. The control bar sits outside [NavDisplay] so one theme and font scale
 * apply to every screen without passing them down.
 */
@Composable
fun PreviewApp() {
    var themeMode by rememberSaveable { mutableStateOf(PreviewThemeMode.SYSTEM) }
    var fontScale by rememberSaveable { mutableStateOf(PreviewFontScale.NORMAL) }
    val isDark =
        when (themeMode) {
            PreviewThemeMode.LIGHT -> false
            PreviewThemeMode.DARK -> true
            PreviewThemeMode.SYSTEM -> isSystemInDarkTheme()
        }

    val activity = LocalActivity.current as? ComponentActivity
    // SystemBarStyle.auto reads its dark-mode lambda only when enableEdgeToEdge runs, so re-run it per theme.
    DisposableEffect(activity, isDark) {
        activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isDark },
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isDark },
        )
        onDispose { }
    }

    val backStack = rememberNavBackStack(PreviewHomeKey)
    val onBack: () -> Unit = {
        if (backStack.size > 1) backStack.removeLastOrNull() else activity?.finish()
    }
    // NavDisplay disables its own back handler on a single entry; without this, back on the home
    // would only move the task to the background (Android 12+) instead of closing the app.
    BackHandler(enabled = backStack.size == 1) { activity?.finish() }

    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = fontScale.scale)) {
        PushlogTheme(darkTheme = isDark) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = PushlogTheme.color.bg.layerDefault,
                contentColor = PushlogTheme.color.fg.neutral,
            ) {
                Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                    NavDisplay(
                        backStack = backStack,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        onBack = onBack,
                        entryProvider = previewEntryProvider(onOpen = { key -> backStack.add(key) }),
                    )
                    PreviewControlBar(
                        themeMode = themeMode,
                        onThemeModeChange = { themeMode = it },
                        fontScale = fontScale,
                        onFontScaleChange = { fontScale = it },
                    )
                }
            }
        }
    }
}
