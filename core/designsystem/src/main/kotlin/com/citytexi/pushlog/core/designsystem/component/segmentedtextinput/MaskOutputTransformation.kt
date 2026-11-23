package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

private const val MASK = "•"

/** 글자마다 '•'로 바꾼다. 한 글자씩 바꿔 길이가 같으니 커서 위치가 그대로 맞는다. 통째로 바꾸면 커서가 범위 끝으로 몰린다. */
internal object MaskOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        for (index in 0 until length) replace(index, index + 1, MASK)
    }
}
