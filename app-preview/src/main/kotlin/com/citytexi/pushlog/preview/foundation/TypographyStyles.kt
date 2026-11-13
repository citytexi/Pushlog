package com.citytexi.pushlog.preview.foundation

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** Every named text style of [PushlogTheme.typography], in declaration order. */
@Composable
fun allTypographyStyles(): List<Pair<String, TextStyle>> {
    val t = PushlogTheme.typography
    return listOf(
        "screenTitle" to t.screenTitle,
        "articleBody" to t.articleBody,
        "articleNote" to t.articleNote,
        "t1Regular" to t.t1Regular,
        "t1Medium" to t.t1Medium,
        "t1Bold" to t.t1Bold,
        "t2Regular" to t.t2Regular,
        "t2Medium" to t.t2Medium,
        "t2Bold" to t.t2Bold,
        "t3Regular" to t.t3Regular,
        "t3Medium" to t.t3Medium,
        "t3Bold" to t.t3Bold,
        "t4Regular" to t.t4Regular,
        "t4Medium" to t.t4Medium,
        "t4Bold" to t.t4Bold,
        "t5Regular" to t.t5Regular,
        "t5Medium" to t.t5Medium,
        "t5Bold" to t.t5Bold,
        "t6Regular" to t.t6Regular,
        "t6Medium" to t.t6Medium,
        "t6Bold" to t.t6Bold,
        "t7Regular" to t.t7Regular,
        "t7Medium" to t.t7Medium,
        "t7Bold" to t.t7Bold,
        "t8Regular" to t.t8Regular,
        "t8Medium" to t.t8Medium,
        "t8Bold" to t.t8Bold,
        "t9Regular" to t.t9Regular,
        "t9Medium" to t.t9Medium,
        "t9Bold" to t.t9Bold,
        "t10Regular" to t.t10Regular,
        "t10Medium" to t.t10Medium,
        "t10Bold" to t.t10Bold,
        "t11Regular" to t.t11Regular,
        "t11Medium" to t.t11Medium,
        "t11Bold" to t.t11Bold,
        "t12Regular" to t.t12Regular,
        "t12Medium" to t.t12Medium,
        "t12Bold" to t.t12Bold,
        "t13Regular" to t.t13Regular,
        "t13Medium" to t.t13Medium,
        "t13Bold" to t.t13Bold,
        "t14Regular" to t.t14Regular,
        "t14Medium" to t.t14Medium,
        "t14Bold" to t.t14Bold,
    )
}
