package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
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
        var loading by mutableIntStateOf(0)
        var clicks = 0
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogContextualFloatingButton(text = "맨 위로", onClick = { clicks++ }, loading = loading == 1)
            }
        }
        loading = 1
        composeRule.waitForIdle()
        composeRule.onNodeWithText("맨 위로").performClick()
        assertEquals(0, clicks)
        val config = composeRule.onNodeWithText("맨 위로").fetchSemanticsNode().config
        assertEquals("불러오는 중", config[SemanticsProperties.StateDescription])
        assertFalse(config.contains(SemanticsProperties.Disabled))
    }

    @Test
    fun iconOnlyExposesNameAndBlocksClickWhileLoading() {
        var loading by mutableIntStateOf(0)
        var clicks = 0
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogContextualFloatingButton(
                    icon = rememberVectorPainter(PreviewSampleIcons.ArrowUp),
                    contentDescription = "맨 위로",
                    onClick = { clicks++ },
                    loading = loading == 1,
                )
            }
        }
        composeRule.onNodeWithContentDescription("맨 위로").assertHasClickAction().performClick()
        assertEquals(1, clicks)
        loading = 1
        composeRule.waitForIdle()
        composeRule.onNodeWithContentDescription("맨 위로").performClick()
        assertEquals(1, clicks)
        val config = composeRule.onNodeWithContentDescription("맨 위로").fetchSemanticsNode().config
        assertEquals("불러오는 중", config[SemanticsProperties.StateDescription])
    }
}
