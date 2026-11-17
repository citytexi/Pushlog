package com.citytexi.pushlog.core.designsystem.component.pressable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogPressableAreaTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()

    @Test
    fun triStateAreaIsCheckboxWithState() {
        var clicks = 0
        setArea(Modifier.pushlogTriStateToggleArea(ToggleableState.Indeterminate, source, enabled = true) { clicks++ })
        val config = node().fetchSemanticsNode().config
        assertEquals(Role.Checkbox, config[SemanticsProperties.Role])
        assertEquals(ToggleableState.Indeterminate, config[SemanticsProperties.ToggleableState])
        node().performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun selectableAreaIsRadioButton() {
        var clicks = 0
        setArea(Modifier.pushlogSelectableArea(selected = true, source, enabled = true) { clicks++ })
        assertEquals(Role.RadioButton, node().fetchSemanticsNode().config[SemanticsProperties.Role])
        node().assertIsSelected().performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun toggleableAreaUsesGivenRole() {
        var received: Boolean? = null
        setArea(Modifier.pushlogToggleableArea(value = false, source, enabled = true, role = Role.Switch) { received = it })
        assertEquals(Role.Switch, node().fetchSemanticsNode().config[SemanticsProperties.Role])
        node().assertIsOff().performClick()
        assertEquals(true, received)
    }

    @Test
    fun disabledTriStateAreaIgnoresTaps() {
        var calls = 0
        setArea(Modifier.pushlogTriStateToggleArea(ToggleableState.Off, source, enabled = false) { calls++ })
        node().assertIsNotEnabled().performClick()
        assertEquals(0, calls)
    }

    @Test
    fun disabledSelectableAreaIgnoresTaps() {
        var calls = 0
        setArea(Modifier.pushlogSelectableArea(selected = false, source, enabled = false) { calls++ })
        node().assertIsNotEnabled().performClick()
        assertEquals(0, calls)
    }

    @Test
    fun disabledToggleableAreaIgnoresTaps() {
        var calls = 0
        setArea(Modifier.pushlogToggleableArea(value = false, source, enabled = false, role = Role.Switch) { calls++ })
        node().assertIsNotEnabled().performClick()
        assertEquals(0, calls)
    }

    @Test
    fun toggleablePressableRoleIsArgument() {
        setArea(toggleablePressable(role = Role.Switch))
        assertEquals(Role.Switch, node().fetchSemanticsNode().config[SemanticsProperties.Role])
    }

    @Test
    fun toggleablePressableRoleDefaultsToCheckbox() {
        setArea(toggleablePressable(role = null))
        assertEquals(Role.Checkbox, node().fetchSemanticsNode().config[SemanticsProperties.Role])
    }

    private fun toggleablePressable(role: Role?): Modifier =
        if (role == null) {
            Modifier.pushlogToggleablePressable(
                interactionSource = source,
                selected = false,
                color = BASE,
                pressedColor = PRESSED,
                shape = RectangleShape,
                enabled = true,
                onClick = {},
            )
        } else {
            Modifier.pushlogToggleablePressable(
                interactionSource = source,
                selected = false,
                color = BASE,
                pressedColor = PRESSED,
                shape = RectangleShape,
                enabled = true,
                onClick = {},
                role = role,
            )
        }

    private fun setArea(area: Modifier) {
        composeRule.setContent { Box(Modifier.size(40.dp).then(area)) }
        composeRule.waitForIdle()
    }

    private fun node(): SemanticsNodeInteraction = composeRule.onNode(hasClickAction())

    private companion object {
        val BASE = Color(0xFF00FF00)
        val PRESSED = Color(0xFFFF0000)
    }
}
