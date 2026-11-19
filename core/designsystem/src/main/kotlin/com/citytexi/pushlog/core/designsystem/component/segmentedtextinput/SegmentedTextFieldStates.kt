package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd

/** 입력 칸마다 [TextFieldState] 하나를 갖는 상태. 칸 수와 자리 수는 [lengths]가 정한다. */
internal class SegmentedTextFieldStates(
    val lengths: List<Int>,
    initial: List<String> = List(lengths.size) { "" },
) {
    init {
        require(initial.size == lengths.size) { "initial size does not match lengths size" }
    }

    val fields: List<TextFieldState> = initial.map { TextFieldState(it) }

    /** 칸 순서대로 읽은 값. 스냅샷 읽기라 Compose가 변화를 추적한다. */
    val values: List<String> get() = fields.map { it.text.toString() }

    fun clear() {
        fields.forEach { it.clearText() }
    }

    /** 칸 수가 다르면 [IllegalArgumentException]을 던진다. 메시지에는 값을 싣지 않는다. */
    fun setAll(values: List<String>) {
        require(values.size == fields.size) { "values size does not match fields size" }
        fields.forEachIndexed { index, field -> field.setTextAndPlaceCursorAtEnd(values[index]) }
    }

    /** 값이 바뀐 칸만 쓴다. 그대로인 칸은 선택 구간과 되돌리기 기록이 남는다. */
    fun setChanged(values: List<String>) {
        require(values.size == fields.size) { "values size does not match fields size" }
        fields.forEachIndexed { index, field ->
            if (field.text.toString() != values[index]) field.setTextAndPlaceCursorAtEnd(values[index])
        }
    }
}
