# 글자 입력 PR1 구현 계획 (field · text-input · textarea · #35 그룹 연결)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `core:designsystem`에 `component.field`(공개 `PushlogField`, `PushlogFieldLabel`)와 `component.textinput`(공개 `PushlogTextInput`, `PushlogTextArea`, `TextFieldState.graphemeCount`)을 만들고, #35 체크박스·라디오·select-box 그룹이 field 상태를 받게 잇는다.

**Architecture:** `PushlogField`가 라벨·필수 표시·설명·오류·글자 수를 그리고 `internal` `LocalPushlogFieldState`(`compositionLocalOf`)로 `invalid`·`enabled`·`readOnly`와 접근성 문구를 내려준다. 상태를 받는 쪽(입력, 그룹)은 자기 인자 기본값을 그 값으로 두고, 의미 정보를 자기 노드에 붙인 뒤 아래로는 기본값을 다시 내려준다. 입력은 `TextFieldState` 기반 `BasicTextField`에 공유 장식 상자(테두리 겹침 + 앞뒤 요소)를 씌운다.

**Tech Stack:** Kotlin, Compose foundation(`BasicTextField(TextFieldState)`, `InputTransformation`), `android.icu.text.BreakIterator`, Robolectric + Roborazzi, Konsist, Navigation 3(`app-preview`).

**Spec:** `docs/superpowers/specs/2026-11-17-design-system-text-inputs-design.md` (이하 "스펙"). 이 계획은 스펙 §8 PR1만 다룬다(§6.1 field·textinput 행, §6.2, §6.3, §6.7, §6.8 느낌표, §7의 해당 줄).

## 시작하기 전에 읽을 것

