package com.citytexi.pushlog.core.designsystem.theme.color

import org.junit.Assert.assertEquals
import org.junit.Test

/** SEED 연결을 그대로 옮겼는지 지키는 표본. 다크 예외는 `BrandContrastTest`가 대비로 함께 지킨다. */
class SeedMappingTest {
    @Test
    fun strokeWarningWeakIsYellow300() {
        assertEquals(LightPalette.yellow300, LightPushlogColors.stroke.warningWeak)
        assertEquals(DarkPalette.yellow300, DarkPushlogColors.stroke.warningWeak)
    }

    @Test
    fun brandSolidDarkExceptionsOnly() {
        assertEquals(LightPalette.forest600, LightPushlogColors.bg.brandSolid)
        assertEquals(LightPalette.forest700, LightPushlogColors.bg.brandSolidPressed)
        assertEquals(DarkPalette.forest500, DarkPushlogColors.bg.brandSolid)
        assertEquals(DarkPalette.forest400, DarkPushlogColors.bg.brandSolidPressed)
    }

    @Test
    fun otherWarningStrokesFollowSeed() {
        assertEquals(LightPalette.yellow700, LightPushlogColors.stroke.warningSolid)
        assertEquals(DarkPalette.yellow700, DarkPushlogColors.stroke.warningSolid)
    }
}
