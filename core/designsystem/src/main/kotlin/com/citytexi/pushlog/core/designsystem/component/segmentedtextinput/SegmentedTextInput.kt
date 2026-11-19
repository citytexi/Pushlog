package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.inputText
import androidx.compose.ui.semantics.password
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.citytexi.pushlog.core.designsystem.component.field.LocalPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.PushlogFieldSpokenText
import com.citytexi.pushlog.core.designsystem.component.field.ResetPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.pushlogFieldSemantics
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInputVariant
import com.citytexi.pushlog.core.designsystem.component.textinput.textInputStroke
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import kotlinx.coroutines.channels.Channel

private const val DOT = "•"

// field 읽기 문구와 같은 구분자. TalkBack이 쉼표에서 잠깐 멈춘다.
private const val SPOKEN_SEPARATOR = ", "

/**
 * 칸 여러 개로 나눈 숫자 입력. 칸 사이 이동, 붙여넣기 분배, 가린 칸 규칙을 맡는다.
 * 숫자는 왼쪽부터 읽으므로 RTL에서도 칸 순서를 뒤집지 않는다.
 */
@Composable
internal fun SegmentedTextInput(
    slots: List<SegmentSlot>,
    states: SegmentedTextFieldStates,
    invalid: Boolean,
    enabled: Boolean,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    suffix: (@Composable () -> Unit)? = null,
) {
    val inputs = remember(slots) { slots.filterIsInstance<SegmentSlot.Input>() }
    require(inputs.size == states.fields.size) { "slots do not match fields" }
    val field = LocalPushlogFieldState.current
    val requesters = remember(inputs.size) { List(inputs.size) { FocusRequester() } }
    val focused = remember(inputs.size) { mutableStateListOf(*Array(inputs.size) { false }) }
    // 붙여넣기에서 남은 숫자. 입력 변환 도중에 오므로 그 칸 값이 아직 확정되지 않았다. 다음 프레임에 처리한다.
    // 스냅샷 상태로 넘기면 효과가 다시 돌지 않는 것을 테스트에서 확인해 채널로 넘긴다.
    val overflow = remember(states) { Channel<Pair<Int, String>>(Channel.UNLIMITED) }
    val editable by rememberUpdatedState(enabled && !readOnly)
    LaunchedEffect(overflow, states, inputs) {
        for ((index, rest) in overflow) {
            withFrameNanos {}
            if (editable) applyOverflow(states, inputs, index, rest, requesters)
        }
    }
    val colors = SegmentColors.of(enabled)
    val shape = RoundedCornerShape(PushlogTheme.radius.r3)
    ResetPushlogFieldState {
        Box(modifier, propagateMinConstraints = true) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier =
                        Modifier
                            .heightIn(min = PushlogTheme.dimension.x13)
                            .clip(shape)
                            .background(PushlogTextInputVariant.OUTLINE.containerColor(enabled, readOnly))
                            .textInputStroke(PushlogTextInputVariant.OUTLINE, PushlogTheme.radius.r3, focused.any { it }, invalid)
                            .pushlogFieldSemantics(field, invalid)
                            .padding(horizontal = PushlogTheme.dimension.x4),
                    horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2Half),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var inputIndex = 0
                    slots.forEach { slot ->
                        when (slot) {
                            is SegmentSlot.Input -> {
                                val index = inputIndex++
                                SegmentField(
                                    slot = slot,
                                    state = states.fields[index],
                                    position = SegmentPosition(index, inputs, states, requesters),
                                    enabled = enabled,
                                    readOnly = readOnly,
                                    colors = colors,
                                    onFocusChange = { focused[index] = it },
                                    onOverflow = { overflow.trySend(index to it) },
                                    modifier = segmentWidth(slot),
                                )
                            }
                            is SegmentSlot.StaticDots ->
                                DecorText(DOT.repeat(slot.count), colors.decor, if (slot.fill) Modifier.weight(1f) else Modifier)
                            is SegmentSlot.Separator -> DecorText(slot.text, colors.decor)
                        }
                    }
                    suffix?.invoke()
                }
            }
        }
    }
}

