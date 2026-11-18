# 글자 입력 PR2 구현 계획 (search-input · input-button)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `core:designsystem`에 공개 `PushlogSearchInput`(`component.searchinput`), `PushlogInputButton`(`component.inputbutton`)과 둘이 같이 쓰는 내부 지우기 버튼(`component.clearbutton`)을 만든다.

**Architecture:** 지우기 버튼은 `PushlogPressableSurface`(투명, 자기 축소)에 SEED `IconXmarkCircleFill` 경로를 그린다. search-input은 `TextFieldState` 기반 `BasicTextField`에 회색 배경과 지우기 버튼을 붙이고 field 상태를 받지 않는다. input-button은 아래층에 탭·눌림 색·테두리를 맡는 `PushlogPressableSurface`, 위층에 내용 줄을 겹친다. 위층은 `pushlogPressScale`로 같은 `interactionSource`를 보며 내용만 줄고, 위층 안의 지우기 버튼은 자기 탭을 먼저 받아 아래층이 눌리지 않는다.

**Tech Stack:** Kotlin, Compose foundation(`BasicTextField(TextFieldState)`, `KeyboardActionHandler`), Robolectric + Roborazzi, Konsist, Navigation 3(`app-preview`).

**Spec:** `docs/superpowers/specs/2026-11-17-design-system-text-inputs-design.md` (이하 "스펙"). 이 계획은 스펙 §8 PR2만 다룬다(§6.1 searchinput·inputbutton·clearbutton 행, §6.4, §6.5, §6.8 지우기, §7 해당 줄). PR3과는 서로 의존하지 않는다.

## 시작하기 전에 읽을 것

