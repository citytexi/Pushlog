package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

/**
 * 카드번호 입력의 상태. [values]는 4칸이다.
 *
 * [Saver]는 가린 칸까지 전부 저장한다. 저장된 모양(형식 이름, 칸 수)이 맞지 않으면 복원하지 않고
 * null을 돌려줘 기본 상태로 시작한다. 값은 예외 메시지에 싣지 않는다.
 */
class PushlogCardNumberState(
    val maskedDigits: PushlogCardNumberMaskedDigits = PushlogCardNumberMaskedDigits.EIGHT,
) {
    internal val fields = SegmentedTextFieldStates(maskedDigits.slots.filterIsInstance<SegmentSlot.Input>().map { it.length })

    val values: List<String> get() = fields.values

    fun clear() = fields.clear()

    /** 값을 싣지 않는다. */
    override fun toString(): String = "PushlogCardNumberState(maskedDigits=$maskedDigits)"

    companion object {
        /** [maskedDigits] 이름과 모든 칸 값을 저장한다. */
        val Saver: Saver<PushlogCardNumberState, Any> =
            listSaver(
                save = { listOf(it.maskedDigits.name) + it.values },
                restore = { saved ->
                    val masked = PushlogCardNumberMaskedDigits.entries.firstOrNull { it.name == saved.firstOrNull() }
                    masked
                        ?.let { PushlogCardNumberState(it) }
                        ?.takeIf { it.fields.fields.size == saved.size - 1 }
                        ?.also { it.fields.setAll(saved.drop(1)) }
                },
            )
    }
}
