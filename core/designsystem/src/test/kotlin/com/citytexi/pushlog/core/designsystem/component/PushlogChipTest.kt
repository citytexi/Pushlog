package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogChipTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun selectableChipExposesToggleState() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogChip(text = "켜짐", onClick = {}, selected = true)
                PushlogChip(text = "꺼짐", onClick = {}, selected = false)
            }
        }
        composeRule.onNodeWithText("켜짐").assertIsOn()
        composeRule.onNodeWithText("꺼짐").assertIsOff()
        val role = composeRule.onNodeWithText("켜짐").fetchSemanticsNode().config[SemanticsProperties.Role]
        assertEquals(Role.Checkbox, role)
    }

    @Test
    fun plainChipIsButton() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogChip(text = "버튼", onClick = {}) }
        }
        val config = composeRule.onNodeWithText("버튼").fetchSemanticsNode().config
        assertEquals(Role.Button, config[SemanticsProperties.Role])
        assertFalse(config.contains(SemanticsProperties.ToggleableState))
    }

    @Test
    fun suffixIconIsNotSeparateTarget() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogChip(text = "필터", onClick = {}, suffixIcon = rememberVectorPainter(PreviewSampleIcons.ChevronDown))
            }
        }
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(1)
    }

    @Test
    fun minHeightIs36dpAtDefaultScale() {
        var heightPx = 0
        var density = 1f
        val fontScale = 1f
        composeRule.setContent {
            val current = LocalDensity.current
            density = current.density
            CompositionLocalProvider(LocalDensity provides Density(current.density, fontScale)) {
                PushlogTheme(darkTheme = false) {
                    Box(Modifier.onSizeChanged { heightPx = it.height }) { PushlogChip(text = "필터", onClick = {}) }
                }
            }
        }
        composeRule.waitForIdle()
        assertEquals(36f * density, heightPx.toFloat(), 1f)
    }

    // Compose의 글자 배율은 크게 갈수록 완만해져서(비선형) 200%에서 19sp 줄 높이가 36dp를 넘지 못한다.
    // 높이가 고정이 아니라 최소값임을 보이려고 비선형 구간을 넘는 큰 배율을 쓴다.
    @Test
    fun growsWithFontScale() {
        var heightPx = 0
        var density = 1f
        composeRule.setContent {
            val current = LocalDensity.current
            density = current.density
            CompositionLocalProvider(LocalDensity provides Density(current.density, 4f)) {
                PushlogTheme(darkTheme = false) {
                    Box(Modifier.onSizeChanged { heightPx = it.height }) { PushlogChip(text = "필터", onClick = {}) }
                }
            }
        }
        composeRule.waitForIdle()
        assertTrue("height $heightPx <= ${36f * density}", heightPx > 36f * density)
    }

    @Test
    fun longLabelKeepsSuffixIcon() {
        var iconWidth = 0f
        val probe =
            object : Painter() {
                override val intrinsicSize = Size(16f, 16f)

                override fun DrawScope.onDraw() {
                    iconWidth = size.width
                }
            }
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Box(Modifier.width(120.dp)) {
                    PushlogChip(text = "아주 긴 라벨이 들어가는 칩입니다 정말 깁니다", onClick = {}, suffixIcon = probe)
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.onRoot().captureToImage()
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNodeWithText("아주 긴 라벨이 들어가는 칩입니다 정말 깁니다").performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertTrue("suffix icon width $iconWidth", iconWidth > 0f)
        assertEquals(1, results.first().lineCount)
        assertTrue(results.first().hasVisualOverflow)
    }
}
