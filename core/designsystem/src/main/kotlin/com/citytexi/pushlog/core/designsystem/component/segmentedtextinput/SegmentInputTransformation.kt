package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.text.TextRange

/**
 * 숫자만 남기고 이 칸 몫만 남긴다. 한 번에 두 글자 이상 들어오면(붙여넣기·자동완성) 이 칸 내용을 버리고
 * 들어온 숫자로 처음부터 채운 뒤, 남은 숫자(없으면 빈 문자열)를 [onOverflow]로 넘긴다. 생년월일 칸은 날짜 형식이면 먼저 6자리로 바꾼다.
 * 가운데에 넣어 자리 수를 넘기는 한 글자 입력은 되돌린다. 가린 칸은 끝에서만 입력·삭제하고 두 글자 이상 입력은 되돌린다.
 *
 * [onOverflow]는 편집 도중에 불린다. 다른 칸 상태를 여기서 바로 바꾸지 말고 나중에 처리한다.
 */
internal class SegmentInputTransformation(
    private val slot: SegmentSlot.Input,
    private val onOverflow: (String) -> Unit,
) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        val inserted = insertedText()
        if (slot.masked) transformMasked(inserted) else transformVisible(inserted)
    }

    private fun TextFieldBuffer.transformVisible(inserted: String) {
        if (inserted.length >= 2) {
            val digits = normalizeBirthDate(inserted).takeIf { slot.autofill == SegmentAutofill.BIRTH_DATE } ?: digitsOnly(inserted)
            if (digits.isEmpty()) {
                revertAllChanges()
                return
            }
            val own = digits.take(slot.length)
            replace(0, length, own)
            selection = TextRange(own.length)
            onOverflow(digits.drop(slot.length))
            return
        }
        if (length > slot.length || !asCharSequence().all(::isDigit)) revertAllChanges()
    }

    /** 커서 위치와 상관없이 한 글자 입력은 끝에 붙이고, 지운 글자 수만큼 끝에서 지운다. */
    private fun TextFieldBuffer.transformMasked(inserted: String) {
        val original = originalText.toString()
        val result =
            when {
                inserted.isEmpty() -> original.take(minOf(length, original.length))
                inserted.length == 1 && isDigit(inserted[0]) && length == original.length + 1 && original.length < slot.length ->
                    original + inserted
                else -> original
            }
        if (result == original) revertAllChanges() else replace(0, length, result)
        selection = TextRange(result.length)
    }

    /**
     * 이번 편집으로 들어온 글자. 앞뒤 공통부분 비교로 구하면 전체 교체(자동완성 replaceAll, 접근성 SetText)에서
     * 새 값이 기존 값으로 시작할 때 앞부분을 놓친다("4" → "4111…"이 "111…"로 잡힘). 그래서 기록된 변경 구간을 쓴다.
     */
    @OptIn(ExperimentalFoundationApi::class)
    private fun TextFieldBuffer.insertedText(): String =
        buildString {
            val text = asCharSequence()
            for (change in 0 until changes.changeCount) {
                val range = changes.getRange(change)
                append(text, range.min, range.max)
            }
        }
}

private fun isDigit(char: Char): Boolean = char in '0'..'9'
