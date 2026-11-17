package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

/** 문자소 [max]개를 넘으면 뒤를 지운다. 붙여넣기도 문자소 경계에서 잘린다. */
internal class GraphemeLimitTransformation(
    private val max: Int,
) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        // 문자소는 char보다 많을 수 없어 길이가 한도 이하면 셀 필요가 없다.
        if (length <= max) return
        val text = asCharSequence()
        if (text.graphemeCount() > max) replace(text.graphemeEndIndex(max), length, "")
    }
}
