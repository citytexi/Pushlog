package com.citytexi.pushlog.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import com.citytexi.pushlog.core.designsystem.theme.color.PushlogColors
import com.citytexi.pushlog.core.designsystem.theme.dimension.PushlogRadius
import com.citytexi.pushlog.core.designsystem.theme.typography.PushlogTypography

/**
 * M3 컴포넌트가 브랜드 색으로 보이게 하는 임시 다리. 기본값(M3 보라)이 새지 않도록 생성자의
 * 모든 칸을 이름과 함께 채운다. 칸별 근거는 `docs/architecture/design-system.md`.
 */
internal fun PushlogColors.toColorScheme(): ColorScheme =
    ColorScheme(
        primary = bg.brandSolid,
        onPrimary = fg.onBrandSolid,
        primaryContainer = bg.brandWeak,
        onPrimaryContainer = fg.brandContrast,
        inversePrimary = bg.brandWeak,
        secondary = bg.neutralSolid,
        onSecondary = fg.onNeutralSolid,
        secondaryContainer = bg.neutralWeak,
        onSecondaryContainer = fg.neutral,
        tertiary = bg.informativeSolid,
        onTertiary = fg.onInformativeSolid,
        tertiaryContainer = bg.informativeWeak,
        onTertiaryContainer = fg.informativeContrast,
        background = bg.layerDefault,
        onBackground = fg.neutral,
        surface = bg.layerDefault,
        onSurface = fg.neutral,
        surfaceVariant = bg.neutralWeak,
        onSurfaceVariant = fg.neutralMuted,
        // 기본값(primary)이면 tonal elevation이 브랜드 색으로 물든다.
        surfaceTint = Color.Transparent,
        inverseSurface = bg.neutralInverted,
        inverseOnSurface = fg.neutralInverted,
        error = bg.criticalSolid,
        onError = fg.onCriticalSolid,
        errorContainer = bg.criticalWeak,
        onErrorContainer = fg.criticalContrast,
        outline = stroke.neutralWeak,
        outlineVariant = stroke.neutralMuted,
        scrim = bg.overlay,
        surfaceBright = bg.layerFloating,
        surfaceDim = bg.layerBasement,
        surfaceContainer = bg.neutralMuted,
        surfaceContainerHigh = bg.layerFloating,
        surfaceContainerHighest = bg.neutralWeak,
        surfaceContainerLow = bg.layerFloating,
        surfaceContainerLowest = bg.layerDefault,
        primaryFixed = bg.brandWeak,
        primaryFixedDim = bg.brandWeakPressed,
        onPrimaryFixed = fg.brandContrast,
        onPrimaryFixedVariant = fg.brand,
        secondaryFixed = bg.neutralWeak,
        secondaryFixedDim = bg.neutralWeakPressed,
        onSecondaryFixed = fg.neutral,
        onSecondaryFixedVariant = fg.neutralMuted,
        tertiaryFixed = bg.informativeWeak,
        tertiaryFixedDim = bg.informativeWeakPressed,
        onTertiaryFixed = fg.informativeContrast,
        onTertiaryFixedVariant = fg.informative,
    )

/** M3 글자 15칸을 Pretendard 토큰으로 채운다. 빠진 칸은 기본 폰트로 그려진다. */
internal fun PushlogTypography.toMaterialTypography(): Typography =
    Typography(
        displayLarge = t14Bold,
        displayMedium = t13Bold,
        displaySmall = t12Bold,
        headlineLarge = t11Bold,
        headlineMedium = t10Bold,
        headlineSmall = t9Bold,
        titleLarge = t7Bold,
        titleMedium = t5Bold,
        titleSmall = t4Bold,
        bodyLarge = t5Regular,
        bodyMedium = t4Regular,
        bodySmall = t3Regular,
        labelLarge = t4Medium,
        labelMedium = t3Medium,
        labelSmall = t2Medium,
    )

internal fun PushlogRadius.toMaterialShapes(): Shapes =
    Shapes(
        extraSmall = RoundedCornerShape(r1),
        small = RoundedCornerShape(r2),
        medium = RoundedCornerShape(r3),
        large = RoundedCornerShape(r4),
        extraLarge = RoundedCornerShape(r6),
    )
