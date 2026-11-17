# 글자 입력 PR3 구현 계획 (segmented: 주민등록번호 · 카드번호)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `core:designsystem`에 `component.segmentedtextinput` 패키지를 만들고, 공개 `PushlogResidentNumberInput`·`PushlogCardNumberInput`과 상태 클래스(`Saver` 포함)를 SEED segmented-text-input 동작 전부로 만든다.

**Architecture:** 칸 구성은 `internal` 칸 명세 목록이다. 값 규칙(숫자만, 자리 수, 붙여넣기 분배, 날짜 변환)은 Android 의존 없는 순수 함수로 두고 JVM 테스트로 덮는다. 칸마다 `TextFieldState` + `BasicTextField`를 두고, 칸 `InputTransformation`이 그 칸에 들어갈 몫만 남긴 뒤 넘친 입력을 엔진에 넘긴다. 엔진(`internal` 컴포저블)은 넘친 입력 분배, 칸 사이 포커스 이동, 빈 칸 Backspace, ←·→ 키를 맡는다. 가린 칸은 `OutputTransformation`으로 점을 보여 주고 `BasicSecureTextField`는 쓰지 않는다. `BasicSecureTextField`는 `ContentType.Password`를 자동으로 붙여 자동완성 저장 제안이 생길 수 있어서다(Compose foundation 1.12.0에서 확인).

**Tech Stack:** Kotlin, Compose foundation 1.12.0(`BasicTextField(TextFieldState)`, `InputTransformation`, `OutputTransformation`), Compose UI 자동완성 `ContentType`(`BirthDateFull`, `CreditCardNumber`), `rememberSaveable`/`Saver`, Robolectric(`performKeyInput`, `StateRestorationTester`) + Roborazzi, Konsist.

**Spec:** `docs/superpowers/specs/2026-11-17-design-system-text-inputs-design.md` (이하 "스펙"). 이 계획은 스펙 §8 PR3만 다룬다(§6.1 segmentedtextinput 행, §6.6, §7 segmented 줄). PR2와는 서로 의존하지 않는다.

## 시작하기 전에 읽을 것