@Composable
private fun SegmentField(
    slot: SegmentSlot.Input,
    state: TextFieldState,
    position: SegmentPosition,
    enabled: Boolean,
    readOnly: Boolean,
    colors: SegmentColors,
    onFocusChange: (Boolean) -> Unit,
    onOverflow: (String) -> Unit,
    modifier: Modifier,
) {
    val editable by rememberUpdatedState(enabled && !readOnly)
    var isFocused by remember { mutableStateOf(false) }
    val currentOnOverflow by rememberUpdatedState(onOverflow)
    val transformation = remember(slot) { SegmentInputTransformation(slot) { currentOnOverflow(it) } }
    AutoAdvanceEffect(state, slot, position) { editable && isFocused }
    if (slot.masked) MaskedCursorEffect(state)
    val style = PushlogTheme.typography.t5Regular
    BasicTextField(
        state = state,
        modifier =
            modifier
                .focusRequester(position.requesters[position.index])
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusChange(it.isFocused)
                }.onPreviewKeyEvent { event ->
                    event.type == KeyEventType.KeyDown && editable && position.handleKey(event.key)
                }.segmentSemantics(slot, state),
        enabled = enabled,
        readOnly = readOnly,
        inputTransformation = transformation,
        textStyle = style.copy(color = colors.value, textAlign = if (slot.centered) TextAlign.Center else TextAlign.Start),
        // 가린 칸은 NumberPassword로 키보드에 민감한 값임을 알린다. 이때 BasicTextField가 붙이는 ContentType.Password는
        // segmentSemantics의 바깥 의미 정보(contentDataType = None)가 덮어 자동완성 대상에서 빠진다.
        keyboardOptions =
            KeyboardOptions(
                keyboardType = if (slot.masked) KeyboardType.NumberPassword else KeyboardType.Number,
                autoCorrectEnabled = if (slot.masked) false else null,
                imeAction = if (position.isLast) ImeAction.Done else ImeAction.Next,
            ),
        lineLimits = TextFieldLineLimits.SingleLine,
        outputTransformation = if (slot.masked) MaskOutputTransformation else null,
        cursorBrush = SolidColor(PushlogTheme.color.fg.neutral),
        decorator = { innerTextField -> SegmentDecoration(slot, state, style, colors, innerTextField) },
    )
}

/**
 * 가린 칸은 비밀번호처럼 읽히고 자동완성 대상에서 빠진다. 보이는 칸은 [SegmentAutofill]대로 자동완성 종류를 붙인다.
 * 한 노드 안에서는 바깥 modifier의 의미 정보가 이기므로, 텍스트 필드가 붙이는 ContentType.Password와
 * 변환 전 값(inputText)을 여기서 덮는다.
 */
private fun Modifier.segmentSemantics(
    slot: SegmentSlot.Input,
    state: TextFieldState,
): Modifier =
    semantics {
        if (slot.masked) {
            password()
            contentDataType = ContentDataType.None
            inputText = AnnotatedString(DOT.repeat(state.text.length))
        } else {
            slot.autofill.contentType?.let { contentType = it }
        }
    }

@Composable
private fun SegmentDecoration(
    slot: SegmentSlot.Input,
    state: TextFieldState,
    style: TextStyle,
    colors: SegmentColors,
    innerTextField: @Composable () -> Unit,
) {
    Box(contentAlignment = Alignment.CenterStart) {
        // 이름은 contentDescription이 아니라 자식 글자로 합친다. 칸 너비가 placeholder 너비와 같아 placeholder 글자 노드가
        // 이 자식을 다 덮으면 이 자식이 접근성 트리에서 빠지므로, placeholder는 의미 정보에서 빼고 그 글을 여기에 잇는다.
        // 생년월일 칸처럼 이름과 같은 placeholder는 두 번 읽히지 않게 뺀다.
        val placeholder = slot.placeholder?.takeIf { state.text.isEmpty() && it != slot.label }
        PushlogFieldSpokenText(listOfNotNull(slot.label, placeholder).joinToString(SPOKEN_SEPARATOR), Modifier.matchParentSize())
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (slot.centered) Arrangement.Center else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 안쪽 입력은 받은 너비를 다 채우므로, 뒤에 고정 점이 붙으면 자리 수 너비로 줄여 점과 한 덩어리로 가운데에 둔다.
            val valueModifier = if (slot.staticSuffixDots > 0) Modifier.width(digitsWidth(slot.length)) else Modifier.weight(1f)
            Box(valueModifier, contentAlignment = if (slot.centered) Alignment.Center else Alignment.CenterStart) {
                if (state.text.isEmpty()) SegmentPlaceholder(slot, style, colors)
                innerTextField()
            }
            if (slot.staticSuffixDots > 0) DecorText(DOT.repeat(slot.staticSuffixDots), colors.decor)
        }
    }
}

