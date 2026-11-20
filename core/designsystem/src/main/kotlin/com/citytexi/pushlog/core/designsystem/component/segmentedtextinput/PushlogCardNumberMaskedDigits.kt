package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

private const val GROUP_LENGTH = 4
private const val GROUP_PLACEHOLDER = "0000"

/** 카드번호 입력에서 가리는 자리. 기본은 [EIGHT]. */
enum class PushlogCardNumberMaskedDigits {
    /** 뒤 4자리만 가린다. */
    FOUR,

    /** 뒤 8자리를 가린다. */
    EIGHT,
    ;

    internal val slots: List<SegmentSlot>
        get() {
            val firstMasked = if (this == EIGHT) 2 else 3
            return buildList {
                repeat(4) { group ->
                    if (group > 0) add(SegmentSlot.Separator(" - "))
                    val masked = group >= firstMasked
                    add(
                        SegmentSlot.Input(
                            length = GROUP_LENGTH,
                            label = "카드번호 ${group * GROUP_LENGTH + 1}-${(group + 1) * GROUP_LENGTH}번째 자리",
                            masked = masked,
                            placeholder = GROUP_PLACEHOLDER.takeUnless { masked },
                            autofill = if (group == 0) SegmentAutofill.CREDIT_CARD_NUMBER else SegmentAutofill.NONE,
                        ),
                    )
                }
            }
        }
}
