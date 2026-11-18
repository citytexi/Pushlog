package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckbox
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckboxGroup
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadio
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadioGroup
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBox
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBoxGroup
import com.citytexi.pushlog.core.designsystem.component.selectbox.PushlogSelectBoxType
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInput
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
class PushlogFieldSelectionGroupTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var clicks = 0

    @Composable
    private fun Checkbox(enabled: Boolean? = null) {
        val onClick: () -> Unit = { clicks++ }
        if (enabled == null) {
            PushlogCheckbox(ToggleableState.Off, onClick, "관심", Modifier.testTag(CONTROL))
        } else {
            PushlogCheckbox(ToggleableState.Off, onClick, "관심", Modifier.testTag(CONTROL), enabled = enabled)
        }
    }

    private fun tapCount() {
        composeRule.onNodeWithTag(CONTROL).performClick()
    }

    @Test
    fun disabledFieldDisablesCheckboxGroupChildren() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(enabled = false) { PushlogCheckboxGroup { Checkbox() } }
            }
        }
        tapCount()
        composeRule.onNodeWithTag(CONTROL).assertIsNotEnabled()
        assertEquals(0, clicks)
    }

    @Test
    fun disabledFieldDisablesRadioGroupChildren() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(enabled = false) {
                    PushlogRadioGroup { PushlogRadio(false, { clicks++ }, "최신순", Modifier.testTag(CONTROL)) }
                }
            }
        }
        tapCount()
        composeRule.onNodeWithTag(CONTROL).assertIsNotEnabled()
        assertEquals(0, clicks)
    }

    @Test
    fun disabledFieldDisablesSelectBoxGroupChildren() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(enabled = false) {
                    PushlogSelectBoxGroup {
                        PushlogSelectBox(false, { clicks++ }, PushlogSelectBoxType.SINGLE, "아침", Modifier.testTag(CONTROL))
                    }
                }
            }
        }
        tapCount()
        composeRule.onNodeWithTag(CONTROL).assertIsNotEnabled()
        assertEquals(0, clicks)
    }

    @Test
    fun childArgumentOverridesGroupDefault() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(enabled = false) { PushlogCheckboxGroup { Checkbox(enabled = true) } }
            }
        }
        tapCount()
        assertEquals(1, clicks)
    }

    @Test
    fun groupCarriesFieldLabelAndError() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(label = "관심사", invalid = true, errorMessage = "하나 이상 고르세요") {
                    PushlogRadioGroup(Modifier.testTag(GROUP)) {
                        PushlogRadio(false, {}, "최신순")
                    }
                }
            }
        }
        val config = composeRule.onNodeWithTag(GROUP).fetchSemanticsNode().config
        assertEquals(listOf("관심사"), config.getOrNull(SemanticsProperties.ContentDescription))
        assertEquals("하나 이상 고르세요", config.getOrNull(SemanticsProperties.Error))
    }

    @Test
    fun groupCarriesRequiredIndicatorAndDescription() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(label = "관심사", showRequiredIndicator = true, indicatorText = "선택", description = "하나만") {
                    PushlogCheckboxGroup(Modifier.testTag(GROUP)) { Checkbox() }
                }
            }
        }
        val state = composeRule.onNodeWithTag(GROUP).fetchSemanticsNode().config[SemanticsProperties.StateDescription]
        listOf("필수", "선택", "하나만").forEach { assertTrue("$it missing", state.contains(it)) }
    }

    @Test
    fun bareCheckboxInFieldIgnoresFieldState() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(enabled = false) { Checkbox() }
            }
        }
        tapCount()
        assertEquals(1, clicks)
    }

    @Test
    fun footerInputDoesNotInheritGroupField() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(invalid = true, enabled = false, label = "방식", errorMessage = "오류") {
                    PushlogSelectBoxGroup {
                        PushlogSelectBox(
                            selected = true,
                            onClick = {},
                            type = PushlogSelectBoxType.SINGLE,
                            label = "기타",
                            enabled = true,
                            footer = { PushlogTextInput(TextFieldState(), Modifier.testTag(ETC)) },
                        )
                    }
                }
            }
        }
        composeRule.onNodeWithTag(ETC).assertIsEnabled()
        val config = composeRule.onNodeWithTag(ETC).fetchSemanticsNode().config
        assertEquals(null, config.getOrNull(SemanticsProperties.Error))
        assertEquals(null, config.getOrNull(SemanticsProperties.ContentDescription))
        assertEquals(null, config.getOrNull(SemanticsProperties.Text))
    }

    @Test
    fun nestedFieldUsesInnerState() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(invalid = true, errorMessage = "바깥") {
                    PushlogField(invalid = false) {
                        PushlogTextInput(TextFieldState(), Modifier.testTag(ETC))
                    }
                }
            }
        }
        val config = composeRule.onNodeWithTag(ETC).fetchSemanticsNode().config
        assertEquals(null, config.getOrNull(SemanticsProperties.Error))
    }

    private companion object {
        const val CONTROL = "control"
        const val GROUP = "group"
        const val ETC = "etc"
    }
}
