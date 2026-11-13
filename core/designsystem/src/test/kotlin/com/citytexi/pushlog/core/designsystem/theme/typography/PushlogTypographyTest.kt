package com.citytexi.pushlog.core.designsystem.theme.typography

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

class PushlogTypographyTest {
    private fun assertStyle(
        style: TextStyle,
        size: TextUnit,
        lineHeight: TextUnit,
        weight: FontWeight,
    ) {
        assertEquals(size, style.fontSize)
        assertEquals(lineHeight, style.lineHeight)
        assertEquals(weight, style.fontWeight)
    }

    @Test fun t5Bold() = assertStyle(DefaultPushlogTypography.t5Bold, 16.sp, 22.sp, FontWeight.W700)

    @Test fun t14Regular() = assertStyle(DefaultPushlogTypography.t14Regular, 48.sp, 60.sp, FontWeight.W400)

    @Test fun screenTitle() = assertStyle(DefaultPushlogTypography.screenTitle, 26.sp, 35.sp, FontWeight.W700)

    @Test fun articleBody() = assertStyle(DefaultPushlogTypography.articleBody, 16.sp, 24.sp, FontWeight.W400)

    @Test fun articleNote() = assertStyle(DefaultPushlogTypography.articleNote, 14.sp, 22.sp, FontWeight.W400)

    @Test
    fun allUsePretendard() {
        val t = DefaultPushlogTypography
        val all =
            listOf(
                t.screenTitle,
                t.articleBody,
                t.articleNote,
                t.t1Regular,
                t.t1Medium,
                t.t1Bold,
                t.t2Regular,
                t.t2Medium,
                t.t2Bold,
                t.t3Regular,
                t.t3Medium,
                t.t3Bold,
                t.t4Regular,
                t.t4Medium,
                t.t4Bold,
                t.t5Regular,
                t.t5Medium,
                t.t5Bold,
                t.t6Regular,
                t.t6Medium,
                t.t6Bold,
                t.t7Regular,
                t.t7Medium,
                t.t7Bold,
                t.t8Regular,
                t.t8Medium,
                t.t8Bold,
                t.t9Regular,
                t.t9Medium,
                t.t9Bold,
                t.t10Regular,
                t.t10Medium,
                t.t10Bold,
                t.t11Regular,
                t.t11Medium,
                t.t11Bold,
                t.t12Regular,
                t.t12Medium,
                t.t12Bold,
                t.t13Regular,
                t.t13Medium,
                t.t13Bold,
                t.t14Regular,
                t.t14Medium,
                t.t14Bold,
            )
        assertEquals(45, all.size)
        all.forEach { assertEquals(PretendardFontFamily, it.fontFamily) }
    }
}
