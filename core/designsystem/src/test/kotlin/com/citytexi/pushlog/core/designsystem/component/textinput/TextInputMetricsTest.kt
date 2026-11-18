package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.ui.test.junit4.createComposeRule
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TextInputMetricsTest {
    @get:Rule
    val composeRule = createComposeRule()

    // 같은 값이면 같다고 봐야 재구성 때 새로 만든 메트릭이 상자 재구성을 일으키지 않는다.
    @Test
    fun metricsWithSameInputsAreEqual() {
        val results = mutableListOf<TextInputMetrics>()
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                repeat(2) { results += PushlogTextInputVariant.OUTLINE.metrics(multiline = false) }
            }
        }
        composeRule.waitForIdle()
        assertEquals(results[0], results[1])
    }
}
