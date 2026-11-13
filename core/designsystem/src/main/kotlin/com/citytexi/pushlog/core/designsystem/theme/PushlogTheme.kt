package com.citytexi.pushlog.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPushlogColors
import com.citytexi.pushlog.core.designsystem.theme.color.LightPushlogColors
import com.citytexi.pushlog.core.designsystem.theme.color.PushlogColors
import com.citytexi.pushlog.core.designsystem.theme.dimension.DefaultPushlogDimension
import com.citytexi.pushlog.core.designsystem.theme.dimension.DefaultPushlogRadius
import com.citytexi.pushlog.core.designsystem.theme.dimension.PushlogDimension
import com.citytexi.pushlog.core.designsystem.theme.dimension.PushlogRadius
import com.citytexi.pushlog.core.designsystem.theme.motion.DefaultPushlogMotion
import com.citytexi.pushlog.core.designsystem.theme.motion.PushlogMotion
import com.citytexi.pushlog.core.designsystem.theme.shadow.DarkPushlogShadows
import com.citytexi.pushlog.core.designsystem.theme.shadow.LightPushlogShadows
import com.citytexi.pushlog.core.designsystem.theme.shadow.PushlogShadows
import com.citytexi.pushlog.core.designsystem.theme.typography.DefaultPushlogTypography
import com.citytexi.pushlog.core.designsystem.theme.typography.PushlogTypography

// 기본값이 라이트라서 PushlogTheme으로 감싸지 않은 Preview·테스트도 라이트 값으로 그려진다.
internal val LocalPushlogColors = staticCompositionLocalOf { LightPushlogColors }
internal val LocalPushlogTypography = staticCompositionLocalOf { DefaultPushlogTypography }
internal val LocalPushlogDimension = staticCompositionLocalOf { DefaultPushlogDimension }
internal val LocalPushlogRadius = staticCompositionLocalOf { DefaultPushlogRadius }
internal val LocalPushlogShadows = staticCompositionLocalOf { LightPushlogShadows }
internal val LocalPushlogMotion = staticCompositionLocalOf { DefaultPushlogMotion }

/** Pushlog 토큰을 제공하고 같은 값으로 M3 테마를 감싼다. */
@Composable
fun PushlogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkPushlogColors else LightPushlogColors
    val shadows = if (darkTheme) DarkPushlogShadows else LightPushlogShadows
    CompositionLocalProvider(
        LocalPushlogColors provides colors,
        LocalPushlogTypography provides DefaultPushlogTypography,
        LocalPushlogDimension provides DefaultPushlogDimension,
        LocalPushlogRadius provides DefaultPushlogRadius,
        LocalPushlogShadows provides shadows,
        LocalPushlogMotion provides DefaultPushlogMotion,
    ) {
        MaterialTheme(
            colorScheme = colors.toColorScheme(),
            typography = DefaultPushlogTypography.toMaterialTypography(),
            shapes = DefaultPushlogRadius.toMaterialShapes(),
            content = content,
        )
    }
}

/** 화면이 토큰을 읽는 입구. `MaterialTheme`처럼 `PushlogTheme.color.bg.brandSolid`로 쓴다. */
object PushlogTheme {
    val color: PushlogColors
        @Composable @ReadOnlyComposable
        get() = LocalPushlogColors.current

    val typography: PushlogTypography
        @Composable @ReadOnlyComposable
        get() = LocalPushlogTypography.current

    val dimension: PushlogDimension
        @Composable @ReadOnlyComposable
        get() = LocalPushlogDimension.current

    val radius: PushlogRadius
        @Composable @ReadOnlyComposable
        get() = LocalPushlogRadius.current

    val shadow: PushlogShadows
        @Composable @ReadOnlyComposable
        get() = LocalPushlogShadows.current

    val motion: PushlogMotion
        @Composable @ReadOnlyComposable
        get() = LocalPushlogMotion.current
}
