package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

/** segmented 입력의 한 줄을 이루는 칸 명세. */
internal sealed interface SegmentSlot {
    /** 입력 칸. [label]은 접근성 이름, [placeholderDots]는 빈 칸에 그리는 점 수(글자 placeholder와 함께 쓰지 않음). */
    data class Input(
        val length: Int,
        val label: String,
        val masked: Boolean = false,
        val placeholder: String? = null,
        val placeholderDots: Int = 0,
        val staticSuffixDots: Int = 0,
        val autofill: SegmentAutofill = SegmentAutofill.NONE,
        /** true면 남는 너비를 나눠 가진다(SEED sizing="fill"). false면 자리 수만큼. */
        val fill: Boolean = false,
        /** SEED align="center". */
        val centered: Boolean = false,
    ) : SegmentSlot {
        init {
            // 가린 칸이 자동완성 대상이 되면 서비스가 가린 값을 읽거나 저장 제안에 담는다.
            require(!masked || autofill == SegmentAutofill.NONE) { "masked segment must not be autofilled" }
        }
    }

    data class StaticDots(
        val count: Int,
        val fill: Boolean = false,
    ) : SegmentSlot

    data class Separator(
        val text: String,
    ) : SegmentSlot
}
