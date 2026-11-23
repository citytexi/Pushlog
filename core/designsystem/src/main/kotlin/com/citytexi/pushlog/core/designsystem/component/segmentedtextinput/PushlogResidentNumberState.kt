package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

/**
 * 주민등록번호 입력의 상태. [values]는 [digits]가 SIX면 [생년월일], 그 밖에는 [생년월일, 뒷자리].
 *
 * [Saver]는 가린 칸까지 전부 저장한다. 저장된 모양(형식 이름, 칸 수)이 맞지 않으면 복원하지 않고
 * null을 돌려줘 기본 상태로 시작한다. 값은 예외 메시지에 싣지 않는다.
 */
class PushlogResidentNumberState(
    val digits: PushlogResidentNumberDigits = PushlogResidentNumberDigits.SEVEN,
) {
    internal val fields = SegmentedTextFieldStates(digits.slots.filterIsInstance<SegmentSlot.Input>().map { it.length })

    val values: List<String> get() = fields.values

    fun clear() = fields.clear()

    /** 값을 싣지 않는다. */
    override fun toString(): String = "PushlogResidentNumberState(digits=$digits)"

    companion object {
        /** [digits] 이름과 모든 칸 값을 저장한다. */
        val Saver: Saver<PushlogResidentNumberState, Any> =
            listSaver(
                save = { listOf(it.digits.name) + it.values },
                restore = { saved ->
                    val digits = PushlogResidentNumberDigits.entries.firstOrNull { it.name == saved.firstOrNull() }
                    digits
                        ?.let { PushlogResidentNumberState(it) }
                        ?.takeIf { it.fields.fields.size == saved.size - 1 }
                        ?.also { it.fields.setAll(saved.drop(1)) }
                },
            )
    }
}
