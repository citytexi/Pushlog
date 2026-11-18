package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

/**
 * 문자소 [max]개를 넘기는 입력을 막는다. 편집 전에 있던 글자는 지우지 않고 넣은 부분만 줄인다.
 * 글자 수가 늘지 않는 편집(지우기, 같은 길이 바꾸기)은 한도를 넘은 값에서도 그대로 둔다. 문자소 경계에서만 자른다.
 */
internal class GraphemeLimitTransformation(
    private val max: Int,
) : InputTransformation {
    init {
        require(max >= 0) { "max must not be negative" }
    }

    override fun TextFieldBuffer.transformInput() {
        // 문자소는 char보다 많을 수 없어 길이가 한도 이하면 셀 필요가 없다.
        if (length > max) limitGrowth(asCharSequence())
    }

    private fun TextFieldBuffer.limitGrowth(text: CharSequence) {
        val count = text.graphemeCount()
        if (count <= max) return
        val original = originalText
        val originalCount = original.graphemeCount()
        if (count > originalCount) trimInserted(text, original, maxOf(max, originalCount))
    }

    /**
     * 앞뒤로 원본과 겹치는 부분을 빼고 남은 구간이 넣은 글자다. 넣은 글자는 [limit]까지만 남긴다.
     * 원본이 이미 한도를 넘었으면 [limit]은 원본 글자 수라서, 선택을 바꿔 쓸 때 지운 만큼은 넣을 수 있다.
     */
    private fun TextFieldBuffer.trimInserted(
        text: CharSequence,
        original: CharSequence,
        limit: Int,
    ) {
        val start = text.commonPrefixWith(original).length
        val suffix = text.commonSuffixWith(original).length.coerceAtMost(minOf(text.length, original.length) - start)
        val end = text.length - suffix
        val rest = text.subSequence(0, start).toString() + text.subSequence(end, text.length)
        val room = (limit - rest.graphemeCount()).coerceAtLeast(0)
        replace(start + text.subSequence(start, end).graphemeEndIndex(room), end, "")
    }
}
