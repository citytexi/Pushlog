package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.ui.autofill.ContentType

/** 입력 칸에 붙이는 자동완성 종류. 가린 칸은 항상 [NONE]이다. */
internal enum class SegmentAutofill {
    NONE,
    BIRTH_DATE,
    CREDIT_CARD_NUMBER,
    ;

    val contentType: ContentType?
        get() =
            when (this) {
                NONE -> null
                BIRTH_DATE -> ContentType.BirthDateFull
                CREDIT_CARD_NUMBER -> ContentType.CreditCardNumber
            }
}
