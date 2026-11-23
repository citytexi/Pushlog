package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.TextRange

/** 입력 칸 하나의 자리와 이웃 칸. 하드웨어 키로 칸 사이를 오가는 규칙을 맡는다. */
internal class SegmentPosition(
    val index: Int,
    private val inputs: List<SegmentSlot.Input>,
    private val states: SegmentedTextFieldStates,
    val requesters: List<FocusRequester>,
) {
    val isLast: Boolean get() = index == inputs.lastIndex

    /**
     * 빈 칸의 Backspace는 앞 칸 마지막 글자를 지우고 앞 칸으로 간다. 끝의 →는 다음 칸 처음(가린 칸이면 끝)으로,
     * 처음의 ←는 앞 칸 끝으로 간다. 가린 칸은 커서가 끝에만 있으니 ←·→가 늘 옆 칸으로 간다. 처리했으면 true.
     */
    fun handleKey(key: Key): Boolean =
        when (key) {
            Key.Backspace -> deletePrevious()
            Key.DirectionRight -> moveNext()
            Key.DirectionLeft -> movePrevious()
            else -> false
        }

    private fun deletePrevious(): Boolean {
        if (states.fields[index].text.isNotEmpty() || index == 0) return false
        states.fields[index - 1].edit {
            if (length > 0) replace(length - 1, length, "")
            placeCursorAtEnd()
        }
        requesters[index - 1].requestFocus()
        return true
    }

    private fun moveNext(): Boolean {
        val state = states.fields[index]
        if (isLast || !(inputs[index].masked || state.selection == TextRange(state.text.length))) return false
        val nextMasked = inputs[index + 1].masked
        states.fields[index + 1].edit { if (nextMasked) placeCursorAtEnd() else selection = TextRange(0) }
        requesters[index + 1].requestFocus()
        return true
    }

    private fun movePrevious(): Boolean {
        if (index == 0 || !(inputs[index].masked || states.fields[index].selection == TextRange(0))) return false
        states.fields[index - 1].edit { placeCursorAtEnd() }
        requesters[index - 1].requestFocus()
        return true
    }
}