@Composable
private fun SegmentPlaceholder(
    slot: SegmentSlot.Input,
    style: TextStyle,
    colors: SegmentColors,
) {
    when {
        slot.placeholder != null ->
            Text(
                slot.placeholder,
                Modifier.clearAndSetSemantics {},
                style = style,
                color = colors.decor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        slot.placeholderDots > 0 -> DecorText(DOT.repeat(slot.placeholderDots), colors.decor)
    }
}

/** 점·구분자. 값이 아니라 모양이라 접근성에서 뺀다. */
@Composable
private fun DecorText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(text, modifier.clearAndSetSemantics {}, style = PushlogTheme.typography.t5Regular, color = color, maxLines = 1)
}

/** 끝에서 한 글자를 쳐서 칸을 채우면 다음 칸으로 간다. 조합 중이거나 붙여넣기처럼 한 번에 늘어난 경우는 건너뛴다. */
@Composable
private fun AutoAdvanceEffect(
    state: TextFieldState,
    slot: SegmentSlot.Input,
    position: SegmentPosition,
    active: () -> Boolean,
) {
    val currentActive by rememberUpdatedState(active)
    LaunchedEffect(state, position.index) {
        var previous = state.text.length
        snapshotFlow { Triple(state.text.length, state.selection, state.composition) }.collect { (length, selection, composition) ->
            val typedToEnd = length == previous + 1 && selection == TextRange(length) && composition == null
            val canAdvance = length == slot.length && !position.isLast
            if (currentActive() && typedToEnd && canAdvance) {
                position.requesters[position.index + 1].requestFocus()
            }
            previous = length
        }
    }
}

/** 가린 칸은 커서를 끝에만 둔다. 선택 구간이 생기면 복사로 값이 샐 수 있다. */
@Composable
private fun MaskedCursorEffect(state: TextFieldState) {
    LaunchedEffect(state) {
        snapshotFlow { state.selection to state.text.length }.collect { (selection, length) ->
            if (selection != TextRange(length)) state.edit { placeCursorAtEnd() }
        }
    }
}

/** 자리 수 너비 칸도 weight(fill = false)로 둬, 큰 글자에서 줄이 모자라면 구분자·suffix 대신 칸이 줄어든다. */
@Composable
private fun RowScope.segmentWidth(slot: SegmentSlot.Input): Modifier =
    if (slot.fill) Modifier.weight(1f) else Modifier.weight(1f, fill = false).width(digitsWidth(slot.length))

/** 자리 수만큼의 "0" 너비. 숫자 글꼴 너비가 글자마다 달라도 칸이 값에 따라 흔들리지 않는다. */
@Composable
private fun digitsWidth(length: Int): Dp {
    val measurer = rememberTextMeasurer()
    val style = PushlogTheme.typography.t5Regular
    val density = LocalDensity.current
    return remember(length, style, density) {
        with(density) {
            measurer
                .measure("0".repeat(length), style)
                .size.width
                .toDp()
        }
    }
}

/**
 * 남은 숫자를 [index] 다음 칸부터 채우고 포커스를 옮긴다. 마지막으로 채운 칸이 덜 찼으면 그 칸,
 * 다 찼으면 그 뒤 첫 미완성 칸(없으면 그 칸). 아무 칸도 못 채웠으면 [index] 칸을 기준으로 같은 규칙을 쓴다.
 * [distribute]가 칸 자리 수를 넘지 않게 자른다.
 */
private fun applyOverflow(
    states: SegmentedTextFieldStates,
    inputs: List<SegmentSlot.Input>,
    index: Int,
    rest: String,
    requesters: List<FocusRequester>,
) {
    val lengths = states.lengths
    val masked = inputs.map { it.masked }
    val digits = digitsOnly(rest)
    val current = states.values
    val next = distribute(digits, index + 1, current, lengths, masked)
    states.setChanged(next)
    val last = lastDistributedIndex(digits.length, index + 1, lengths, masked) ?: index
    val target =
        if (next[last].length < lengths[last]) {
            last
        } else {
            (last + 1 until next.size).firstOrNull { next[it].length < lengths[it] } ?: last
        }
    requesters[target].requestFocus()
}
