package com.citytexi.pushlog.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.dimension.DefaultPushlogDimension
import com.citytexi.pushlog.core.designsystem.theme.dimension.DefaultPushlogRadius
import com.citytexi.pushlog.core.designsystem.theme.motion.DefaultPushlogMotion
import com.citytexi.pushlog.core.designsystem.theme.shadow.DarkPushlogShadows
import com.citytexi.pushlog.core.designsystem.theme.shadow.LightPushlogShadows
import org.junit.Assert.assertEquals
import org.junit.Test

class FoundationScaleTest {
    @Test
    fun dimension() {
        assertEquals(2.dp, DefaultPushlogDimension.x0Half)
        assertEquals(18.dp, DefaultPushlogDimension.x4Half)
        assertEquals(64.dp, DefaultPushlogDimension.x16)
    }

    @Test
    fun semanticSpacingFollowsSeed() {
        val d = DefaultPushlogDimension
        assertEquals(d.x2, d.spacingX.betweenChips)
        assertEquals(d.x4, d.spacingX.globalGutter)
        assertEquals(d.x3, d.spacingY.componentDefault)
        assertEquals(d.x5, d.spacingY.navToTitle)
        assertEquals(d.x14, d.spacingY.screenBottom)
        assertEquals(d.x1Half, d.spacingY.betweenText)
    }

    @Test
    fun radius() {
        assertEquals(2.dp, DefaultPushlogRadius.r0Half)
        assertEquals(24.dp, DefaultPushlogRadius.r6)
        assertEquals(RoundedCornerShape(percent = 50), DefaultPushlogRadius.full)
    }

    @Test
    fun shadowS1() {
        assertEquals(1.dp, LightPushlogShadows.s1.offsetY)
        assertEquals(4.dp, LightPushlogShadows.s1.blur)
        assertEquals(Color(0x14000000), LightPushlogShadows.s1.color)
        assertEquals(Color(0x80000000), DarkPushlogShadows.s1.color)
    }

    @Test
    fun shadowS3() {
        assertEquals(16.dp, LightPushlogShadows.s3.blur)
        assertEquals(Color(0x1F000000), LightPushlogShadows.s3.color)
        assertEquals(Color(0xCC000000), DarkPushlogShadows.s3.color)
    }

    @Test
    fun timingFunctionEnter() = assertEquals(CubicBezierEasing(0f, 0f, 0.15f, 1f), DefaultPushlogMotion.timingFunction.enter)

    @Test
    fun durationD3() {
        val duration = DefaultPushlogMotion.duration
        assertEquals(150, duration.d3)
        assertEquals(300, duration.d6)
        assertEquals(150, duration.colorTransition)
        assertEquals(150, duration.pressedScale)
    }
}
