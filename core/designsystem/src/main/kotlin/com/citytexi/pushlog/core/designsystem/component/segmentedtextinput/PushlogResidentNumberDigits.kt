package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

private const val SEPARATOR = " - "
private const val BIRTH_LABEL = "생년월일 6자리"
private const val BIRTH_LENGTH = 6
private const val BACK_LENGTH = 7

private val birthSlot =
    SegmentSlot.Input(
        length = BIRTH_LENGTH,
        label = BIRTH_LABEL,
        placeholder = BIRTH_LABEL,
        autofill = SegmentAutofill.BIRTH_DATE,
        fill = true,
    )

/** 주민등록번호 입력에서 사용자가 직접 치는 자리. 기본은 [SEVEN]. */
enum class PushlogResidentNumberDigits {
    /** 생년월일 6자리만 입력하고 뒷자리는 점으로 보여 준다. */
    SIX,

    /** 생년월일 6자리와 뒷자리 첫 자리. 나머지 6자리는 점으로 보여 준다. */
    SEVEN,

    /** 생년월일 6자리와 뒷자리 7자리. 뒷자리는 가린다. */
    THIRTEEN,
    ;

    internal val slots: List<SegmentSlot>
        get() =
            when (this) {
                SIX -> listOf(birthSlot, SegmentSlot.Separator(SEPARATOR), SegmentSlot.StaticDots(BACK_LENGTH, fill = true))
                SEVEN ->
                    listOf(
                        birthSlot,
                        SegmentSlot.Separator(SEPARATOR),
                        SegmentSlot.Input(
                            length = 1,
                            label = "주민등록번호 뒷자리 첫 번째 자리",
                            placeholderDots = 1,
                            staticSuffixDots = BACK_LENGTH - 1,
                            fill = true,
                            centered = true,
                        ),
                    )
                THIRTEEN ->
                    listOf(
                        birthSlot,
                        SegmentSlot.Separator(SEPARATOR),
                        SegmentSlot.Input(
                            length = BACK_LENGTH,
                            label = "주민등록번호 뒷자리 7자리",
                            masked = true,
                            fill = true,
                            centered = true,
                        ),
                    )
            }
}
