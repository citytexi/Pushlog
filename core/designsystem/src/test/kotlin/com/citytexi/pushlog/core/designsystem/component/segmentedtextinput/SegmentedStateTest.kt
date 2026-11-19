package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SegmentedStateTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun toStringHasNoDigits() {
        val resident =
            PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN)
                .also { it.fields.setAll(listOf("900101", "1234567")) }
        val card =
            PushlogCardNumberState()
                .also { it.fields.setAll(listOf("1234", "5678", "9012", "3456")) }
        assertFalse(resident.toString().any(Char::isDigit))
        assertFalse(card.toString().any(Char::isDigit))
        assertEquals("PushlogResidentNumberState(digits=THIRTEEN)", resident.toString())
        assertEquals("PushlogCardNumberState(maskedDigits=EIGHT)", card.toString())
    }

    @Test
    fun restoresAllValuesIncludingMasked() {
        val tester = StateRestorationTester(rule)
        lateinit var card: PushlogCardNumberState
        lateinit var resident: PushlogResidentNumberState
        tester.setContent {
            card = rememberSaveable(saver = PushlogCardNumberState.Saver) { PushlogCardNumberState(PushlogCardNumberMaskedDigits.FOUR) }
            resident =
                rememberSaveable(saver = PushlogResidentNumberState.Saver) {
                    PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN)
                }
        }
        rule.runOnIdle {
            card.fields.setAll(listOf("1234", "5678", "9012", "3456"))
            resident.fields.setAll(listOf("900101", "1234567"))
        }
        tester.emulateSavedInstanceStateRestore()
        rule.runOnIdle {
            assertEquals(listOf("1234", "5678", "9012", "3456"), card.values)
            assertEquals(PushlogCardNumberMaskedDigits.FOUR, card.maskedDigits)
            assertEquals(listOf("900101", "1234567"), resident.values)
            assertEquals(PushlogResidentNumberDigits.THIRTEEN, resident.digits)
        }
    }

    @Test
    fun restoreFallsBackOnBadShape() {
        with(PushlogCardNumberState.Saver) {
            assertNull(restore(listOf("NOPE", "1", "2", "3", "4")))
            assertNull(restore(listOf("EIGHT", "1")))
            assertNull(restore(emptyList<Any>()))
        }
        assertNull(PushlogResidentNumberState.Saver.restore(listOf("SIX", "1", "2")))
    }

    @Test
    fun clearEmptiesAll() {
        val card = PushlogCardNumberState().also { it.fields.setAll(listOf("1234", "5678", "9012", "3456")) }
        card.clear()
        assertEquals(listOf("", "", "", ""), card.values)
        val resident = PushlogResidentNumberState().also { it.fields.setAll(listOf("900101", "1")) }
        resident.clear()
        assertEquals(listOf("", ""), resident.values)
    }

    @Test
    fun valuesShapeFollowsDigits() {
        assertEquals(1, PushlogResidentNumberState(PushlogResidentNumberDigits.SIX).values.size)
        assertEquals(2, PushlogResidentNumberState(PushlogResidentNumberDigits.SEVEN).values.size)
        assertEquals(2, PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN).values.size)
        assertEquals(4, PushlogCardNumberState().values.size)
    }

    @Test
    fun slotLengthsMatchState() {
        PushlogResidentNumberDigits.entries.forEach {
            assertEquals(it.slots.filterIsInstance<SegmentSlot.Input>().size, PushlogResidentNumberState(it).values.size)
        }
        PushlogCardNumberMaskedDigits.entries.forEach {
            assertEquals(4, it.slots.filterIsInstance<SegmentSlot.Input>().size)
        }
    }
}
