package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import com.citytexi.pushlog.core.designsystem.R

// 읽기 문구 조각을 잇는 구분자. TalkBack이 쉼표에서 잠깐 멈춘다.
private const val SPOKEN_SEPARATOR = ", "

/** field가 안쪽 입력에 내려주는 상태와 문구. field 밖이면 [None]. */
internal data class PushlogFieldState(
    val invalid: Boolean = false,
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val label: String? = null,
    val description: String? = null,
    val errorMessage: String? = null,
    val required: Boolean = false,
    val indicatorText: String? = null,
    val characterCount: Int? = null,
    val maxCharacterCount: Int? = null,
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
 * 화면에서 숨긴 field 머리·꼬리 대신 읽힐 보조 문구. 필수, 보조 글자, 설명, 글자 수 순서로 잇는다.
 * 설명은 오류로 화면에서 감춰져도 넣는다. 없으면 null.
 */
@Composable
internal fun PushlogFieldState.spokenDetails(): String? {
    val count =
        characterCount?.let { count ->
            maxCharacterCount?.let { stringResource(R.string.designsystem_field_character_count, count, it) }
                ?: stringResource(R.string.designsystem_field_character_count_unbounded, count)
        }
    val required = if (required) stringResource(R.string.designsystem_field_required) else null
    return listOfNotNull(required, indicatorText, description, count).joinSpoken()
}

/** 라벨과 보조 문구를 이은 전체 읽기 문구. 글자 입력의 의미 전용 자식이 쓴다. */
@Composable
internal fun PushlogFieldState.spokenText(): String? = listOfNotNull(label, spokenDetails()).joinSpoken()

private fun List<String>.joinSpoken(): String? = filter { it.isNotEmpty() }.takeIf { it.isNotEmpty() }?.joinToString(SPOKEN_SEPARATOR)

/**
 * 그룹처럼 편집 글자가 없는 노드에 field 문구를 붙인다. [contentDescription]이 있으면 field 라벨 대신 쓴다.
 * 이름 = contentDescription ?: label, 보조 문구는 stateDescription, invalid면 error(errorMessage.orEmpty()).
 * 글자 입력에는 쓰지 않는다. 편집 글자가 있는 노드는 stateDescription이 덮이고 contentDescription이 글자 탐색을 막는다.
 */
@Composable
internal fun Modifier.pushlogFieldSemantics(
    field: PushlogFieldState,
    invalid: Boolean,
    contentDescription: String? = null,
): Modifier {
    val details = field.spokenDetails()
    return semantics {
        (contentDescription ?: field.label)?.let { this.contentDescription = it }
        details?.let { stateDescription = it }
        if (invalid) error(field.errorMessage.orEmpty())
    }
}

/** 글자 입력 노드에는 오류만 붙인다. 이름과 보조 문구는 [PushlogFieldSpokenText]가 자식으로 합친다. */
internal fun Modifier.pushlogFieldErrorSemantics(
    field: PushlogFieldState,
    invalid: Boolean,
): Modifier = if (invalid) semantics { error(field.errorMessage.orEmpty()) } else this

/**
 * 그리지 않고 [text]만 접근성에 내놓는 자식. 글자 입력이 자손 의미를 합치므로 M3 라벨처럼 입력의 자식 노드로 읽힌다.
 * 크기가 0이면 접근성 트리에서 빠지므로 [modifier]로 입력 상자 크기를 받는다.
 */
@Composable
internal fun PushlogFieldSpokenText(
    text: String,
    modifier: Modifier,
) {
    Spacer(modifier.semantics { this.text = AnnotatedString(text) })
}