1. 스펙 §6.4, §6.5, §6.8, §7
2. PR1 계획의 마지막 절 "PR2·PR3이 쓰는 인터페이스": `docs/superpowers/plans/2026-11-17-text-inputs-pr1.md`
3. `AGENTS.md`, `docs/code-conventions.md`(서브에이전트 브리프에 링크)
4. `docs/architecture/design-system/pressable.md`, `preview.md`, `components/input.md`(PR1이 만듦)
5. `.claude/rules/design-system-components.md`, `.claude/skills/git-flow/SKILL.md`
6. SEED(기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`): `packages/rootage/components/search-input.yaml`, `input-button.yaml`, `docs/registry/react/ui/search-input.tsx`, `field-button.tsx`, `packages/react/src/components/SearchInput/SearchInput.tsx`, 문서 `docs/content/react/components/search-input.mdx`, `field-button.mdx`
7. 기존 코드(PR1 머지 뒤 develop): `component/pressable/PushlogPressableSurface.kt`, `PushlogPressIndication.kt`(`pushlogPressScale`), `component/field/*`, `component/textinput/TextInputDecoration.kt`(`textInputStroke`), `component/checkbox/CheckmarkIconDrawing.kt`, 테스트 `component/chip/PushlogChipScreenshotTest.kt`(눌림 주입 캡처)

## Global Constraints

- 작업 위치: 메인 레포, 브랜치 `feat/design-system-search-input-button`(PR1 머지 뒤 `origin/develop`에서 땀). 워크트리는 만들지 않는다. PR 대상 `develop`.
- `AGENTS.md` 금지 사항, `docs/code-conventions.md`. `core:designsystem` 주석은 한국어, `app-preview`는 영어.
- 커밋: `type: 한국어 설명`, 빈 줄, `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- 파일 하나에 최상위 타입 하나. `clickable`·`pointerInput` 등은 `pressable` 밖에서 쓰지 않는다. 탭은 `PushlogPressableSurface`만.
- 공개 `@Composable`: `PushlogSearchInput`, `PushlogInputButton` → 각각 Key·Screen·entry·catalog. `ClearButton`은 `internal`이라 화면이 없다.
- 값 → 토큰: 4→`x1`, 12→`x3`, 16→`x4`, 18→`x4Half`(쓰지 않음, medium), 20→`x5`, 52→`x13`, r2.5→`radius.r2Half`, r3→`radius.r3`, 글자 `t5Regular`·`t6Regular`. 상수(SEED 출처 주석): search 높이 44dp, 지우기 아이콘 22dp(input-button large).
- 기본 문구는 `core/designsystem/src/main/res/values/strings.xml`에 둔다: `designsystem_search`("검색"), `designsystem_clear`("지우기"). 인자 기본값에서 `stringResource`로 읽는다.
- 입력값을 로그·예외 메시지에 넣지 않는다.
- 작업 중 `./gradlew :core:designsystem:testDebugUnitTest --tests '<클래스>'`, `./gradlew :konsist:test`. 마지막 커밋 전 `./gradlew verifyStarter`.
- 미리보기는 prevue로 확인. 기준 스크린샷은 커밋하지 않는다. push·PR은 사용자 확인 뒤.

## Review Focus

1. search-input 값이 공백뿐이어도 지우기 버튼이 보이고 지우면 빈다(값 있음 = 길이 > 0). → Task 3 `whitespaceValueShowsClear`.
2. search-input에 화면보다 긴 글을 넣어도 한 줄로 가로 스크롤되고, 지우기 버튼이 밀려나지 않는다. → Task 3 `longValueKeepsClearButtonVisible`.
3. input-button `value = ""`는 값 없음으로 본다. placeholder가 보이고 지우기 버튼이 없다. → Task 4 `emptyStringValueShowsPlaceholder`.
4. input-button 값이 길면 한 줄 말줄임이 되고 뒤쪽 아이콘·글자는 잘리지 않는다. → Task 4 `longValueEllipsizesAndKeepsSuffix`.
5. RTL에서 search-input 지우기 버튼은 왼쪽 끝, input-button 앞 아이콘은 오른쪽 끝으로 간다. → Task 3 `rtlMirrorsClearButton`, Task 4 `rtlMirrorsPrefix`.

---

### Task 1: 준비 — PR1 확인, 브랜치

**Files:** 없음(확인만)

- [ ] **Step 1: develop에 PR1이 들어왔는지 확인**

```bash
git fetch origin
P=core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component
git grep -n "LocalPushlogFieldState" origin/develop -- $P/field/PushlogFieldState.kt
git grep -n "fun Modifier.pushlogFieldSemantics" origin/develop -- $P/field/PushlogFieldState.kt
git grep -n "fun Modifier.textInputStroke" origin/develop -- $P/textinput/TextInputDecoration.kt
git grep -n "fun Modifier.pushlogPressScale" origin/develop -- $P/pressable/PushlogPressIndication.kt
```

Expected: 모두 한 줄 이상. 하나라도 비면 멈추고 사용자에게 알린다. 시그니처가 PR1 계획과 다르면 develop 코드를 따르고 다른 점을 알린다.

- [ ] **Step 2: 브랜치** — 메인 레포 작업 트리가 깨끗한지 `git status --short`로 보고, `git switch -c feat/design-system-search-input-button origin/develop`.
- [ ] **Step 3: 기준선** — `./gradlew testStarter` → BUILD SUCCESSFUL.

---

### Task 2: 내부 `ClearButton`

**Files:**
- Create: `.../component/clearbutton/ClearButton.kt`, `.../component/clearbutton/ClearIconDrawing.kt`
- Modify: `core/designsystem/src/main/res/values/strings.xml`
- Test: `.../component/clearbutton/ClearButtonTest.kt`

**Interfaces:**
- Consumes: `PushlogPressableSurface` (pressable).
- Produces:

```kotlin
// ClearIconDrawing.kt — 24 viewBox, PathFillType.EvenOdd
internal fun DrawScope.drawXmarkCircleIcon(color: Color)

// ClearButton.kt
/** 투명 표면에 지우기 아이콘을 그린다. 눌리면 자기만 줄어든다(SEED clearButton scaleScope: self). */
@Composable
internal fun ClearButton(
    onClick: () -> Unit,
    contentDescription: String,
    iconSize: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    touchPadding: Dp = 0.dp,
)
```

`PushlogPressableSurface(color = Color.Transparent, pressedColor = Color.Transparent, shape = CircleShape, enabled = true, onClick, modifier = modifier.semantics { this.contentDescription = contentDescription })` 안에 `Box(Modifier.padding(touchPadding)) { Canvas(Modifier.size(iconSize)) { drawXmarkCircleIcon(color) } }`.

아이콘 경로(`IconXmarkCircleFill`, `@karrotmarket/react-monochrome-icon` 1.35.0, Apache-2.0) — 그대로 옮긴다:

```
M 0.801 12 C 0.801 5.817 5.818 0.8 12.001 0.8 C 18.183 0.8 23.201 5.817 23.201 12 C 23.201 18.183 18.183 23.2 12.001 23.2 C 5.818 23.2 0.801 18.183 0.801 12 Z M 8.465 8.466 C 8.856 8.075 9.489 8.075 9.88 8.466 L 12.001 10.587 L 14.122 8.466 C 14.512 8.075 15.145 8.075 15.536 8.466 C 15.926 8.856 15.926 9.489 15.536 9.88 L 13.415 12.001 L 15.536 14.123 C 15.927 14.513 15.927 15.146 15.536 15.537 C 15.146 15.927 14.513 15.927 14.122 15.537 L 12.001 13.415 L 9.879 15.537 C 9.488 15.927 8.855 15.927 8.465 15.537 C 8.074 15.146 8.074 14.513 8.465 14.123 L 10.586 12.001 L 8.465 9.88 C 8.075 9.489 8.075 8.856 8.465 8.466 Z
```

- [ ] **Step 1: 실패 테스트**

```kotlin
@Test fun clickCallsOnClickOnce()
@Test fun hasButtonRoleAndLabel() { /* Role.Button, ContentDescription == ["지우기"] */ }
@Test fun touchPaddingEnlargesHitArea() { /* iconSize 20dp, touchPadding 12dp → 노드 크기 44dp, 가장자리(2dp,2dp) 클릭이 onClick 호출 */ }
@Test fun pressShrinksSelf() { /* Press 주입 뒤 아이콘 픽셀 너비 < 쉰 상태 */ }
```

- [ ] **Step 2: 실패 확인 → Step 3: 구현(문자열 두 개 추가 포함) → Step 4: 통과 확인(`--tests '*ClearButtonTest' :konsist:test`)**
- [ ] **Step 5: 커밋** — `feat: 입력 지우기 버튼 부품 추가`

---

### Task 3: `PushlogSearchInput`

**Files:**
- Create: `.../component/searchinput/PushlogSearchInput.kt`
- Test: `.../component/searchinput/PushlogSearchInputTest.kt`, `PushlogSearchInputScreenshotTest.kt`
- Create: `app-preview/.../PushlogSearchInputPreviewKey.kt`, `PushlogSearchInputPreviewScreen.kt`; Modify entry provider, catalog

**Interfaces:**
- Consumes: Task 2 `ClearButton`.
- Produces:

```kotlin
@Composable
fun PushlogSearchInput(
    state: TextFieldState,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    contentDescription: String = stringResource(R.string.designsystem_search),
    clearButtonLabel: String = stringResource(R.string.designsystem_clear),
    clearFocusOnSubmit: Boolean = true,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
)
```

- 레이아웃: `Row(Modifier.height(44dp).clip(r2Half).background(bg.neutralWeak).padding(start = x4, end = x1), verticalAlignment = CenterVertically)` = `BasicTextField(weight(1f))` + 값이 있을 때 `ClearButton(iconSize = x5, color = fg.neutralSubtle, touchPadding = x3)`.
- `BasicTextField(state, enabled, lineLimits = SingleLine, textStyle = t6Regular + fg.neutral(비활성 fg.disabled), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), onKeyboardAction = { onSubmit(state.text.toString()); if (clearFocusOnSubmit) focusManager.clearFocus() }, decorator = placeholder(t6Regular, fg.placeholder, 비활성 fg.disabled))`. 의미 정보: `contentDescription`.
- field 상태를 읽지 않는다. 다만 field 안에 놓여도 아래로 새지 않게 `ResetPushlogFieldState`로 감싼다.
- 지우기: `state.clearText()`만 한다. 포커스를 요청하거나 풀지 않는다(있던 포커스 유지, 없던 포커스 안 줌). 비활성이면 지우기 버튼을 그리지 않는다.

- [ ] **Step 1: 실패 테스트**

```kotlin
@Test fun clearButtonOnlyWhenValue() { /* "" → "지우기" 노드 없음, "a" → 있음 */ }
@Test fun whitespaceValueShowsClear() { /* " " → 있음, 누르면 text == "" */ }
@Test fun clearEmptiesValue()
@Test fun clearKeepsFocusWhenFocused() { /* requestFocus → 지우기 클릭 → 입력 assertIsFocused */ }
@Test fun clearDoesNotFocusWhenUnfocused() { /* 포커스 없이 지우기 → assertIsNotFocused */ }
@Test fun submitCallsOnceWithValue() { /* "커피" 입력, performImeAction() → 받은 값 목록 == ["커피"] */ }
@Test fun submitEmptyCallsWithEmptyString() { /* 빈 상태 performImeAction → [""] */ }
@Test fun clearFocusOnSubmitTrueReleasesFocus()
@Test fun clearFocusOnSubmitFalseKeepsFocus()
@Test fun defaultLabels() { /* 입력 ContentDescription == ["검색"], 버튼 ["지우기"] */ }
@Test fun ignoresFieldState() { /* PushlogField(enabled=false, invalid=true) { PushlogSearchInput } → assertIsEnabled, Error 키 없음 */ }
@Test fun disabledHidesClearAndRejectsInput()
@Test fun longValueKeepsClearButtonVisible() { /* 너비 200dp, 200자 입력 → 지우기 노드 boundsInRoot.right <= 200dp */ }
@Test fun rtlMirrorsClearButton() { /* LocalLayoutDirection Rtl → 지우기 노드 left < 입력 노드 left */ }
```

- [ ] **Step 2: 실패 확인 → Step 3: 구현**
- [ ] **Step 4: 스크린샷** — `statesLight`/`statesDark`: 빈 값+placeholder, 값, 비활성. 지우기 눌림(Press 주입) 하나.
- [ ] **Step 5: 미리보기** — 파일 아래 `@PushlogPreview` 섹션(빈 값, 값, 비활성). Key/Screen(섹션 "states", "submit"(제출한 값을 아래 글로 보여 줌)), entry, catalog.
- [ ] **Step 6: 통과 확인** — `--tests '*searchinput*' :konsist:test` PASS.
- [ ] **Step 7: 커밋** — `feat: search-input 추가`

---

### Task 4: `PushlogInputButton`

**Files:**
- Create: `.../component/inputbutton/PushlogInputButton.kt`
- Test: `.../component/inputbutton/PushlogInputButtonTest.kt`, `PushlogInputButtonScreenshotTest.kt`
- Create: `app-preview/.../PushlogInputButtonPreviewKey.kt`, `PushlogInputButtonPreviewScreen.kt`; Modify entry provider, catalog

**Interfaces:**
- Consumes: Task 2 `ClearButton`; PR1 `LocalPushlogFieldState`, `ResetPushlogFieldState`, `pushlogFieldSemantics`, `textInputStroke`; pressable `PushlogPressableSurface`, `pushlogPressScale`.
- Produces:

```kotlin
@Composable
fun PushlogInputButton(
    value: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    onClear: (() -> Unit)? = null,
    prefixIcon: Painter? = null,
    prefixText: String? = null,
    suffixText: String? = null,
    suffixIcon: Painter? = null,
    contentDescription: String? = null,
    invalid: Boolean = LocalPushlogFieldState.current.invalid,
    enabled: Boolean = LocalPushlogFieldState.current.enabled,
    readOnly: Boolean = LocalPushlogFieldState.current.readOnly,
    interactionSource: MutableInteractionSource? = null,
)
```

구조(스펙 §6.5):

```
Box(modifier.heightIn(min = x13)) {
  PushlogPressableSurface(               // 아래층: 탭, 눌림 색, 테두리
      Modifier.matchParentSize()
          .textInputStroke(OUTLINE, r3, emphasized = false, invalid)
          .pushlogFieldSemantics(field, invalid, contentDescription = 이름),
      color = if (!enabled || readOnly) bg.disabled else bg.transparent,
      pressedColor = bg.transparentPressed,
      shape = RoundedCornerShape(r3), enabled = enabled && !readOnly,
      onClick = onClick, interactionSource = source) {}
  Row(Modifier.pushlogPressScale(source, enabled && !readOnly)   // 위층: 내용만 축소
        .padding(horizontal = x4).heightIn(min = x13), spacedBy(x2Half), CenterVertically) {
    prefixIcon(x5, fg.neutralMuted) · prefixText(t5, fg.neutralSubtle)
    값 또는 placeholder(weight(1f), maxLines 1, Ellipsis, 값 fg.neutral / placeholder fg.placeholder, 비활성 fg.disabled)
    ClearButton(iconSize = 22dp, color = fg.neutralSubtle)  // 값 있음 && onClear != null && enabled && !readOnly
    suffixText · suffixIcon
  }
}
```

- `이름` = `listOfNotNull(contentDescription ?: field.label, value?.takeIf { it.isNotEmpty() } ?: placeholder).joinToString(", ")`. 위층 글자·아이콘은 `clearAndSetSemantics {}`로 따로 읽히지 않게 하고, 지우기 버튼만 의미 정보를 남긴다.
- 위층 `Row`에는 탭 modifier가 없어 빈 곳 터치는 아래층으로 간다. 지우기 버튼은 위층에 있어 자기 탭을 먼저 받고 아래층은 눌리지 않는다.
- 전체를 `ResetPushlogFieldState`로 감싼다(위층 안은 field 상태를 받지 않는다).

- [ ] **Step 1: 실패 테스트**

```kotlin
@Test fun clickCallsOnClick()
@Test fun clearCallsOnClearNotOnClick() { /* 지우기 클릭 → onClear 1회, onClick 0회 */ }
@Test fun pressingClearDoesNotPressSurface() {
    // autoAdvance=false. 지우기 노드에 performTouchInput { down(center) } → 시계 200ms
    // 표면 빈 곳 픽셀 == bg.transparent 위 배경색(눌림 색 아님), 값 글자 bounds 크기 == 누르기 전
}
@Test fun clearHiddenWhenNoValueOrNoOnClearOrDisabledOrReadOnly()
@Test fun emptyStringValueShowsPlaceholder()
@Test fun readOnlyIsNotClickableAndUsesDisabledBackground() { /* onClick 0회, 표면 픽셀 == bg.disabled, 값 색 == fg.neutral */ }
@Test fun invalidDrawsCriticalStroke() { /* 시계 200ms 뒤 테두리 픽셀 == stroke.criticalSolid */ }
@Test fun semanticsNameIsLabelPlusValue() { /* PushlogField(label="생일") { PushlogInputButton(value="1990.01.01") } → Role.Button, ContentDescription == ["생일, 1990.01.01"], Text 노드 "1990.01.01" 없음 */ }
@Test fun contentDescriptionWinsOverFieldLabel() { /* contentDescription="생년월일" → ["생년월일, 1990.01.01"] */ }
@Test fun placeholderUsedInNameWhenNoValue()
@Test fun inheritsFieldState() { /* PushlogField(enabled=false) → onClick 0회 */ }
@Test fun longValueEllipsizesAndKeepsSuffix() { /* 너비 200dp, 긴 값 + suffixText "원" → "원" 노드(useUnmergedTree) right <= 200dp - x4 */ }
@Test fun rtlMirrorsPrefix()
```

- [ ] **Step 2: 실패 확인 → Step 3: 구현**
- [ ] **Step 4: 스크린샷** — `statesLight`/`statesDark`: placeholder, 값, 값+지우기, 앞뒤 요소, 오류, 읽기 전용, 비활성, 눌림(Press 주입 뒤 시계 진행).
- [ ] **Step 5: 미리보기** — 파일 아래 `@PushlogPreview`(기본, 지우기, 상태). Key/Screen(누르면 값이 바뀌는 예시, field와 함께), entry, catalog.
- [ ] **Step 6: 통과 확인** — `--tests '*inputbutton*' :konsist:test` PASS.
- [ ] **Step 7: 커밋** — `feat: input-button 추가`

---

### Task 5: 문서, 검증, PR

**Files:**
- Modify: `docs/architecture/design-system/components/input.md`

- [ ] **Step 1: 문서** — `components/input.md`에 코드로 복원할 수 없는 것만 더한다: search-input이 field 상태를 받지 않는 이유, 빈 값도 `onSubmit("")`을 부르는 이유(SEED와 같음), input-button을 두 층으로 겹친 이유(지우기 버튼이 바깥 눌림을 일으키지 않게), 지우기 버튼이 자기만 줄어드는 이유. `python3 docs/script/check_links.py docs` → 0건.
- [ ] **Step 2: 수동 확인(기기)** — Gboard·삼성 키보드에서 search-input에 한글을 조합하는 중에 검색 키를 누르면 조합만 확정되고 `onSubmit`은 확정 뒤 한 번만 불리는지. TalkBack에서 지우기 버튼을 누르면 접근성 포커스가 입력으로 가는지. 결과를 PR 본문에 적는다. 기기가 없으면 사용자에게 알리고 "미확인"으로 적는다.
- [ ] **Step 3: 전체 검증** — `./gradlew verifyStarter` → BUILD SUCCESSFUL.
- [ ] **Step 4: 커밋** — `docs: search-input·input-button 결정 기록`
- [ ] **Step 5: PR(사용자 확인 뒤)** — `gh pr create --base develop --head feat/design-system-search-input-button --title "feat: add search input and input button"`. 본문은 PR 템플릿, `Part of #36`(PR2·PR3 중 마지막으로 머지되는 쪽은 `Closes #36`), 수동 확인 결과, 끝에 `🤖 Generated with [Claude Code](https://claude.com/claude-code)`.