1. 스펙 전체, 특히 §4, §6.2, §6.3, §6.7, §7
2. `AGENTS.md`, `docs/code-conventions.md`(서브에이전트 브리프에 이 링크를 넣는다)
3. `docs/architecture/design-system/README.md`, `pressable.md`, `preview.md`, `components/selection.md`
4. `.claude/rules/design-system-components.md`
5. SEED 정의(기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`). 필요한 값은 Task에 옮겨 두었다.
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/field.yaml`, `field-label.yaml`, `text-input.yaml`
   - 조립: `.../docs/registry/react/ui/text-field.tsx`, `checkbox.tsx`, `radio-group.tsx`, `select-box.tsx`
6. 기존 코드: `component/selectbox/PushlogSelectBoxGroup.kt`·`PushlogSelectBoxLayout.kt`(internal `CompositionLocal` 선례), `component/checkbox/CheckmarkIconDrawing.kt`(SVG 경로 그리기 선례), `component/chip/PushlogChip.kt`(`Painter?` 아이콘), 테스트 `component/radio/PushlogRadioTest.kt`, `component/chip/PushlogChipScreenshotTest.kt`, `app-preview/.../preview/component/PushlogRadioGroupPreview{Key,Screen}.kt`.

## Global Constraints

- 작업 위치: 메인 레포, 브랜치 `feat/design-system-text-input`(`origin/develop`에서 땀, 이미 있음). 워크트리는 만들지 않는다. PR 대상은 `develop`.
- `AGENTS.md` 금지 사항. `@Suppress`, baseline, ktlint 끄기, Konsist·`ModuleRules` 약화 금지.
- 주석·KDoc은 `docs/code-conventions.md`. `core:designsystem`은 한국어, `app-preview`는 영어.
- 커밋: `type: 한국어 설명`, 빈 줄, `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- 파일 하나에 최상위 class/interface/object/enum 하나(`FileStructureRulesTest`).
- `component` 패키지에서 `clickable`·`toggleable`·`selectable`·`indication`·`pointerInput`, Material 3(`Icon`·`Text`·진행 표시 외)을 쓰지 않는다(`ComponentRulesTest`). `BasicTextField`, `collectIsFocusedAsState`, `semantics`는 써도 된다.
- 공개 `@Composable`(최상위)마다 `app-preview`에 `<함수>PreviewKey`·`<함수>PreviewScreen.kt`·entry·`PreviewCatalog` 행(`PreviewCoverageRulesTest`). 이 PR의 공개 `@Composable`: `PushlogField`, `PushlogFieldLabel`, `PushlogTextInput`, `PushlogTextArea`. `graphemeCount`는 `@Composable`이 아닌 확장 프로퍼티다.
- SEED 값 → 토큰: 2→`x0Half`, 6→`x1Half`, 8→`x2`, 10→`x2Half`, 14→`x3Half`, 16→`x4`, 20→`x5`, 24→`x6`, 40→`x10`, 52→`x13`, r3→`radius.r3`. 글자는 `typography.t4Regular`·`t5Regular`·`t5Medium`·`t5Bold`·`t6Regular`. 토큰이 없는 값만 `private` 상수 + SEED 출처 주석: 1dp·2dp 선, 100ms 테두리 전환, 94dp(textarea 최소 높이), 필수 표시 sp 값(점 6, 위 4, 왼쪽 2, 보조 글자 왼쪽 4).
- 색: `PushlogTheme.colors.fg.*`·`bg.*`·`stroke.*`. 전환 시간 `PushlogTheme.motion.timingFunction.easing`.
- 입력값을 로그·예외 메시지에 넣지 않는다.
- 작업 중 `./gradlew :core:designsystem:testDebugUnitTest --tests '<클래스>'`, `./gradlew :konsist:test`. 마지막 커밋 전 `./gradlew verifyStarter`. 포맷 `./gradlew ktlintFormat`.
- 미리보기는 prevue로 렌더링해 확인한다(`prevue` skill).
- 기준 스크린샷은 커밋하지 않는다. push·PR은 사용자 확인 뒤.

## Review Focus

1. field 안 select-box 그룹의 footer에 `PushlogTextInput`을 넣으면, 입력은 바깥 `invalid`·라벨·`enabled = false`를 물려받지 않아야 한다. → Task 6 `footerInputDoesNotInheritGroupField`.
2. 글자 크기 2배에서 필수 표시 점과 보조 글자가 라벨과 겹치거나 잘리지 않아야 한다(sp 값). → Task 3 `requiredIndicatorScalesWithFont`.
3. `maxGraphemeCount`가 있는 입력에 제한보다 긴 글을 붙여넣으면 예외 없이 앞부분만 남아야 한다. 이모지 결합 문자를 반으로 자르면 안 된다. → Task 4 `pasteLongerThanMaxTruncatesAtGraphemeBoundary`.
4. 포커스된 채로 `invalid`가 풀리면 테두리가 오류색에서 포커스 색으로 바뀌어야 하고, 투명도가 0으로 떨어졌다 다시 오르면 안 된다. → Task 4 `invalidClearedWhileFocusedKeepsEmphasis`.
5. field 머리·꼬리를 숨겼으므로, 오류일 때 접근성 트리에서 오류 문구가 정확히 한 노드(입력)에만 있어야 한다. → Task 3 `errorMessageAppearsOnceInSemantics`.

---

### Task 1: 준비 — 브랜치, 기준선

스펙과 계획 세 개는 계획 단계에서 이미 이 브랜치에 커밋됐다.

**Files:** 없음(확인만)

- [ ] **Step 1: 브랜치와 문서 커밋 확인**

Run: `git branch --show-current && git status --short && git log --oneline origin/develop..HEAD`
Expected: `feat/design-system-text-input`, 작업 트리 깨끗함, 스펙·계획 커밋이 보인다.

- [ ] **Step 2: 기준선**

Run: `./gradlew testStarter`
Expected: BUILD SUCCESSFUL. 실패하면 멈추고 사용자에게 알린다.

---

### Task 2: field 상태와 `PushlogFieldLabel`

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/field/PushlogFieldState.kt`
- Create: `.../component/field/PushlogFieldLabelWeight.kt`
- Create: `.../component/field/PushlogFieldLabel.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/field/PushlogFieldStateTest.kt`
- Create: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogFieldLabelPreviewKey.kt`, `PushlogFieldLabelPreviewScreen.kt`
- Modify: `app-preview/.../preview/navigation/PreviewEntryProvider.kt`, `app-preview/.../preview/catalog/PreviewCatalog.kt`

**Interfaces:**
- Produces:

```kotlin
// field/PushlogFieldState.kt
internal data class PushlogFieldState(
    val invalid: Boolean = false,
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val label: String? = null,
    val description: String? = null,
    val errorMessage: String? = null,
) {
    // toString은 data class 기본(문구는 입력값이 아니다).
    companion object { val None = PushlogFieldState() }
}
internal val LocalPushlogFieldState: ProvidableCompositionLocal<PushlogFieldState> // compositionLocalOf { PushlogFieldState.None }

/** 상태를 받은 쪽이 자기 아래로 기본값을 다시 내려줄 때 쓴다. */
@Composable internal fun ResetPushlogFieldState(content: @Composable () -> Unit)

/**
 * 상태를 받는 노드에 field 문구를 붙인다. [contentDescription]이 있으면 field 라벨 대신 쓴다.
 * 이름 = contentDescription ?: label, invalid면 error(errorMessage.orEmpty()), 설명은 stateDescription.
 */
internal fun Modifier.pushlogFieldSemantics(
    field: PushlogFieldState,
    invalid: Boolean,
    contentDescription: String? = null,
): Modifier

// field/PushlogFieldLabelWeight.kt
enum class PushlogFieldLabelWeight { REGULAR, MEDIUM, BOLD;
    internal val textStyle: TextStyle @Composable get() // when(this): t5Regular / t5Medium / t5Bold
}

// field/PushlogFieldLabel.kt
@Composable
fun PushlogFieldLabel(
    text: String,
    modifier: Modifier = Modifier,
    weight: PushlogFieldLabelWeight = PushlogFieldLabelWeight.MEDIUM,
)
```

- [ ] **Step 1: 실패 테스트**

`PushlogFieldStateTest`(Robolectric, `createComposeRule`):

```kotlin
@Test fun semanticsUseLabelAsNameAndErrorOnlyWhenInvalid() {
    // Box(Modifier.testTag("t").pushlogFieldSemantics(PushlogFieldState(label = "이름", errorMessage = "필수 항목"), invalid = true))
    // assert: ContentDescription == listOf("이름"), Error == "필수 항목"
    // invalid = false로 다시 그리면 Error 키가 없다
}
@Test fun contentDescriptionArgumentWinsOverLabel() { /* contentDescription = "생일" → ContentDescription == listOf("생일") */ }
@Test fun descriptionBecomesStateDescription() { /* description = "숫자만" → StateDescription == "숫자만" */ }
@Test fun resetProvidesNoneBelow() { /* CompositionLocalProvider(LocalPushlogFieldState provides invalidState) { ResetPushlogFieldState { 읽은 값 == PushlogFieldState.None } } */ }
```

- [ ] **Step 2: 실행해 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogFieldStateTest'`
Expected: 컴파일 실패(`PushlogFieldState` 없음).

- [ ] **Step 3: 구현** — 위 Interfaces 그대로. `PushlogFieldLabel`은 `Text(text, style = weight.textStyle, color = fg.neutral)`. 파일 아래 `@PushlogPreview`(세 굵기).

- [ ] **Step 4: 미리보기 화면** — `PushlogFieldLabelPreviewKey`(`data object`, `NavKey`), `PushlogFieldLabelPreviewScreen.kt`(entry `pushlogFieldLabelPreviewEntry`, 본문 `PushlogFieldLabelPreviewContent`, 섹션 "weights"), entry provider와 catalog에 한 줄씩. 형식은 `PushlogRadioGroupPreviewScreen.kt`와 같다.

- [ ] **Step 5: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogFieldStateTest' :konsist:test`
Expected: PASS.

- [ ] **Step 6: 커밋** — `feat: field 상태와 field 라벨 추가`

---

### Task 3: `PushlogField`

**Files:**
- Create: `.../component/field/PushlogField.kt`
- Create: `.../component/field/FieldIconDrawing.kt` (느낌표 그리기, `internal`)
- Create: `.../component/field/FieldRequiredIndicator.kt` (`internal`)
- Test: `.../component/field/PushlogFieldTest.kt`
- Create: `app-preview/.../PushlogFieldPreviewKey.kt`, `PushlogFieldPreviewScreen.kt`; Modify entry provider, catalog

**Interfaces:**
- Consumes: Task 2 전부.
- Produces:

```kotlin
@Composable
fun PushlogField(
    modifier: Modifier = Modifier,
    label: String? = null,
    labelWeight: PushlogFieldLabelWeight = PushlogFieldLabelWeight.MEDIUM,
    showRequiredIndicator: Boolean = false,
    indicatorText: String? = null,
    description: String? = null,
    descriptionIcon: Painter? = null,
    errorMessage: String? = null,
    characterCount: Int? = null,
    maxCharacterCount: Int? = null,
    invalid: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    content: @Composable () -> Unit,
)

// FieldIconDrawing.kt
internal fun DrawScope.drawExclamationCircleIcon(color: Color)
```

구성: `Column(spacedBy(x2))` = 머리 / `CompositionLocalProvider(LocalPushlogFieldState provides PushlogFieldState(invalid, enabled, readOnly, label, description, errorMessage)) { content() }` / 꼬리.
- 머리(`label != null || indicatorText != null`일 때): `Row(padding(horizontal = x0Half))`에 `PushlogFieldLabel`, 필수 점(`showRequiredIndicator`), 보조 글자. 머리 전체에 `Modifier.clearAndSetSemantics {}`.
- 필수 점: 지름 6sp, 위 4sp, 왼쪽 2sp, 색 `fg.critical`. `LocalDensity`로 sp→dp. 보조 글자: `t4Regular.copy(lineHeight = t5Regular.lineHeight)`, `fg.neutralSubtle`, 왼쪽 4sp.
- 꼬리(설명·보이는 오류·글자 수 중 하나라도 있을 때): `Row(padding(horizontal = x0Half), spacedBy(x2))`. 왼쪽 `weight(1f)`: `invalid && errorMessage != null`이면 느낌표(16dp=`x4`, `fg.critical`, 오른쪽 `x1Half`) + 오류 문구(`t4Regular`, `fg.critical`). 아니면 `descriptionIcon`(16dp, `fg.neutralSubtle`) + 설명(`t4Regular`, `fg.neutralSubtle`). 오른쪽: 글자 수 `"$characterCount"`(`fg.neutral`) + `"/$maxCharacterCount"`(`fg.neutralSubtle`), `invalid`면 둘 다 `fg.critical`. 꼬리 전체 `clearAndSetSemantics {}`.
- 느낌표 경로(`IconExclamationmarkCircleFill`, `@karrotmarket/react-monochrome-icon` 1.35.0, Apache-2.0, viewBox 24, `PathFillType.EvenOdd`) — 그대로 옮긴다:

```
M 12 1 C 18.075 1 23 5.925 23 12 C 23 18.075 18.075 23 12 23 C 5.925 23 1 18.075 1 12 C 1 5.925 5.925 1 12 1 Z M 12 15.183 C 11.313 15.183 10.75 15.739 10.75 16.433 C 10.75 17.126 11.313 17.683 12 17.683 C 12.687 17.683 13.25 17.126 13.25 16.433 C 13.25 15.739 12.687 15.183 12 15.183 Z M 12 5.93 C 11.448 5.93 11 6.378 11 6.931 V 12.431 C 11 12.983 11.448 13.431 12 13.431 C 12.552 13.431 13 12.983 13 12.431 V 6.931 C 13 6.378 12.552 5.93 12 5.93 Z
```

- [ ] **Step 1: 실패 테스트** (`PushlogFieldTest`, Robolectric). 안쪽 내용은 `Box(Modifier.testTag("input").pushlogFieldSemantics(LocalPushlogFieldState.current, LocalPushlogFieldState.current.invalid))`로 흉내 낸다(Task 4 전이므로).

```kotlin
@Test fun errorShownOnlyWhenInvalidAndDescriptionHiddenButReadable() {
    // invalid=true, errorMessage="필수 항목", description="숫자만"
    // onNodeWithText("필수 항목", useUnmergedTree = true) 존재(화면), onNodeWithText("숫자만") 없음(화면)
    // "input" 노드: Error == "필수 항목", StateDescription == "숫자만"
    // invalid=false: "필수 항목" 화면에 없음, "숫자만" 화면에 있음
}
@Test fun errorMessageAppearsOnceInSemantics() { /* 병합 트리 전체에서 Error 키를 가진 노드 수 == 1, Text "필수 항목"을 가진 접근성 노드 수 == 0 */ }
@Test fun headerAndFooterOmittedWhenEmpty() { /* 문구 없이 그리면 root 높이 == 안쪽 Box 높이 */ }
@Test fun characterCountTurnsCriticalWhenInvalid() { /* captureToImage에서 "3"·"/40" 영역 픽셀에 fg.critical 색이 있다(invalid=true), 없다(false). 색 비교는 PushlogRadioTest 방식 */ }
@Test fun requiredIndicatorScalesWithFont() { /* fontScale 2f에서 점 지름(px) == 1f 대비 2배, 점과 라벨 경계가 겹치지 않는다(boundsInRoot) */ }
```

- [ ] **Step 2: 실패 확인** — `--tests '*PushlogFieldTest'`, 컴파일 실패.
- [ ] **Step 3: 구현** — 위 구성. 파일 아래 `@PushlogPreview` 섹션: 기본(라벨+설명), 필수+보조 글자, 오류, 글자 수.
- [ ] **Step 4: 미리보기 화면** — `PushlogFieldPreviewKey`/`Screen`. 이 Task에서는 내용 슬롯에 회색 `Box(height = x13)`을 넣고, Task 4 뒤 `PushlogTextInput`으로 바꾼다.
- [ ] **Step 5: 통과 확인** — `--tests '*PushlogField*' :konsist:test` PASS.
- [ ] **Step 6: 커밋** — `feat: field 추가`

---

### Task 4: `PushlogTextInput`와 문자소 세기

**Files:**
- Create: `.../component/textinput/PushlogTextInputVariant.kt`, `TextInputMetrics.kt`, `TextInputDecoration.kt`, `GraphemeLimitTransformation.kt`, `Graphemes.kt`, `PushlogTextInput.kt`
- Test: `.../component/textinput/GraphemesTest.kt`, `PushlogTextInputTest.kt`, `PushlogTextInputScreenshotTest.kt`
- Create: `app-preview/.../PushlogTextInputPreviewKey.kt`, `PushlogTextInputPreviewScreen.kt`; Modify entry provider, catalog, `PushlogFieldPreviewScreen.kt`(내용을 `PushlogTextInput`으로)

**Interfaces:**
- Consumes: `LocalPushlogFieldState`, `ResetPushlogFieldState`, `Modifier.pushlogFieldSemantics` (Task 2).
- Produces:

```kotlin
// Graphemes.kt
val TextFieldState.graphemeCount: Int          // 공개, text.graphemeCount()
internal fun CharSequence.graphemeCount(): Int  // android.icu.text.BreakIterator.getCharacterInstance()
internal fun CharSequence.graphemeEndIndex(count: Int): Int // 앞에서 count개 문자소가 끝나는 char 인덱스

// GraphemeLimitTransformation.kt
internal class GraphemeLimitTransformation(private val max: Int) : InputTransformation
// 넘으면 앞 max개 문자소만 남기고 뒤를 지운다(SEED useTextFieldWithGraphemes가 자른다).

// PushlogTextInputVariant.kt
enum class PushlogTextInputVariant { OUTLINE, UNDERLINE;
    @Composable internal fun metrics(multiline: Boolean): TextInputMetrics
    @Composable internal fun containerColor(enabled: Boolean, readOnly: Boolean): Color
    @Composable internal fun valueColor(enabled: Boolean, readOnly: Boolean): Color
    @Composable internal fun placeholderColor(enabled: Boolean, readOnly: Boolean): Color
}

// TextInputMetrics.kt
internal class TextInputMetrics(
    val minHeight: Dp, val paddingX: Dp, val paddingY: Dp, val gap: Dp,
    val cornerRadius: Dp, val textStyle: TextStyle, val iconSize: Dp,
)

// TextInputDecoration.kt
/** 기본선을 늘 그리고, emphasized || invalid면 2dp 강조선을 100ms 투명도 전환으로 겹친다. */
@Composable
internal fun Modifier.textInputStroke(
    variant: PushlogTextInputVariant,
    cornerRadius: Dp,
    emphasized: Boolean,
    invalid: Boolean,
): Modifier

@Composable
internal fun TextInputDecoration(
    variant: PushlogTextInputVariant,
    metrics: TextInputMetrics,
    focused: Boolean,
    invalid: Boolean,
    enabled: Boolean,
    readOnly: Boolean,
    showPlaceholder: Boolean,
    placeholder: String?,
    prefixIcon: Painter?, prefixText: String?, suffixText: String?, suffixIcon: Painter?,
    modifier: Modifier = Modifier,
    innerTextField: @Composable () -> Unit,
)

// PushlogTextInput.kt
@Composable
fun PushlogTextInput(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    variant: PushlogTextInputVariant = PushlogTextInputVariant.OUTLINE,
    placeholder: String? = null,
    prefixIcon: Painter? = null,
    prefixText: String? = null,
    suffixText: String? = null,
    suffixIcon: Painter? = null,
    maxGraphemeCount: Int? = null,
    invalid: Boolean = LocalPushlogFieldState.current.invalid,
    enabled: Boolean = LocalPushlogFieldState.current.enabled,
    readOnly: Boolean = LocalPushlogFieldState.current.readOnly,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    interactionSource: MutableInteractionSource? = null,
)
```

값(스펙 §6.3, `text-input.yaml`):

| | OUTLINE | UNDERLINE | multiline(둘 다) |
|---|---|---|---|
| minHeight | `x13` | `x10` | 94dp(상수) |
| paddingX | `x4` | 0 | 위와 같음 |
| paddingY | 0(세로 가운데) | `x2` | `x3Half` |
| gap | `x2Half` | `x2Half` | |
| radius | `r3` | 0 | |
| 글자 | `t5Regular` | `t6Regular` | |
| 아이콘 | `x5` | `x6` | |

색: 컨테이너 OUTLINE은 `!enabled || readOnly`면 `bg.disabled`, 아니면 투명. UNDERLINE은 늘 투명. 값: `!enabled`→`fg.disabled`, UNDERLINE+`readOnly`→`fg.neutralMuted`, 그 밖 `fg.neutral`. placeholder: `!enabled`→`fg.disabled`, UNDERLINE+`readOnly`→`fg.neutralMuted`, 그 밖 `fg.placeholder`. 앞뒤 글자 `fg.neutralSubtle`(비활성 `fg.disabled`), 앞뒤 아이콘 `fg.neutralMuted`(비활성 `fg.disabled`).

테두리: 기본선(OUTLINE 1dp 사방, UNDERLINE 아래 1dp) `stroke.neutralWeak`를 늘 그린다. 강조선(2dp)은 `focused || invalid`일 때 투명도 1, 아니면 0. `animateFloatAsState(tween(100, easing = PushlogTheme.motion.timingFunction.easing))`. 강조색은 `invalid`면 `stroke.criticalSolid`, 아니면 `stroke.neutralContrast`. 강조색은 애니메이션하지 않는다. 투명도는 `drawBehind`/`drawWithCache` 안에서 읽는다(그리기 단계).

`PushlogTextInput` 본문: `BasicTextField(state, enabled = enabled, readOnly = readOnly, inputTransformation = 호출자 transformation then GraphemeLimitTransformation, lineLimits = SingleLine, textStyle = metrics.textStyle.copy(color = valueColor), cursorBrush = SolidColor(fg.neutral), decorator = { TextInputDecoration(...) { it() } }, modifier = modifier.pushlogFieldSemantics(field, invalid))`. 포커스는 `interactionSource.collectIsFocusedAsState()`. 전체를 `ResetPushlogFieldState`로 감싼다.

- [ ] **Step 1: 실패 테스트 — 문자소** (`GraphemesTest`, JVM은 ICU가 없으므로 Robolectric)

```kotlin
@Test fun countsHangulAndEmojiClustersAsOne() {
    assertEquals(2, "한글".graphemeCount())
    assertEquals(1, "👨‍👩‍👧".graphemeCount())
    assertEquals(1, "🇰🇷".graphemeCount())
    assertEquals(1, "é".graphemeCount()) // e + U+0301
}
@Test fun endIndexStopsAtClusterBoundary() { assertEquals("a👨‍👩‍👧".length, "a👨‍👩‍👧b".graphemeEndIndex(2)) }
```

- [ ] **Step 2: 실패 테스트 — 입력** (`PushlogTextInputTest`, Robolectric)

```kotlin
@Test fun maxGraphemeCountBlocksExtraInput() { /* max=3, performTextInput("가나다라") → state.text == "가나다" */ }
@Test fun pasteLongerThanMaxTruncatesAtGraphemeBoundary() { /* max=2, performTextReplacement("a👨‍👩‍👧b") → "a👨‍👩‍👧", 예외 없음 */ }
@Test fun disabledRejectsInput() { /* enabled=false, assertIsNotEnabled, performTextInput 시도 뒤 text == "" (performTextInput은 예외를 던지므로 runCatching) */ }
@Test fun readOnlyKeepsValue() { /* readOnly=true, state="abc", 입력 시도 뒤 "abc", SemanticsProperties.IsEditable == false */ }
@Test fun inheritsFieldStateAndArgumentWins() {
    // PushlogField(invalid=true, enabled=false, label="이름", errorMessage="오류") { PushlogTextInput(state) }
    // → 입력 노드 assertIsNotEnabled, Error == "오류", ContentDescription == ["이름"]
    // PushlogField(enabled=false) { PushlogTextInput(state, enabled = true) } → assertIsEnabled
}
@Test fun invalidClearedWhileFocusedKeepsEmphasis() {
    // autoAdvance=false. 포커스 + invalid=true → 시계 200ms → invalid=false → 50ms 뒤 캡처
    // 테두리 위 픽셀 색 == stroke.neutralContrast(투명도 1), 중간 값 아님
}
@Test fun tapOnPaddingFocusesInput() { /* OUTLINE, 루트 왼쪽 4dp 지점 클릭 → assertIsFocused */ }
```

- [ ] **Step 3: 실패 확인** — `--tests '*Graphemes*' --tests '*PushlogTextInputTest'` 컴파일 실패.
- [ ] **Step 4: 구현** — Interfaces와 값 표대로.
- [ ] **Step 5: 스크린샷** (`PushlogTextInputScreenshotTest`, `@Config(qualifiers = "w1200dp-h1200dp")`, `autoAdvance = false`): `statesLight`/`statesDark` — variant 2 × {기본, 값, 포커스, 오류, 오류+포커스, 읽기 전용, 비활성}. 포커스는 `requestFocus()` 뒤 시계 200ms.
- [ ] **Step 6: 미리보기** — 파일 아래 `@PushlogPreview` 섹션: outline 상태, underline 상태, 앞뒤 요소, field와 함께. `PushlogTextInputPreviewKey`/`Screen`, entry, catalog. `PushlogFieldPreviewScreen.kt` 내용 슬롯을 `PushlogTextInput`으로 바꾸고 글자 수 예시에 `state.graphemeCount`를 쓴다.
- [ ] **Step 7: 통과 확인** — `--tests '*textinput*' --tests '*field*' :konsist:test` PASS. `recordRoborazziDebug`로 기준을 만들어 눈으로 확인(커밋 안 함).
- [ ] **Step 8: 커밋** — `feat: text-input과 문자소 글자 수 추가`

---

### Task 5: `PushlogTextArea`

**Files:**
- Create: `.../component/textinput/PushlogTextArea.kt`
- Test: `.../component/textinput/PushlogTextAreaTest.kt`; 스크린샷은 Task 4 테스트 파일에 `textAreaLight`/`textAreaDark` 추가
- Create: `app-preview/.../PushlogTextAreaPreviewKey.kt`, `PushlogTextAreaPreviewScreen.kt`; Modify entry provider, catalog

**Interfaces:**
- Consumes: Task 4 `TextInputDecoration`, `PushlogTextInputVariant.metrics(multiline = true)`, `GraphemeLimitTransformation`.
- Produces: `PushlogTextInput`과 같은 인자에 `lineLimits: TextFieldLineLimits = TextFieldLineLimits.MultiLine()`를 더한 `fun PushlogTextArea(...)`. 장식 상자의 앞뒤 요소는 위쪽 정렬.

- [ ] **Step 1: 실패 테스트**

```kotlin
@Test fun minHeightIs94dp() { /* 빈 상태 높이 == 94dp */ }
@Test fun growsWithContent() { /* 다섯 줄 입력 뒤 높이 > 94dp */ }
@Test fun respectsCallerHeightLimit() { /* lineLimits = MultiLine(maxHeightInLines = 3), 열 줄 입력 → 높이가 세 줄 기준 이하 */ }
@Test fun maxGraphemeCountApplies() { /* max=5 */ }
```

- [ ] **Step 2: 실패 확인 → Step 3: 구현 → Step 4: 미리보기(섹션: 기본, 오류, 비활성, field+글자 수) → Step 5: 통과 확인(`--tests '*TextArea*' :konsist:test`)**
- [ ] **Step 6: 커밋** — `feat: textarea 추가`

---

### Task 6: #35 선택 그룹을 field에 잇기

**Files:**
- Create: `.../component/selection/LocalSelectionGroupEnabled.kt` — `internal val LocalSelectionGroupEnabled = compositionLocalOf { true }`
- Modify: `.../component/checkbox/PushlogCheckboxGroup.kt`, `.../radio/PushlogRadioGroup.kt`, `.../selectbox/PushlogSelectBoxGroup.kt`
- Modify: `.../checkbox/PushlogCheckbox.kt`, `.../radio/PushlogRadio.kt`, `.../selectbox/PushlogSelectBox.kt` — `enabled: Boolean = LocalSelectionGroupEnabled.current`
- Test: `.../component/field/PushlogFieldSelectionGroupTest.kt`

**Interfaces:**
- Consumes: Task 2 `LocalPushlogFieldState`, `ResetPushlogFieldState`, `pushlogFieldSemantics`.
- 그룹 세 개: `val field = LocalPushlogFieldState.current`. 그룹 루트 modifier에 `.pushlogFieldSemantics(field, field.invalid)`를 `selectableGroup()` 뒤에 붙인다. 내용은 `CompositionLocalProvider(LocalSelectionGroupEnabled provides field.enabled) { ResetPushlogFieldState { content() } }`. 공개 시그니처는 바꾸지 않는다.
- 컨트롤 세 개: `enabled` 기본값만 바꾼다. 그룹 밖·field 바로 아래에서는 `true`라 동작이 같다.

- [ ] **Step 1: 실패 테스트**

```kotlin
@Test fun disabledFieldDisablesGroupChildren() { /* PushlogField(enabled=false) { PushlogCheckboxGroup { PushlogCheckbox(...) } } → 탭해도 onClick 0회, assertIsNotEnabled. 라디오·select-box 그룹도 같은 검사 */ }
@Test fun childArgumentOverridesGroupDefault() { /* 위에서 PushlogCheckbox(enabled = true) → 탭 1회 */ }
@Test fun groupCarriesFieldLabelAndError() { /* PushlogField(label="관심사", invalid=true, errorMessage="하나 이상 고르세요") { PushlogRadioGroup {...} } → 그룹 노드 ContentDescription == ["관심사"], Error == "하나 이상 고르세요" */ }
@Test fun bareCheckboxInFieldIgnoresFieldState() { /* PushlogField(enabled=false) { PushlogCheckbox(...) } → 탭 1회(그룹이 없으면 field를 읽지 않음) */ }
@Test fun footerInputDoesNotInheritGroupField() {
    // PushlogField(invalid=true, enabled=false, label="방식", errorMessage="오류") {
    //   PushlogSelectBoxGroup { PushlogSelectBox(selected=true, ..., enabled=true, footer = { PushlogTextInput(state, modifier=Modifier.testTag("etc")) }) } }
    // → "etc" assertIsEnabled, Error 키 없음, ContentDescription 없음
}
@Test fun nestedFieldUsesInnerState() { /* PushlogField(invalid=true) { PushlogField(invalid=false) { PushlogTextInput } } → Error 키 없음 */ }
```

- [ ] **Step 2: 실패 확인** — `--tests '*PushlogFieldSelectionGroupTest'`.
- [ ] **Step 3: 구현.**
- [ ] **Step 4: #35 회귀 확인** — 이 Task 전 커밋에서 `./gradlew recordRoborazziDebug`, 바꾼 뒤 `./gradlew verifyRoborazziDebug`. Expected: 차이 없음. 새 파일이 있으면 `git stash -u`로 되돌려 기록한다(rules 파일).
- [ ] **Step 5: 통과 확인** — `./gradlew testStarter` PASS.
- [ ] **Step 6: 커밋** — `feat: 선택 컨트롤 그룹이 field 상태를 받도록 연결`

---

### Task 7: 문서, 검증, PR

**Files:**
- Create: `docs/architecture/design-system/components/input.md`
- Modify: `docs/architecture/design-system/README.md`(작업 표에 "field·text-input·search-input·input-button·segmented 입력을 고친다 → components/input.md"), `docs/architecture/design-system/components/selection.md`(그룹이 field 상태를 받는다는 한 줄과 링크), `docs/synthesis/open-questions.md`

- [ ] **Step 1: `components/input.md`** — 프론트매터는 `components/selection.md` 형식을 따른다(`related_spec`에 이 스펙). 코드로 복원할 수 없는 것만: field 상태 전달과 끊는 지점(왜 받는 쪽에서 끊는지), 접근성 노드를 하나로 모은 이유, 테두리를 겹쳐 투명도만 바꾸는 이유, `graphemeCount`가 `@Composable`이 아닌 이유, `medium` 크기를 뺀 이유. 그리고 이 문장: "주민등록번호·카드번호 입력을 쓰는 화면은 수집 목적, 보존 기간, 동의를 자기 스펙에 적는다."
- [ ] **Step 2: `open-questions.md` 미해결에 추가**

```markdown
### [2026-11-17] 글자 입력 상태를 화면 어디에 둘지

- `PushlogTextInput`은 `TextFieldState`, segmented 입력은 상태 클래스를 받는다. 둘 다 변경 가능한 객체라 불변 `UiState`에 넣기 어렵다. 설계: [`2026-11-17-design-system-text-inputs-design.md`](../superpowers/specs/2026-11-17-design-system-text-inputs-design.md) §4.1
- 남은 것: 첫 입력 화면에서 Route가 상태를 들고 `snapshotFlow`로 `UiIntent`를 보낼지, ViewModel이 들지 정하고 `state-management.md`에 적는다.
```

- [ ] **Step 3: 링크 검사** — `python3 docs/script/check_links.py docs` → 깨진 링크 0건.
- [ ] **Step 4: 수동 확인(기기)** — 스펙 §7 "수동 확인" 중 PR1 해당분: Gboard·삼성 키보드에서 `PushlogTextInput`에 "한글"을 천천히 입력해 되돌림·커서 튐이 없는지, TalkBack에서 field 라벨·설명·오류가 한 번씩만 읽히는지. 결과를 PR 본문에 적는다. 기기가 없으면 사용자에게 알리고 PR 본문에 "미확인"으로 적는다.
- [ ] **Step 5: 전체 검증** — `./gradlew verifyStarter` → BUILD SUCCESSFUL.
- [ ] **Step 6: 커밋** — `docs: 글자 입력 컴포넌트 문서와 미결 항목 추가`
- [ ] **Step 7: PR(사용자 확인 뒤)** — `gh pr create --base develop --head feat/design-system-text-input --title "feat: add field, text input and textarea"`. 본문은 `.github/PULL_REQUEST_TEMPLATE.md`, `Part of #36`, 수동 확인 결과, 끝에 `🤖 Generated with [Claude Code](https://claude.com/claude-code)`.

## PR2·PR3이 쓰는 인터페이스

```kotlin
// component.field
internal data class PushlogFieldState(invalid, enabled, readOnly, label, description, errorMessage)
internal val LocalPushlogFieldState: ProvidableCompositionLocal<PushlogFieldState>
@Composable internal fun ResetPushlogFieldState(content: @Composable () -> Unit)
internal fun Modifier.pushlogFieldSemantics(field: PushlogFieldState, invalid: Boolean, contentDescription: String? = null): Modifier
internal fun DrawScope.drawExclamationCircleIcon(color: Color)

// component.textinput
@Composable internal fun TextInputDecoration(...)        // Task 4 시그니처
internal class TextInputMetrics(...)
@Composable internal fun Modifier.textInputStroke(variant: PushlogTextInputVariant, cornerRadius: Dp, emphasized: Boolean, invalid: Boolean): Modifier
// PR2 input-button(emphasized = false)과 PR3 segmented(emphasized = 칸 중 하나라도 포커스)가 같은 테두리를 쓴다.
internal fun CharSequence.graphemeCount(): Int
```
