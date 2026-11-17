package com.citytexi.pushlog.core.designsystem.component.textinput

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GraphemesTest {
    @Test
    fun countsHangulAndEmojiClustersAsOne() {
        assertEquals(2, "한글".graphemeCount())
        assertEquals(1, "👨‍👩‍👧".graphemeCount())
        assertEquals(1, "🇰🇷".graphemeCount())
        assertEquals(1, "é".graphemeCount())
    }

    @Test
    fun endIndexStopsAtClusterBoundary() {
        assertEquals("a👨‍👩‍👧".length, "a👨‍👩‍👧b".graphemeEndIndex(2))
    }

    @Test
    fun endIndexClampsToLengthWhenShort() {
        assertEquals("ab".length, "ab".graphemeEndIndex(5))
    }
}
