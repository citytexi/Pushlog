package com.citytexi.pushlog.core.designsystem.component.textinput

import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GraphemeLimitTransformationTest {
    @Test
    fun negativeMaxIsRejected() {
        assertThrows(IllegalArgumentException::class.java) { GraphemeLimitTransformation(-1) }
    }
}
