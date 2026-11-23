package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInputVariant

/** 값 글자색과 placeholder·점·구분자 색. OUTLINE 입력 글자색을 따르므로 읽기 전용은 활성과 같다(SEED segmented-text-input). */
@Immutable
internal data class SegmentColors(
    val value: Color,
    val decor: Color,
) {
    companion object {
        @Composable
        fun of(
            enabled: Boolean,
            readOnly: Boolean,
        ): SegmentColors {
            val variant = PushlogTextInputVariant.OUTLINE
            return SegmentColors(variant.valueColor(enabled, readOnly), variant.placeholderColor(enabled, readOnly))
        }
    }
}
