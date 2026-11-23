package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInputVariant
import com.citytexi.pushlog.core.designsystem.component.textinput.TextInputMetrics

/** 상자 모양과 글자는 OUTLINE 한 줄 입력과 같다. OUTLINE을 바꾸면 따라 바뀐다. */
@Composable
internal fun segmentMetrics(): TextInputMetrics = PushlogTextInputVariant.OUTLINE.metrics(multiline = false)

/** 자리 수 너비 칸도 weight(fill = false)로 둬, 큰 글자에서 줄이 모자라면 구분자·suffix 대신 칸이 줄어든다. */
@Composable
internal fun RowScope.segmentWidth(slot: SegmentSlot.Input): Modifier =
    if (slot.fill) Modifier.weight(1f) else Modifier.weight(1f, fill = false).width(digitsWidth(slot.length))

/** 자리 수만큼의 "0" 너비. 숫자 글꼴 너비가 글자마다 달라도 칸이 값에 따라 흔들리지 않는다. */
@Composable
internal fun digitsWidth(length: Int): Dp {
    val measurer = rememberTextMeasurer()
    val style = segmentMetrics().textStyle
    val density = LocalDensity.current
    return remember(length, style, density) {
        with(density) {
            measurer
                .measure("0".repeat(length), style)
                .size.width
                .toDp()
        }
    }
}
