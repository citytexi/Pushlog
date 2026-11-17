package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PushlogFieldStateTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun config() = composeRule.onNodeWithTag(TAG).fetchSemanticsNode().config

    @Test
    fun semanticsUseLabelAsNameAndErrorOnlyWhenInvalid() {
        var invalid by mutableStateOf(true)
        val field = PushlogFieldState(label = "이름", errorMessage = "필수 항목")
        composeRule.setContent {
            Box(Modifier.testTag(TAG).pushlogFieldSemantics(field, invalid = invalid))
        }

        assertEquals(listOf("이름"), config()[SemanticsProperties.ContentDescription])
        assertEquals("필수 항목", config()[SemanticsProperties.Error])

        invalid = false
        composeRule.waitForIdle()

        assertNull(config().getOrNull(SemanticsProperties.Error))
    }

    @Test
    fun contentDescriptionArgumentWinsOverLabel() {
        val field = PushlogFieldState(label = "이름")
        composeRule.setContent {
            Box(Modifier.testTag(TAG).pushlogFieldSemantics(field, invalid = false, contentDescription = "생일"))
        }

        assertEquals(listOf("생일"), config()[SemanticsProperties.ContentDescription])
    }

    @Test
    fun descriptionBecomesStateDescription() {
        val field = PushlogFieldState(description = "숫자만")
        composeRule.setContent {
            Box(Modifier.testTag(TAG).pushlogFieldSemantics(field, invalid = false))
        }

        assertEquals("숫자만", config()[SemanticsProperties.StateDescription])
        assertNull(config().getOrNull(SemanticsProperties.ContentDescription))
    }

    @Test
    fun resetProvidesNoneBelow() {
        var seen: PushlogFieldState? = null
        composeRule.setContent {
            CompositionLocalProvider(LocalPushlogFieldState provides PushlogFieldState(invalid = true)) {
                ResetPushlogFieldState { seen = LocalPushlogFieldState.current }
            }
        }

        assertEquals(PushlogFieldState.None, seen)
    }

    private companion object {
        const val TAG = "t"
    }
}