1. 스펙 §5(저장 상태 결정), §6.6, §7
2. PR1 계획의 마지막 절 "PR2·PR3이 쓰는 인터페이스": `docs/superpowers/plans/2026-11-17-text-inputs-pr1.md`
3. `AGENTS.md`, `docs/code-conventions.md`(서브에이전트 브리프에 링크)
4. `docs/architecture/design-system/components/input.md`(PR1), `preview.md`
5. `.claude/rules/design-system-components.md`, `.claude/skills/git-flow/SKILL.md`
6. SEED(기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`):
   - 값: `packages/rootage/components/segmented-text-input.yaml`
   - 형식: `docs/registry/react/ui/resident-number-field.tsx`, `card-number-field.tsx`, `segmented-text-field.tsx`
   - 동작: `docs/content/react/components/segmented-text-input.mdx`, `packages/react-headless/segmented-text-input/src`(애매한 동작은 여기를 따른다)
7. 기존 코드(PR1 머지 뒤 develop): `component/field/PushlogFieldState.kt`, `component/textinput/TextInputDecoration.kt`(`textInputStroke`), `component/textinput/GraphemeLimitTransformation.kt`(변환 선례)

## Global Constraints

- 작업 위치: 메인 레포, 브랜치 `feat/design-system-segmented-text-input`(PR1 머지 뒤 `origin/develop`에서 땀). 워크트리 없음. PR 대상 `develop`.
- `AGENTS.md` 금지 사항, `docs/code-conventions.md`. 주석은 `core:designsystem` 한국어, `app-preview` 영어.
- 커밋: `type: 한국어 설명`, 빈 줄, `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- 파일 하나에 최상위 타입 하나. `pointerInput`·`clickable` 등 탭 처리 금지. 키 처리는 `Modifier.onPreviewKeyEvent`(탭 처리가 아니라 `ComponentRulesTest` 대상이 아님).
- 공개 `@Composable`: `PushlogResidentNumberInput`, `PushlogCardNumberInput` → 각각 Key·Screen·entry·catalog. 공개 `remember…` 함수는 만들지 않는다(`PreviewCoverageRulesTest`). 상태 클래스와 `Saver`는 공개 `@Composable`이 아니라 화면이 필요 없다.
- 값(`segmented-text-input.yaml` large): 최소 높이 `x13`, `radius.r3`, 여백 `x4`, 칸 간격 `x2Half`, 글자 `t5Regular`. 색: 값 `fg.neutral`, placeholder·가린 점·구분자 `fg.placeholder`, 고정 글자 `fg.neutral`. 비활성: 배경 `bg.disabled`, 위 글자 전부 `fg.disabled`. 읽기 전용: 배경 `bg.disabled`. 테두리는 `textInputStroke(OUTLINE, r3, emphasized = 칸 하나라도 포커스, invalid)`.
- 숫자 칸은 LTR이다. 앱이 RTL이어도 칸 줄은 `LocalLayoutDirection provides LayoutDirection.Ltr`로 그린다(번호는 왼쪽부터 읽힌다).
- 가린 값은 화면·접근성 트리·로그·`toString`·자동완성 서비스에 나오지 않는다. 저장 상태(`Saver`)에는 들어간다(스펙 §5, 사용자 결정).
- 작업 중 `./gradlew :core:designsystem:testDebugUnitTest --tests '<클래스>'`, `./gradlew :konsist:test`. 마지막 커밋 전 `./gradlew verifyStarter`.
- 미리보기는 prevue로 확인. 기준 스크린샷은 커밋하지 않는다. push·PR은 사용자 확인 뒤.

## Review Focus

1. 가운데 칸(카드 둘째 칸)에 16자리를 붙여넣으면 그 칸부터 채우고 가린 칸 앞에서 멈춘다. 앞 칸은 건드리지 않는다. → Task 2 `pasteIntoMiddleSegmentFillsFromThere`.
2. 가린 칸에 붙여넣기·자동완성이 들어오면 무시된다(값이 그대로). → Task 4 `pasteIntoMaskedSegmentIsIgnored`.
3. 앱이 RTL이어도 칸 순서와 Backspace·←·→ 방향은 LTR 그대로다. → Task 4 `rtlKeepsSegmentOrder`.
4. 회전(상태 복원) 뒤 포커스가 있던 칸과 값이 복원되고, 가린 칸은 계속 점으로 보인다. → Task 3 `restoresAllValuesIncludingMasked`, Task 4 `maskedStaysMaskedAfterRestore`.
5. 숫자가 아닌 글자(한글, 공백, 하이픈)를 치면 칸에 들어가지 않는다. 한글 조합 중에는 칸 이동이 없다. → Task 2 `nonDigitsAreDropped`, Task 4 `compositionDoesNotAdvance`.

---

### Task 1: 준비 — PR1 확인, 브랜치

**Files:** 없음(확인만)

- [ ] **Step 1: PR1 확인**

```bash
git fetch origin
P=core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component
git grep -n "LocalPushlogFieldState" origin/develop -- $P/field/PushlogFieldState.kt
git grep -n "fun Modifier.pushlogFieldSemantics" origin/develop -- $P/field/PushlogFieldState.kt
git grep -n "fun Modifier.textInputStroke" origin/develop -- $P/textinput/TextInputDecoration.kt
```

Expected: 모두 한 줄 이상. 비면 멈추고 사용자에게 알린다.

- [ ] **Step 2: 브랜치** — `git status --short`가 깨끗한지 보고 `git switch -c feat/design-system-segmented-text-input origin/develop`.
- [ ] **Step 3: 기준선** — `./gradlew testStarter` → BUILD SUCCESSFUL.

---

### Task 2: 칸 명세와 값 규칙(순수 함수)

**Files:**
- Create: `.../component/segmentedtextinput/SegmentSlot.kt`, `SegmentRules.kt`
- Test: `core/designsystem/src/test/kotlin/.../component/segmentedtextinput/SegmentRulesTest.kt` (JVM, Robolectric 아님)

**Interfaces:**
- Produces:

```kotlin
// SegmentSlot.kt
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
        val fill: Boolean = false,      // true면 남는 너비를 나눠 가짐(SEED sizing="fill"), false면 자리 수만큼
        val centered: Boolean = false,  // SEED align="center"
    ) : SegmentSlot
    data class StaticDots(val count: Int, val fill: Boolean = false) : SegmentSlot
    data class Separator(val text: String) : SegmentSlot
}
// SegmentAutofill은 같은 파일에 둘 수 없다(최상위 타입 하나) → SegmentAutofill.kt
internal enum class SegmentAutofill { NONE, BIRTH_DATE, CREDIT_CARD_NUMBER }

// SegmentRules.kt (최상위 함수, Android 의존 없음)
internal fun digitsOnly(text: CharSequence): String
/** yyyy-MM-dd, yyyy.MM.dd, yyyy/MM/dd, yyyyMMdd(앞뒤 공백 허용)를 yyMMdd로. 아니면 null. 월 1..12, 일 1..31만 확인. */
internal fun normalizeBirthDate(text: CharSequence): String?
/**
 * [startIndex] 입력 칸부터 [digits]를 차례로 채운 새 값 목록. [startIndex] 앞 칸은 그대로 둔다.
 * 시작 칸 다음의 가린 칸 앞에서 멈춘다. 시작 칸이 가린 칸이면 아무것도 바꾸지 않는다. 남는 숫자는 버린다.
 * [current]·[lengths]·[masked]는 입력 칸 순서다.
 */
internal fun distribute(digits: String, startIndex: Int, current: List<String>, lengths: List<Int>, masked: List<Boolean>): List<String>
```

- [ ] **Step 1: 실패 테스트**

```kotlin
@Test fun nonDigitsAreDropped() { assertEquals("1234", digitsOnly("12-3 4가")) }
@Test fun birthDateFormats() {
    assertEquals("900101", normalizeBirthDate("1990-01-01"))
    assertEquals("900101", normalizeBirthDate("1990.01.01"))
    assertEquals("900101", normalizeBirthDate("1990/01/01"))
    assertEquals("051231", normalizeBirthDate("20051231"))
    assertNull(normalizeBirthDate("1990-13-01"))
    assertNull(normalizeBirthDate("900101"))   // 이미 6자리면 변환 대상 아님
}
private val card = Triple(listOf(4, 4, 4, 4), listOf(false, false, true, true), List(4) { "" })
@Test fun pasteFillsFromStartAndStopsBeforeMasked() {
    assertEquals(listOf("1234", "5678", "", ""), distribute("1234567890123456", 0, card.third, card.first, card.second))
}
@Test fun pasteIntoMiddleSegmentFillsFromThere() {
    assertEquals(listOf("9999", "1234", "", ""), distribute("12345678", 1, listOf("9999", "", "", ""), card.first, card.second))
}
@Test fun pasteStartingAtMaskedChangesNothing() {
    assertEquals(listOf("1234", "5678", "", ""), distribute("1111", 2, listOf("1234", "5678", "", ""), card.first, card.second))
}
@Test fun residentThirteenStopsBeforeMaskedBack() {
    assertEquals(listOf("900101", ""), distribute("9001011234567", 0, listOf("", ""), listOf(6, 7), listOf(false, true)))
}
@Test fun residentSevenFillsFirstBackDigit() {
    assertEquals(listOf("900101", "1"), distribute("9001011234567", 0, listOf("", ""), listOf(6, 1), listOf(false, false)))
}
```

- [ ] **Step 2: 실패 확인** — `--tests '*SegmentRulesTest'` 컴파일 실패.
- [ ] **Step 3: 구현.**
- [ ] **Step 4: 통과 확인** — PASS.
- [ ] **Step 5: 커밋** — `feat: segmented 입력 칸 명세와 값 규칙 추가`

---

### Task 3: 상태 클래스와 `Saver`

**Files:**
- Create: `.../segmentedtextinput/SegmentedTextFieldStates.kt`(internal 공통), `PushlogResidentNumberDigits.kt`, `PushlogCardNumberMaskedDigits.kt`, `PushlogResidentNumberState.kt`, `PushlogCardNumberState.kt`
- Test: `.../segmentedtextinput/SegmentedStateTest.kt` (Robolectric)

**Interfaces:**
- Consumes: Task 2 `SegmentSlot`.
- Produces:

```kotlin
// 공개 enum
enum class PushlogResidentNumberDigits { SIX, SEVEN, THIRTEEN;     // 기본 SEVEN(SEED digits=7)
    internal val slots: List<SegmentSlot> }                          // 아래 표
enum class PushlogCardNumberMaskedDigits { FOUR, EIGHT;             // 기본 EIGHT
    internal val slots: List<SegmentSlot> }

// internal 공통
internal class SegmentedTextFieldStates(lengths: List<Int>, initial: List<String> = List(lengths.size) { "" }) {
    val fields: List<TextFieldState>
    val values: List<String>          // 스냅샷 읽기
    fun clear()
    fun setAll(values: List<String>)
}

// 공개 상태
class PushlogResidentNumberState(val digits: PushlogResidentNumberDigits = PushlogResidentNumberDigits.SEVEN) {
    internal val fields: SegmentedTextFieldStates
    val values: List<String>           // digits=SIX면 [생년월일], 그 밖은 [생년월일, 뒷자리]
    fun clear()
    override fun toString(): String    // "PushlogResidentNumberState(digits=SEVEN)" — 값 없음
    companion object { val Saver: Saver<PushlogResidentNumberState, Any> }   // listSaver: [digits.name, *values]
}
class PushlogCardNumberState(val maskedDigits: PushlogCardNumberMaskedDigits = PushlogCardNumberMaskedDigits.EIGHT) {
    internal val fields: SegmentedTextFieldStates
    val values: List<String>           // 4개
    fun clear()
    override fun toString(): String    // "PushlogCardNumberState(maskedDigits=EIGHT)"
    companion object { val Saver: Saver<PushlogCardNumberState, Any> }
}
```

칸 구성(SEED registry 그대로, 구분자 `" - "`):

| 형식 | slots |
|---|---|
| 주민 SIX | `Input(6, "생년월일 6자리", placeholder = "생년월일 6자리", autofill = BIRTH_DATE, fill = true)`, `Separator`, `StaticDots(7, fill = true)` |
| 주민 SEVEN | 위 첫 칸, `Separator`, `Input(1, "주민등록번호 뒷자리 첫 번째 자리", placeholderDots = 1, staticSuffixDots = 6, fill = true, centered = true)` |
| 주민 THIRTEEN | 위 첫 칸, `Separator`, `Input(7, "주민등록번호 뒷자리 7자리", masked = true, fill = true, centered = true)` |
| 카드 | `Input(4, "카드번호 1-4번째 자리", placeholder = "0000", autofill = CREDIT_CARD_NUMBER)`, `Separator`, `Input(4, "카드번호 5-8번째 자리", placeholder = "0000")`, `Separator`, 셋째·넷째(`9-12`, `13-16`). EIGHT면 셋째·넷째, FOUR면 넷째만 `masked = true`, placeholder 없음 |

- [ ] **Step 1: 실패 테스트**

```kotlin
@Test fun toStringHasNoDigits() {
    val s = PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN).also { it.fields.setAll(listOf("900101", "1234567")) }
    assertFalse(s.toString().any(Char::isDigit))
    // 카드: PushlogCardNumberState().also { it.fields.setAll(listOf("1234", "5678", "9012", "3456")) } → 숫자 없음
}
@Test fun restoresAllValuesIncludingMasked() {
    // StateRestorationTester: rememberSaveable(saver = PushlogCardNumberState.Saver) { PushlogCardNumberState() }
    // 값 ["1234","5678","9012","3456"] → emulateSavedInstanceStateRestore() → values 같음, maskedDigits 같음
}
@Test fun clearEmptiesAll()
@Test fun valuesShapeFollowsDigits() { /* SIX → 1개, SEVEN·THIRTEEN → 2개, 카드 → 4개 */ }
```

두 상태 클래스는 `internal val fields: SegmentedTextFieldStates`를 갖는다. 엔진과 테스트가 이것으로 칸 상태에 접근한다.

- [ ] **Step 2: 실패 확인 → Step 3: 구현 → Step 4: 통과 확인**
- [ ] **Step 5: 커밋** — `feat: 주민등록번호·카드번호 입력 상태 추가`

---

### Task 4: 칸 엔진과 공개 컴포저블

**Files:**
- Create: `.../segmentedtextinput/SegmentInputTransformation.kt`, `MaskOutputTransformation.kt`, `SegmentedTextInput.kt`(internal 엔진), `PushlogResidentNumberInput.kt`, `PushlogCardNumberInput.kt`
- Test: `.../segmentedtextinput/SegmentedTextInputTest.kt`, `SegmentedTextInputScreenshotTest.kt`
- Create: `app-preview/.../PushlogResidentNumberInputPreviewKey.kt`, `...Screen.kt`, `PushlogCardNumberInputPreviewKey.kt`, `...Screen.kt`; Modify entry provider, catalog

**Interfaces:**
- Consumes: Task 2·3 전부; PR1 `LocalPushlogFieldState`, `ResetPushlogFieldState`, `pushlogFieldSemantics`, `textInputStroke`.
- Produces:

```kotlin
// SegmentInputTransformation.kt
/**
 * 숫자만 남기고 이 칸 몫만 남긴다. 한 번에 두 글자 이상 들어오면(붙여넣기·자동완성)
 * 이 칸 몫을 남기고 나머지를 [onOverflow]로 넘긴다. 생년월일 칸은 날짜 형식이면 먼저 6자리로 바꾼다.
 * 가운데에 넣어 자리 수를 넘기는 한 글자 입력은 되돌린다. 가린 칸은 끝에서만 입력·삭제하고 두 글자 이상 입력은 되돌린다.
 */
internal class SegmentInputTransformation(
    private val slot: SegmentSlot.Input,
    private val onOverflow: (String) -> Unit,
) : InputTransformation

// MaskOutputTransformation.kt — 글자마다 '•'(U+2022)로 바꾼다. 길이가 같아 커서 매핑이 단순하다.
internal object MaskOutputTransformation : OutputTransformation

// SegmentedTextInput.kt
@Composable
internal fun SegmentedTextInput(
    slots: List<SegmentSlot>,
    states: SegmentedTextFieldStates,
    invalid: Boolean,
    enabled: Boolean,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    suffix: (@Composable () -> Unit)? = null,
)

// 공개
@Composable
fun PushlogResidentNumberInput(
    state: PushlogResidentNumberState,
    modifier: Modifier = Modifier,
    invalid: Boolean = LocalPushlogFieldState.current.invalid,
    enabled: Boolean = LocalPushlogFieldState.current.enabled,
    readOnly: Boolean = LocalPushlogFieldState.current.readOnly,
)
@Composable
fun PushlogCardNumberInput(
    state: PushlogCardNumberState,
    modifier: Modifier = Modifier,
    suffix: (@Composable () -> Unit)? = null,
    invalid: Boolean = LocalPushlogFieldState.current.invalid,
    enabled: Boolean = LocalPushlogFieldState.current.enabled,
    readOnly: Boolean = LocalPushlogFieldState.current.readOnly,
)
```

엔진 규칙:
- 바깥: `Row(minHeight x13, padding(horizontal = x4), spacedBy(x2Half), 배경, textInputStroke(...))`를 `LocalLayoutDirection provides Ltr` 안에 그린다. 바깥 노드에 `pushlogFieldSemantics(field, invalid)`. 전체를 `ResetPushlogFieldState`로 감싼다.
- 입력 칸: `BasicTextField(states.fields[i], enabled, readOnly, inputTransformation = SegmentInputTransformation(slot) { overflow -> pending = i to overflow }, outputTransformation = if (masked) MaskOutputTransformation else null, keyboardOptions(keyboardType = if (masked) NumberPassword else Number, imeAction = Next(마지막 칸은 Done)), lineLimits = SingleLine, modifier = focusRequester + onPreviewKeyEvent + semantics)`.
- 칸 의미 정보: `contentDescription = slot.label`. 가린 칸은 `password()`를 붙인다(TalkBack이 숫자를 읽지 않음). 자동완성은 `slot.autofill`에 따라 `contentType = ContentType.BirthDateFull` / `ContentType.CreditCardNumber`. `NONE`과 가린 칸에는 `contentType`을 붙이지 않는다.
- 넘친 입력: `pending`(`mutableStateOf<Pair<Int, String>?>`)을 `LaunchedEffect(pending)`에서 `distribute(digitsOnly(text), i + 1, states.values, lengths, masked)`로 다음 칸부터 채운다(`setAll`). 그 뒤 마지막으로 채운 칸 다음 빈 칸(없으면 마지막으로 채운 칸)으로 포커스. `enabled && !readOnly`일 때만.
- 자동 이동: 칸마다 `snapshotFlow { field.text, field.selection, field.composition }`을 보고, 길이 == 자리 수 && 커서가 끝 && `composition == null` && 방금 늘어났으면 다음 입력 칸 `requestFocus()`. 마지막 칸이면 그대로 둔다(스펙: 포커스 유지, 추가 입력 버림).
- 가린 칸 커서: 선택이 끝이 아니면 `edit { placeCursorAtEnd() }`.
- 키(`onPreviewKeyEvent`, `KeyDown`만, `enabled && !readOnly`일 때만):
  - Backspace + 이 칸이 빈 칸 + 앞 입력 칸 있음 → 앞 칸 마지막 글자 삭제, 앞 칸으로 포커스, `true` 반환.
  - → + 커서가 끝(또는 가린 칸) + 다음 입력 칸 있음 → 다음 칸 포커스(커서 처음, 가린 칸이면 끝).
  - ← + 커서가 처음(또는 가린 칸) + 앞 입력 칸 있음 → 앞 칸 포커스(커서 끝).
- 고정 점·placeholder 점: `Text("•".repeat(n))`, `fg.placeholder`(비활성 `fg.disabled`), `clearAndSetSemantics {}`. 구분자 `Text(" - ")` 같은 색, `clearAndSetSemantics {}`.
- 칸 너비: `fill`이면 `weight(1f)`, 아니면 `"0".repeat(length)`를 `t5Regular`로 잰 너비(`rememberTextMeasurer`). `centered`면 `TextAlign.Center`.
- 포커스 테두리: `emphasized = fields 중 하나라도 isFocused`(칸마다 `onFocusChanged`로 모음).

- [ ] **Step 1: 실패 테스트** (`SegmentedTextInputTest`, Robolectric. 칸 노드는 `hasContentDescription(label)`로 찾는다)

```kotlin
@Test fun fillingSegmentMovesFocusToNext() { /* 카드 첫 칸 performTextInput "1"×4(한 글자씩) → 둘째 칸 assertIsFocused */ }
@Test fun overLengthInsertInMiddleIsRejected() { /* 첫 칸 "1234", 커서 1로 옮기고 "9" 입력 → "1234" 그대로 */ }
@Test fun backspaceOnEmptyDeletesPreviousLastChar() { /* 첫 칸 "1234", 둘째 칸 포커스, performKeyInput { pressKey(Key.Backspace) } → 첫 칸 "123", 첫 칸 포커스 */ }
@Test fun arrowKeysMoveBetweenSegments() { /* 첫 칸 끝에서 DirectionRight → 둘째 칸, 둘째 칸 처음에서 DirectionLeft → 첫 칸 */ }
@Test fun maskedEditsOnlyAtEnd() { /* 주민 THIRTEEN 뒷칸 "123", 커서 0으로 옮기려 해도 끝(3), "4" 입력 → "1234" */ }
@Test fun pasteDistributesAndStopsBeforeMasked() { /* 카드 첫 칸 performTextReplacement("1234-5678-9012-3456") → values ["1234","5678","",""] */ }
@Test fun pasteIntoMaskedSegmentIsIgnored() { /* 카드 셋째 칸 포커스, performTextReplacement("90123456") → 셋째 "" */ }
@Test fun birthDateAutofillIsNormalized() { /* 주민 첫 칸 performTextReplacement("1990-01-01") → "900101", 다음 칸 포커스 */ }
@Test fun lastSegmentFullKeepsFocusAndDropsExtra() { /* 카드 넷째 "3456" 뒤 "7" 입력 → "3456", 넷째 포커스 유지 */ }
@Test fun disabledAndReadOnlyBlockInputAndMoves() { /* enabled=false: 입력 노드 assertIsNotEnabled. readOnly=true: 입력·Backspace 이동 무시, 포커스는 받음 */ }
@Test fun maskedValueNotInSemantics() { /* THIRTEEN 뒷칸 "1234567" → 어떤 노드의 EditableText·Text에도 "1234567"·"1234" 없음, Password 키 있음 */ }
@Test fun autofillContentTypeOnlyOnVisibleSegments() { /* 주민 첫 칸 ContentType == BirthDateFull, 카드 첫 칸 == CreditCardNumber, 가린 칸·카드 둘째 칸은 ContentType 키 없음 */ }
@Test fun inheritsFieldStateAndShowsInvalidStroke() { /* PushlogField(invalid=true, errorMessage="확인") → 바깥 노드 Error == "확인", 시계 200ms 뒤 테두리 픽셀 == stroke.criticalSolid */ }
@Test fun rtlKeepsSegmentOrder() { /* LocalLayoutDirection Rtl → 첫 칸 left < 둘째 칸 left, Backspace 동작 같음 */ }
@Test fun maskedStaysMaskedAfterRestore() { /* StateRestorationTester로 복원 뒤 화면 캡처 영역에 숫자 글자가 없고 '•'만, maskedValueNotInSemantics와 같은 검사 */ }
```

한글 조합 중 이동하지 않는 것은 Compose 테스트 API로 조합을 넣을 수 없어 자동 테스트가 아니다(스펙 §7 수동 확인). 대신 `compositionDoesNotAdvance`는 엔진 단위로 본다: `TextFieldState.edit { ... setComposition(0, 1) }`로 조합 구간을 직접 둔 뒤 자리 수를 채워도 포커스가 옮겨지지 않는다(`TextFieldBuffer.setComposition`이 이 Compose 버전에 공개돼 있지 않으면 이 테스트를 빼고 수동 확인만 남긴다. 빠지면 PR 본문에 적는다).

- [ ] **Step 2: 실패 확인** — `--tests '*SegmentedTextInputTest'`.
- [ ] **Step 3: 구현** — 위 엔진 규칙. `password()` 의미 정보가 자동완성에서 비밀번호 저장 제안을 일으키는지는 Step 7 기기 확인에서 본다.
- [ ] **Step 4: 스크린샷** — `statesLight`/`statesDark`: 주민 SIX·SEVEN·THIRTEEN(빈 값, 값), 카드 FOUR·EIGHT(빈 값, 값), 포커스, 오류, 비활성, 읽기 전용.
- [ ] **Step 5: 미리보기** — 파일 아래 `@PushlogPreview` 섹션(형식별, 상태). Key/Screen 두 쌍(형식 고르기 + field와 함께), entry, catalog. 미리보기 상태는 `rememberSaveable(saver = ...Saver) { ...State() }`로 만든다.
- [ ] **Step 6: 통과 확인** — `--tests '*segmentedtextinput*' :konsist:test` PASS.
- [ ] **Step 7: 기기 확인(자동완성)** — Google 자동완성이 켜진 기기에서: 생년월일·카드번호 제안이 보이는 칸에만 뜨는지, 값을 채우고 화면을 떠날 때 가린 칸 값이 저장 제안에 들어가는지. 가린 칸이 비밀번호로 저장 제안되면 멈추고 사용자에게 알린다(`password()` 대신 다른 가림 방식을 정해야 한다). 기기가 없으면 사용자에게 알린다.
- [ ] **Step 8: 커밋** — `feat: 주민등록번호·카드번호 입력 추가`

---

### Task 5: 문서, 검증, PR

**Files:**
- Modify: `docs/architecture/design-system/components/input.md`

- [ ] **Step 1: 문서** — `components/input.md`에 코드로 복원할 수 없는 것만: 두 형식만 공개하고 칸 엔진을 `internal`로 둔 이유, `BasicSecureTextField`를 쓰지 않은 이유(`ContentType.Password` 자동 부착), 가린 칸을 자동완성에서 뺀 이유, 저장 상태에 가린 값까지 남기는 사용자 결정(스펙 §5 링크), 칸 줄을 LTR로 고정한 이유, `clear()` 책임이 쓰는 화면에 있다는 것. `python3 docs/script/check_links.py docs` → 0건.
- [ ] **Step 2: 수동 확인(기기)** — Gboard·삼성 키보드에서 칸 안 한글 조합 중 다음 칸으로 넘어가지 않는지(숫자 키보드에서 한글 전환 시), 칸별 키보드 종류(가린 칸 숫자 비밀번호, 나머지 숫자), 소프트 키보드 Backspace로 빈 칸에서 앞 칸이 지워지는지, Task 4 Step 7 결과. PR 본문에 적는다.
- [ ] **Step 3: 전체 검증** — `./gradlew verifyStarter` → BUILD SUCCESSFUL.
- [ ] **Step 4: 커밋** — `docs: segmented 입력 결정 기록`
- [ ] **Step 5: PR(사용자 확인 뒤)** — `gh pr create --base develop --head feat/design-system-segmented-text-input --title "feat: add resident number and card number inputs"`. 본문은 PR 템플릿, `Part of #36`(PR2·PR3 중 마지막으로 머지되는 쪽은 `Closes #36`), 수동 확인 결과, 끝에 `🤖 Generated with [Claude Code](https://claude.com/claude-code)`.
