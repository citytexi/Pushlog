package com.citytexi.pushlog.core.designsystem.theme

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.citytexi.pushlog.core.designsystem.theme.typography.PretendardFontFamily
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PretendardWeightTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun boldIsWiderThanRegular() {
        var regularWidth = 0
        var boldWidth = 0
        composeRule.setContent {
            val measurer: TextMeasurer = rememberTextMeasurer()
            regularWidth = measure(measurer, FontWeight.W400)
            boldWidth = measure(measurer, FontWeight.W700)
        }
        composeRule.waitForIdle()
        assertTrue("bold=$boldWidth regular=$regularWidth", boldWidth > regularWidth)
    }

    private fun measure(
        measurer: TextMeasurer,
        weight: FontWeight,
    ): Int =
        measurer
            .measure(
                text = "알림 보관함 Pushlog",
                style = TextStyle(fontFamily = PretendardFontFamily, fontWeight = weight, fontSize = 16.sp),
            ).size.width
}
