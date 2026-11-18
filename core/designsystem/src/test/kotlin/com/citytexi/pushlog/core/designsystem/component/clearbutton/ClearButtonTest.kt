package com.citytexi.pushlog.core.designsystem.component.clearbutton

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.center
import androidx.compose.ui.test.click
import androidx.compose.ui.test.down
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ClearButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var clicks = 0

    private fun setButton(touchPadding: androidx.compose.ui.unit.Dp = 0.dp) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                ClearButton(
                    onClick = { clicks++ },
                    contentDescription = "지우기",
                    iconSize = 20.dp,
                    color = Color.Black,
                    modifier = Modifier.testTag(TAG),
                    touchPadding = touchPadding,
                )
            }
        }
    }

    @Test
    fun clickCallsOnClickOnce() {
        setButton()
        composeRule.onNodeWithTag(TAG).performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun hasButtonRoleAndLabel() {
        setButton()
        val config = composeRule.onNodeWithTag(TAG).fetchSemanticsNode().config
        assertEquals(Role.Button, config[SemanticsProperties.Role])
        assertEquals(listOf("지우기"), config[SemanticsProperties.ContentDescription])
    }

    @Test
    fun touchPaddingEnlargesHitArea() {
        setButton(touchPadding = 12.dp)
        composeRule.onNodeWithTag(TAG).assertWidthIsEqualTo(44.dp).assertHeightIsEqualTo(44.dp)
        val d = composeRule.density.density
        composeRule.onNodeWithTag(TAG).performTouchInput { click(Offset(2 * d, 2 * d)) }
        assertEquals(1, clicks)
    }

    @Test
    fun pressShrinksSelf() {
        setButton()
        val rest = markWidth()
        composeRule.onNodeWithTag(TAG).performTouchInput { down(center) }
        composeRule.waitForIdle()
        val pressed = markWidth()
        assertTrue("pressed $pressed should be < rest $rest", pressed < rest)
    }

    private fun markWidth(): Int {
        val image: PixelMap = composeRule.onNodeWithTag(TAG).captureToImage().toPixelMap()
        val black = Color.Black.toArgb()
        val row = image.height / 2
        return (0 until image.width).count { x -> image[x, row].toArgb() == black }
    }

    private companion object {
        const val TAG = "clear"
    }
}
