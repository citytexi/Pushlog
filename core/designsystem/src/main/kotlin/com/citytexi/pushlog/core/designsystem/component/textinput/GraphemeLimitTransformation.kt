package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

/**
 * 문자소 [max]개를 넘기는 입력을 막는다. 편집 전에 있던 글자는 지우지 않고 넣은 부분만 줄인다.
 * 이미 한도에 찬 상태에서 늘리는 편집은 통째로 되돌린다. 문자소 경계에서만 자른다.
 */
internal class GraphemeLimitTransformation(
    private val max: Int,
) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        // 문자소는 char보다 많을 수 없어 길이가 한도 이하면 셀 필요가 없다.
        if (length <= max) return
        val text = asCharSequence()
        if (text.graphemeCount() <= max) return
        val original = originalText
        if (original.graphemeCount() >= max) {
            revertAllChanges()
        } else {
            trimInserted(text, original)
        }
    }

    /** 앞뒤로 원본과 겹치는 부분을 빼고 남은 구간이 넣은 글자다. */
    private fun TextFieldBuffer.trimInserted(
        text: CharSequence,
        original: CharSequence,
    ) {
        val start = text.commonPrefixWith(original).length
        val suffix = text.commonSuffixWith(original).length.coerceAtMost(minOf(text.length, original.length) - start)
        val end = text.length - suffix
        val rest = text.subSequence(0, start).toString() + text.subSequence(end, text.length)
        val room = (max - rest.graphemeCount()).coerceAtLeast(0)
        replace(start + text.subSequence(start, end).graphemeEndIndex(room), end, "")
    }
}
