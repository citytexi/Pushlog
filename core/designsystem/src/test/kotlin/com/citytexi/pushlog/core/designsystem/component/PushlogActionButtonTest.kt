package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
class PushlogActionButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun iconOnlyExposesContentDescription() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogActionButton(icon = rememberVectorPainter(PreviewSampleIcons.Plus), contentDescription = "추가", onClick = {})
            }
        }
        composeRule.onNodeWithContentDescription("추가").assertHasClickAction()
    }

    @Test
    fun loadingKeepsWidthAndBlocksClick() {
        var loading by mutableStateOf(false)
        var widthPx = 0
        var clicks = 0
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Box(Modifier.onSizeChanged { widthPx = it.width }) {
                    PushlogActionButton(text = "저장", onClick = { clicks++ }, loading = loading)
                }
            }
        }
        composeRule.waitForIdle()
        val idleWidth = widthPx
        loading = true
        composeRule.waitForIdle()
        assertEquals(idleWidth, widthPx)
        composeRule.onNodeWithText("저장").performClick()
        assertEquals(0, clicks)
        val config = composeRule.onNodeWithText("저장").fetchSemanticsNode().config
        assertEquals("불러오는 중", config[SemanticsProperties.StateDescription])
        assertFalse(config.contains(SemanticsProperties.Disabled))
    }

    @Test
    fun clickableNodeCarriesNameRoleAndState() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogActionButton(text = "저장", onClick = {}, loading = true)
            }
        }
        val config = composeRule.onNode(hasClickAction()).fetchSemanticsNode().config
        assertEquals(listOf("저장"), config[SemanticsProperties.Text].map { it.text })
        assertEquals(Role.Button, config[SemanticsProperties.Role])
        assertEquals("불러오는 중", config[SemanticsProperties.StateDescription])
    }

    @Test
    fun disabledIsNotClickable() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogActionButton(text = "저장", onClick = {}, enabled = false)
            }
        }
        composeRule.onNodeWithText("저장").assertIsNotEnabled()
    }

    @Test
    fun iconOnlyTouchTargetAtLeast40dp() {
        var density = 1f
        composeRule.setContent {
            density = LocalDensity.current.density
            PushlogTheme(darkTheme = false) {
                PushlogActionButton(
                    icon = rememberVectorPainter(PreviewSampleIcons.Plus),
                    contentDescription = "추가",
                    onClick = {},
                    size = PushlogActionButtonSize.SMALL,
                )
            }
        }
        val bounds = composeRule.onNodeWithContentDescription("추가").fetchSemanticsNode().touchBoundsInRoot
        val min = 40.dp.value * density
        assertTrue("touch ${bounds.width}x${bounds.height} < $min", bounds.width >= min && bounds.height >= min)
    }
}
