package com.citytexi.pushlog.core.designsystem.component.textinput

import android.icu.text.BreakIterator
import androidx.compose.foundation.text.input.TextFieldState

/** 이모지·결합 문자를 한 글자로 센 글자 수. field의 `characterCount`에 넘긴다. */
val TextFieldState.graphemeCount: Int
    get() = text.graphemeCount()

internal fun CharSequence.graphemeCount(): Int {
    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(this)
    var count = 0
    while (iterator.next() != BreakIterator.DONE) count++
    return count
}

/** 앞에서 [count]개 문자소가 끝나는 char 인덱스. 문자소가 모자라면 길이를 돌려준다. */
internal fun CharSequence.graphemeEndIndex(count: Int): Int {
    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(this)
    var end = 0
    repeat(count) {
        val next = iterator.next()
        if (next == BreakIterator.DONE) return length
        end = next
    }
    return end
}
