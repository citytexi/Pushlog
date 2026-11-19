package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class SegmentRulesTest {
    private val lengths = listOf(4, 4, 4, 4)
    private val masked = listOf(false, false, true, true)
    private val empty = List(4) { "" }

    @Test
    fun nonDigitsAreDropped() {
        assertEquals("1234", digitsOnly("12-3 4가"))
    }

    @Test
    fun birthDateFormats() {
        assertEquals("900101", normalizeBirthDate("1990-01-01"))
        assertEquals("900101", normalizeBirthDate("1990.01.01"))
        assertEquals("900101", normalizeBirthDate("1990/01/01"))
        assertEquals("051231", normalizeBirthDate("20051231"))
        assertEquals("051231", normalizeBirthDate(" 20051231 "))
        assertNull(normalizeBirthDate("1990-13-01"))
        assertNull(normalizeBirthDate("1990-00-10"))
        assertNull(normalizeBirthDate("1990-01-32"))
        assertNull(normalizeBirthDate("900101"))
    }

    @Test
    fun pasteFillsFromStartAndStopsBeforeMasked() {
        assertEquals(
            listOf("1234", "5678", "", ""),
            distribute("1234567890123456", 0, empty, lengths, masked),
        )
    }

    @Test
    fun pasteIntoMiddleSegmentFillsFromThere() {
        assertEquals(
            listOf("9999", "1234", "", ""),
            distribute("12345678", 1, listOf("9999", "", "", ""), lengths, masked),
        )
    }

    @Test
    fun pasteStartingAtMaskedChangesNothing() {
        val current = listOf("1234", "5678", "", "")
        assertEquals(current, distribute("1111", 2, current, lengths, masked))
    }

    @Test
    fun residentThirteenStopsBeforeMaskedBack() {
        assertEquals(
            listOf("900101", ""),
            distribute("9001011234567", 0, listOf("", ""), listOf(6, 7), listOf(false, true)),
        )
    }

    @Test
    fun residentSevenFillsFirstBackDigit() {
        assertEquals(
            listOf("900101", "1"),
            distribute("9001011234567", 0, listOf("", ""), listOf(6, 1), listOf(false, false)),
        )
    }

    @Test
    fun shortPasteFillsPartiallyAndLeavesLaterCells() {
        assertEquals(
            listOf("1234", "56", "", ""),
            distribute("123456", 0, empty, lengths, masked),
        )
        assertEquals(
            listOf("12", "5678", "", ""),
            distribute("12", 0, listOf("9999", "5678", "", ""), lengths, masked),
        )
    }

    @Test
    fun emptyDigitsChangeNothing() {
        val current = listOf("12", "", "", "")
        assertEquals(current, distribute("", 0, current, lengths, masked))
    }

    @Test
    fun outOfRangeStartChangesNothing() {
        val current = listOf("12", "", "", "")
        assertEquals(current, distribute("1111", -1, current, lengths, masked))
        assertEquals(current, distribute("1111", 4, current, lengths, masked))
    }

    @Test
    fun mismatchedListSizesAreRejectedWithoutLeakingValues() {
        val error =
            assertThrows(IllegalArgumentException::class.java) {
                distribute("1234", 0, listOf("55"), lengths, masked)
            }
        assertEquals(false, error.message.orEmpty().contains("55"))
        assertEquals(false, error.message.orEmpty().contains("1234"))
    }
}
