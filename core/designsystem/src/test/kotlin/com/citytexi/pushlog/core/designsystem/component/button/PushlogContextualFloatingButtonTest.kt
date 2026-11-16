package com.citytexi.pushlog.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextLayoutResult
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
class PushlogContextualFloatingButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun shadowShrinksWithSurface() {
        val source = MutableInteractionSource()
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Box(Modifier.background(Color.White).padding(40.dp)) {
                    PushlogContextualFloatingButton(
                        text = "맨 위로",
                        onClick = {},
                        modifier = Modifier.width(200.dp),
                        interactionSource = remember { source },
                    )
                }
            }
        }
        composeRule.waitForIdle()
        val before = shadedWidth()
        composeRule.runOnIdle { source.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        val pressed = shadedWidth()
        assertTrue("shaded width $pressed should be below $before", pressed < before)
    }

    @Test
    fun clickableNodeCarriesNameRoleAndState() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogContextualFloatingButton(text = "저장", onClick = {}, loading = true)
            }
        }
        val config = composeRule.onNode(hasClickAction()).fetchSemanticsNode().config
        assertEquals(listOf("저장"), config[SemanticsProperties.Text].map { it.text })
        assertEquals(Role.Button, config[SemanticsProperties.Role])
        assertEquals("불러오는 중", config[SemanticsProperties.StateDescription])
    }

    @Test
    fun labelIsSingleLine() {
        val label = "A very long floating button label that cannot fit in one hundred twenty dp"
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogContextualFloatingButton(text = label, onClick = {}, modifier = Modifier.width(120.dp))
            }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        val config = composeRule.onNodeWithText(label).fetchSemanticsNode().config
        config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
        assertEquals(1, layouts.single().lineCount)
        assertTrue(layouts.single().hasVisualOverflow)
    }

    /** 가운데 가로줄에서 흰 바탕이 아닌 픽셀 수. 표면 바깥으로 번진 그림자까지 센다. */
    private fun shadedWidth(): Int {
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val y = image.height / 2
        return (0 until image.width).count { image[it, y].toArgb() != Color.White.toArgb() }
    }

    @Test
    fun iconOnlyTouchTargetAtLeast40dp() {
        var density = 1f
        composeRule.setContent {
            density = LocalDensity.current.density
            PushlogTheme(darkTheme = false) {
                PushlogContextualFloatingButton(
                    icon = rememberVectorPainter(PreviewSampleIcons.ArrowUp),
                    contentDescription = "맨 위로",
                    onClick = {},
                )
            }
        }
        val bounds = composeRule.onNodeWithContentDescription("맨 위로").fetchSemanticsNode().touchBoundsInRoot
        val min = 40.dp.value * density
        assertTrue("touch ${bounds.width}x${bounds.height} < $min", bounds.width >= min && bounds.height >= min)
    }

    @Test
    fun loadingBlocksClickKeepsName() {
        var loading by mutableStateOf(false)
        var clicks = 0
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogContextualFloatingButton(text = "맨 위로", onClick = { clicks++ }, loading = loading)
            }
        }
        loading = true
        composeRule.waitForIdle()
        composeRule.onNodeWithText("맨 위로").performClick()
        assertEquals(0, clicks)
        val config = composeRule.onNodeWithText("맨 위로").fetchSemanticsNode().config
        assertEquals("불러오는 중", config[SemanticsProperties.StateDescription])
        assertFalse(config.contains(SemanticsProperties.Disabled))
    }

    @Test
    fun iconOnlyExposesNameAndBlocksClickWhileLoading() {
        var loading by mutableStateOf(false)
        var clicks = 0
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogContextualFloatingButton(
                    icon = rememberVectorPainter(PreviewSampleIcons.ArrowUp),
                    contentDescription = "맨 위로",
                    onClick = { clicks++ },
                    loading = loading,
                )
            }
        }
        composeRule.onNodeWithContentDescription("맨 위로").assertHasClickAction().performClick()
        assertEquals(1, clicks)
        loading = true
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("맨 위로").performClick()
        assertEquals(1, clicks)
        val config = composeRule.onNodeWithContentDescription("맨 위로").fetchSemanticsNode().config
        assertEquals("불러오는 중", config[SemanticsProperties.StateDescription])
    }
}
