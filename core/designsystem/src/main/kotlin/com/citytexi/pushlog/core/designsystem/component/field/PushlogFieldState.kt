package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription

/** field가 안쪽 입력에 내려주는 상태와 문구. field 밖이면 [None]. */
internal data class PushlogFieldState(
    val invalid: Boolean = false,
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val label: String? = null,
    val description: String? = null,
    val errorMessage: String? = null,
) {
    companion object {
        val None = PushlogFieldState()
    }
}

internal val LocalPushlogFieldState: ProvidableCompositionLocal<PushlogFieldState> =
    compositionLocalOf { PushlogFieldState.None }

/** 상태를 받은 쪽이 자기 아래로 기본값을 다시 내려줄 때 쓴다. */
@Composable
internal fun ResetPushlogFieldState(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalPushlogFieldState provides PushlogFieldState.None, content = content)
}

/**
 * 상태를 받는 노드에 field 문구를 붙인다. [contentDescription]이 있으면 field 라벨 대신 쓴다.
 * 이름 = contentDescription ?: label, invalid면 error(errorMessage.orEmpty()), 설명은 stateDescription.
 */
internal fun Modifier.pushlogFieldSemantics(
    field: PushlogFieldState,
    invalid: Boolean,
    contentDescription: String? = null,
): Modifier =
    semantics {
        (contentDescription ?: field.label)?.let { this.contentDescription = it }
        field.description?.let { stateDescription = it }
        if (invalid) error(field.errorMessage.orEmpty())
    }
