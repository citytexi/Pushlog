# 선택 컨트롤 PR1 구현 계획 (pressable 확장 · 체크박스 · 라디오)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `core:designsystem`의 `pressable`에 §6.1 부품 여섯 가지를 모두 더하고, 그 위에 체크 마크·체크박스·체크박스 그룹, 라디오 마크·라디오·라디오 그룹을 SEED 정의대로 만든다.

**Architecture:** 줄(Row) 전체에 시각 효과 없는 탭 영역 modifier를 붙이고, 마크에는 같은 `MutableInteractionSource`를 보는 눌림 표시 modifier(축소 + 표면 색)를 붙인다. 탭 판정은 축소 층 바깥(줄)에 있어서 줄지 않고, 마크만 줄어든다. PR2·PR3이 쓸 `PushlogPressableSurface` 확장(내용 축소, 단일 선택, 눌림 테두리)과 역할 인자도 이 PR에서 만들고 단위 테스트로 검증한다.

**Tech Stack:** Kotlin, Jetpack Compose foundation, Robolectric + Roborazzi, Konsist, Navigation 3(`app-preview`).

**Spec:** `docs/superpowers/specs/2026-11-16-design-system-selection-controls-design.md` (이하 "스펙"). 이 계획은 스펙 §7의 PR1만 다룬다.

## 시작하기 전에 읽을 것

1. 스펙 전체: `docs/superpowers/specs/2026-11-16-design-system-selection-controls-design.md`
2. `AGENTS.md` (금지 사항, 검증)
3. `docs/code-conventions.md` (주석·KDoc. 서브에이전트 브리프에 이 링크를 넣는다)
4. `docs/architecture/design-system.md`의 "동작 컴포넌트", "프리뷰 스크린샷", "미리보기 앱" 절
5. `.claude/rules/design-system-components.md`
6. SEED 정의(기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`). 필요한 값은 각 Task에 옮겨 두었다. 표에 없는 것을 확인할 때만 연다.
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/checkbox.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/checkbox-group.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/checkmark.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/radio.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/radio-group.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/radiomark.yaml`
   - recipe(기본값·상태 조합): `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/qvism-preset/src/recipes/<name>.ts` (`<name>` = `checkbox`, `checkbox-group`, `checkmark`, `radio`, `radio-group`, `radiomark`)
7. 기존 코드: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/*.kt`, `component/chip/*.kt`(Size·Variant 멤버 패턴), `core/designsystem/src/test/.../component/pressable/PushlogPressableTest.kt`(픽셀 측정, Press 주입), `component/chip/PushlogChipScreenshotTest.kt`(`autoAdvance = false` 눌림 캡처), `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogChipPreview{Key,Screen}.kt`.

## Global Constraints

- 작업 위치: 메인 레포 `/Users/jeonheehoon/Documents/work_station/github/Pushlog`, 브랜치 `feat/design-system-selection-controls`(이미 있음, develop 기준). PR 대상은 `develop`, 본문에 `Part of #35`.
- `AGENTS.md` 금지 사항을 지킨다. `@Suppress`, detekt baseline, ktlint 비활성화, Konsist·`ModuleRules` 약화는 금지다. 규칙이 막으면 구조를 고친다.
- 주석·KDoc은 `docs/code-conventions.md`를 따른다. `core:designsystem`은 한국어, `app-preview`는 기존 파일처럼 영어로 쓴다. 코드가 이미 말하는 것은 쓰지 않는다.
- 커밋 메시지: `type: 한국어 설명`, 빈 줄, `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- 파일 하나에 최상위 class/interface/object/enum은 하나다(`FileStructureRulesTest`). 최상위 함수는 여러 개 둘 수 있다.
- 컴포넌트는 `component.checkbox`, `component.radio`에 둔다. `toggleable`·`triStateToggleable`·`selectable`·`clickable`·`indication`·`pointerInput`은 `component.pressable` 안에서만 쓴다(`ComponentRulesTest`). `Modifier.selectableGroup()`은 탭 처리가 아니라서 컴포넌트 패키지에서 써도 된다(규칙의 정규식 `selectable\s*[({]`에 걸리지 않는다).
- 크기 값은 `PushlogXxxSize.metrics()`가 `XxxMetrics`(`internal class`)로, 색은 enum의 `internal` 멤버가 `else` 없이 `when (this)`로 돌려준다. `XxxDefaults`는 만들지 않는다.
- SEED px 값은 같은 크기 토큰으로 옮긴다(4→`x1`/`r1`, 8→`x2`, 10→`x2Half`, 12→`x3`, 14→`x3Half`, 18→`x4Half`, 20→`x5`, 24→`x6`, 32→`x8`, 36→`x9`). 토큰이 없는 값(1px 선)만 파일 안 `private val`에 SEED 출처 주석을 단다. 새 foundation 토큰은 만들지 않는다.
- 팔레트 값은 `pushlogThemedColor(LightPalette.x, DarkPalette.x)`(`component/button/PushlogButtonSupport.kt`, `internal`)로 읽는다.
- 기본값: 스펙 §6.3. variant `SQUARE`, size `MEDIUM`, weight `REGULAR`, tone `NEUTRAL`(recipe의 brand 대신).
- ripple·state layer 금지. 줄 전체가 줄어들면 안 되고 마크만 준다. 라벨을 탭해도 상태가 바뀐다.
- 공개 `@Composable`마다 `app-preview`에 `<함수>PreviewKey`, `<함수>PreviewScreen.kt`(entry 함수 + 본문 + 섹션별 `@PushlogPreview`), `PreviewEntryProvider` 등록, `PreviewCatalog` 행. 섹션 하나가 320x470 캔버스(2배 글자 포함)를 넘지 않게 나눈다.
- 작업 중 `./gradlew :core:designsystem:testDebugUnitTest --tests '<클래스>'`, `./gradlew :konsist:test`, 큰 단위로 `./gradlew testStarter`. 마지막 커밋 전 `./gradlew verifyStarter`. 포맷은 `./gradlew ktlintFormat`.
- 개발 중 미리보기는 prevue로 렌더링해 확인한다(`prevue` skill). 준비 승인을 물으면 사용자에게 묻는다.
- 기준 스크린샷(`build/outputs/roborazzi`)은 커밋하지 않는다.

## Review Focus

1. 글자 크기 2배에서 라벨 줄 높이가 줄 최소 높이(32/36dp)보다 커진다. 라벨 위쪽 보정 `(minHeight - lineHeight) / 2`가 음수가 되어 레이아웃이 깨지거나 예외가 나면 안 되고, 줄이 늘어나야 한다. → Task 5 `largeFontScaleGrowsRow`.
2. 라벨이 여러 줄이면 마크는 세로 가운데가 아니라 첫 줄에 붙어 있어야 한다(SEED `alignItems: flex-start` + 위치 보정). → Task 5 `multiLineLabelKeepsMarkOnFirstLine`.
3. 호출하는 쪽이 `interactionSource`를 넘기지 않아도 줄과 마크가 같은 소스를 써야 한다. 주입이 아니라 실제 터치로 줄을 눌렀을 때 마크가 줄어야 한다. → Task 5·7 `realPressOnLabelShrinksMark`.
4. 마크와 라벨이 탭 대상 둘로 갈라지면 안 된다(스크린 리더가 둘로 읽고, 라벨 탭이 무시된다). → Task 5·7 `rowIsSingleTapTarget`.
5. 비활성 줄에 눌림이 들어와도(주입) 마크가 줄거나 색이 바뀌면 안 된다. → Task 2 `disabledIndicationIgnoresPress`, Task 5 `disabledIgnoresTap`.

---

### Task 1: 준비 — 스펙과 계획 확인

스펙과 계획 세 개는 계획을 쓴 세션에서 이 브랜치(`feat/design-system-selection-controls`)에 `docs:` 커밋으로 이미 들어가 있다.

- [ ] **Step 1: 브랜치와 파일 확인**

Run: `git branch --show-current && git log --oneline -- docs/superpowers/plans/2026-11-16-selection-controls-pr1.md | head -1 && git status --short`
Expected: 브랜치 `feat/design-system-selection-controls`, 커밋 한 줄이 나온다, 작업 트리가 깨끗하다. 커밋이 없거나 다른 브랜치면 멈추고 사용자에게 묻는다.

---

### Task 2: pressable — 탭 영역 modifier, 눌림 표시 modifier, 토글 역할 인자 (§6.1-1, 2, 4)

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PushlogPressableArea.kt`
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PushlogPressIndication.kt`
- Modify: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PushlogPressable.kt` (`pushlogToggleablePressable`에 `role`)
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PushlogPressableAreaTest.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PushlogPressIndicationTest.kt`

**Interfaces:**
- Consumes: `PressScaleNode.Element(interactionSource, enabled, loading)`, `PressColorNode.Element(interactionSource, color, pressedColor, enabled, loading)` (기존).
- Produces (모두 `internal`, 패키지 `component.pressable`):

```kotlin
// PushlogPressableArea.kt — 시각 효과 없는 탭 영역. 모두 indication = null.
internal fun Modifier.pushlogTriStateToggleArea(
    state: ToggleableState,
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    onClick: () -> Unit,
): Modifier // triStateToggleable, Role.Checkbox

internal fun Modifier.pushlogSelectableArea(
    selected: Boolean,
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    onClick: () -> Unit,
): Modifier // selectable, Role.RadioButton

internal fun Modifier.pushlogToggleableArea(
    value: Boolean,
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    role: Role,
    onValueChange: (Boolean) -> Unit,
): Modifier // toggleable, 역할은 호출하는 쪽(Role.Switch, Role.Checkbox)

// PushlogPressIndication.kt — 탭 처리 없는 눌림 표시.
internal fun Modifier.pushlogPressScale(
    interactionSource: InteractionSource,
    enabled: Boolean,
): Modifier // then(PressScaleNode.Element(interactionSource, enabled, loading = false))

internal fun Modifier.pushlogPressIndication(
    interactionSource: InteractionSource,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    scale: Boolean = true,
): Modifier // (scale이면 pushlogPressScale) → clip(shape) → PressColorNode.Element(..., loading = false)

// PushlogPressable.kt — 기존 함수에 role 추가(기본값으로 칩 동작 유지)
internal fun Modifier.pushlogToggleablePressable(
    interactionSource: MutableInteractionSource,
    selected: Boolean,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    role: Role = Role.Checkbox,
    shadow: Modifier = Modifier,
): Modifier
```

`pushlogPressIndication` 뒤에 붙인 modifier(테두리, 내용)는 축소 층 안이라 함께 준다. KDoc에 그 점과 "탭 영역은 이 modifier 바깥(줄)에 둔다"를 적는다.

- [ ] **Step 1: 실패하는 테스트 작성 — `PushlogPressableAreaTest`** (`@RunWith(RobolectricTestRunner::class)`, `@GraphicsMode(NATIVE)`)

```kotlin
@Test fun triStateAreaIsCheckboxWithState()
// Box(Modifier.size(40.dp).pushlogTriStateToggleArea(ToggleableState.Indeterminate, source, true) { clicks++ })
// config[SemanticsProperties.Role] == Role.Checkbox
// config[SemanticsProperties.ToggleableState] == ToggleableState.Indeterminate
// performClick() 뒤 clicks == 1

@Test fun selectableAreaIsRadioButton()
// selected = true → Role.RadioButton, assertIsSelected(); performClick() → clicks == 1

@Test fun toggleableAreaUsesGivenRole()
// role = Role.Switch, value = false → Role.Switch, assertIsOff(); performClick() → 받은 값 == true

@Test fun disabledAreasIgnoreTaps()
// 세 modifier 각각 enabled = false로 performClick() → 콜백 0회, assertIsNotEnabled()

@Test fun toggleablePressableRoleIsArgument()
// Box(...pushlogToggleablePressable(..., role = Role.Switch)) → Role.Switch
// role 생략 → Role.Checkbox
```

- [ ] **Step 2: 실패하는 테스트 작성 — `PushlogPressIndicationTest`**

`PushlogPressableTest`의 `press()`·`release()`·`paintedWidth()` 방식(가운데 가로줄 픽셀 수, `PressInteraction.Press` 직접 주입)을 따른다. 루트 `Box(Modifier.size(40.dp), Alignment.Center)` 안에 `Box(Modifier.size(20.dp).pushlogPressIndication(source, BASE, PRESSED, RectangleShape, enabled, scale))`.

```kotlin
@Test fun indicationShrinksAndSwapsColor()
// scale = true: press 뒤 PRESSED 픽셀 수 < 누르기 전 BASE 픽셀 수, 가운데 픽셀 == PRESSED
// 20dp 마크의 기준 길이는 24dp라 배율은 pressedScaleFactor(20d, 20d, d) == 22/24

@Test fun indicationWithoutScaleChangesColorOnly()
// scale = false: press 뒤 PRESSED 픽셀 수 == 누르기 전 BASE 픽셀 수(20dp 그대로), 가운데 픽셀 == PRESSED

@Test fun disabledIndicationIgnoresPress()
// enabled = false: press 뒤 픽셀 수 그대로, 가운데 픽셀 == BASE

@Test fun pressScaleShrinksWithoutDrawing()
// Box(Modifier.size(20.dp).pushlogPressScale(source, true).background(BASE)): press 뒤 BASE 픽셀 수가 준다
```

- [ ] **Step 3: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogPressableAreaTest' --tests '*PushlogPressIndicationTest'`
Expected: 컴파일 실패(`pushlogTriStateToggleArea` 등 없음).

- [ ] **Step 4: 구현**

Interfaces 블록의 시그니처대로 만든다. 탭 영역 셋은 `triStateToggleable`/`selectable`/`toggleable`에 `indication = null`과 고정 역할을 넘기는 한 줄짜리다. `pushlogToggleablePressable`은 `role`을 `toggleable`에 넘긴다.

- [ ] **Step 5: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*pressable*'`
Expected: PASS(기존 `PushlogPressableTest`, `PushlogChipTest` 포함).

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable
git commit -m "feat: pressable에 탭 영역과 눌림 표시 modifier를 나눠 더함

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 3: pressable — 표면의 내용 축소, 단일 선택, 눌림 테두리 (§6.1-3, 5, 6)

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PressScaleScope.kt`
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PressableSelectionMode.kt`
- Modify: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PushlogPressable.kt`
- Modify: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PushlogPressableSurface.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable/PushlogPressableSurfaceTest.kt`

**Interfaces:**
- Consumes: Task 2의 `pushlogToggleablePressable(..., role)`.
- Produces:

```kotlin
/** 눌렸을 때 줄어드는 대상. SEED `scaleScope`. */
internal enum class PressScaleScope {
    /** 표면 전체(그림자·배경·테두리·내용)가 준다. */
    SELF,

    /** 표면 색·테두리는 그대로 두고 안의 내용만 준다. 배율은 표면 크기로 계산한다. */
    CONTENT,
}

/** [PushlogPressableSurface]의 selected가 null이 아닐 때 쓰는 선택 방식. */
internal enum class PressableSelectionMode {
    /** toggleable, Role.Checkbox. 칩, 다중 선택 select-box. */
    TOGGLE,

    /** selectable, Role.RadioButton. 세그먼트 항목, 단일 선택 select-box. */
    SINGLE,
}

// PushlogPressable.kt — 새 함수
internal fun Modifier.pushlogSelectablePressable(
    interactionSource: MutableInteractionSource,
    selected: Boolean,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    shadow: Modifier = Modifier,
    scaleSelf: Boolean = true,
): Modifier // selectable(indication = null, role = Role.RadioButton) + pressableSurface

// pushlogPressable, pushlogToggleablePressable에도 마지막 인자로 scaleSelf: Boolean = true를 더한다.
// private pressableSurface(..., scaleSelf)는 scaleSelf가 false면 PressScaleNode를 건너뛴다.

@Composable
internal fun PushlogPressableSurface(
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    selected: Boolean? = null,
    selectionMode: PressableSelectionMode = PressableSelectionMode.TOGGLE,
    scaleScope: PressScaleScope = PressScaleScope.SELF,
    border: BorderStroke? = null,
    pressedBorder: BorderStroke? = null,
    shadow: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
)
```

동작:
- `selected == null`이면 `selectionMode`는 무시한다(버튼). `selected != null`이면 `TOGGLE` → `pushlogToggleablePressable`, `SINGLE` → `pushlogSelectablePressable`.
- `scaleScope == SELF`: 지금과 같은 트리(`Box(contentAlignment = Center) { content() }`). 기존 컴포넌트의 픽셀이 바뀌면 안 된다.
- `scaleScope == CONTENT`: 표면 modifier에는 `scaleSelf = false`를 넘기고, 트리를 `Box(modifier..., contentAlignment = Center, propagateMinConstraints = true) { Box(Modifier.then(PressScaleNode.Element(source, enabled, loading)), contentAlignment = Center) { content() } }`로 만든다. 안쪽 상자가 표면의 최소 제약을 그대로 받으므로 크기가 표면과 같고, `PressScaleNode`가 자기 크기로 잰 배율이 곧 표면 크기 기준 배율이 된다(SEED `scale-feedback.ts`: "The ratio still comes from the measured element's size"). `PressScaleNode`는 고치지 않는다.
- `pressedBorder`: `source.collectIsPressedAsState()`로 눌림을 읽고 `Modifier.border(pressedBorder.width, if (pressed && enabled) pressedBorder.brush else SolidColor(Color.Transparent), shape)`를 `border` 다음에 둔다. `pressedBorder == null`이면 상태를 모으지 않는다. 눌림 테두리를 쓰는 곳(세그먼트, 항목 최대 4개)은 적어서 재구성 비용은 문제 되지 않는다.

- [ ] **Step 1: 기존 스크린샷 기준 기록 (바꾸기 전 커밋에서)**

```bash
git stash -u
./gradlew recordRoborazziDebug
git stash pop
```
Expected: 기록 성공. 이 Task의 코드를 아직 쓰지 않았다면 stash할 것이 없으니 `git stash -u`·`pop`은 건너뛴다(Task 2까지는 커밋됨). 기록은 바뀌기 전 SELF 경로의 기준이다.

- [ ] **Step 2: 실패하는 테스트 작성 — `PushlogPressableSurfaceTest`** (`@Config(qualifiers = "w600dp-h600dp")`, Robolectric, NATIVE)

```kotlin
@Test fun contentScaleUsesSurfaceSize()
// PushlogPressableSurface(color = BASE, pressedColor = PRESSED, shape = RectangleShape, enabled = true, onClick = {},
//   modifier = Modifier.size(300.dp, 40.dp), scaleScope = PressScaleScope.CONTENT, interactionSource = source)
//   { Box(Modifier.size(100.dp, 20.dp).background(CONTENT)) }
// press 주입 + 300ms 진행 뒤, 가운데 가로줄에서
//   CONTENT 픽셀 수 == round(100 * d * pressedScaleFactor(300 * d, 40 * d, d)) ± 1   // 기준 75dp → 73/75
//   CONTENT 픽셀 수 != round(100 * d * pressedScaleFactor(100 * d, 20 * d, d))        // 내용 자기 크기 기준(23/25)이면 실패
//   (BASE 또는 PRESSED 픽셀 수) + CONTENT 픽셀 수 == 300 * d ± 1                       // 표면은 줄지 않는다

@Test fun contentScaleKeepsSurfaceColorSwap()
// 같은 설정에서 press 뒤 (5dp, 가운데) 픽셀 == PRESSED

@Test fun singleSelectionSurfaceIsRadioButton()
// selected = true, selectionMode = SINGLE → Role.RadioButton, assertIsSelected(),
// config에 SemanticsProperties.ToggleableState 없음, performClick() → clicks == 1

@Test fun toggleSelectionSurfaceStaysCheckbox()
// selected = false, selectionMode 생략 → Role.Checkbox, assertIsOff()

@Test fun pressedBorderShowsOnlyWhilePressed()
// size 100dp, scaleScope = CONTENT(표면이 줄지 않게), pressedBorder = BorderStroke(4.dp, BORDER)
// 누르기 전 (1dp, 가운데) 픽셀 == BASE → press 뒤 == BORDER → release 뒤 == BASE

@Test fun pressedBorderHiddenWhenDisabled()
// enabled = false, press 주입 → (1dp, 가운데) 픽셀 == BASE
```

- [ ] **Step 3: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogPressableSurfaceTest'`
Expected: 컴파일 실패(`PressScaleScope` 없음).

- [ ] **Step 4: 구현**

Interfaces 블록대로 만든다. `PushlogPressableSurface` KDoc에 `scaleScope`, `selectionMode`, `pressedBorder`가 무엇을 바꾸는지 한 줄씩 더한다.

- [ ] **Step 5: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*pressable*' --tests '*chip*' --tests '*button*'`
Expected: PASS.

- [ ] **Step 6: 기존 스크린샷이 그대로인지 확인**

Run: `./gradlew verifyRoborazziDebug`
Expected: 차이 0건. 차이가 나면 SELF 경로 트리가 바뀐 것이다. SELF 경로를 원래 코드와 같게 되돌린다.

- [ ] **Step 7: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/pressable
git commit -m "feat: 눌림 표면에 내용 축소, 단일 선택, 눌림 테두리를 더함

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 4: 체크 마크 `PushlogCheckmark`

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox/PushlogCheckmarkVariant.kt`
- Create: `.../component/checkbox/PushlogCheckmarkTone.kt`
- Create: `.../component/checkbox/PushlogCheckmarkSize.kt`
- Create: `.../component/checkbox/CheckmarkMetrics.kt`
- Create: `.../component/checkbox/CheckmarkIconDrawing.kt`
- Modify: `core/designsystem/licenses/seed-design-NOTICE` (아이콘 경로 출처 한 단락)
- Create: `.../component/checkbox/PushlogCheckmark.kt`
- Create: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogCheckmarkPreviewKey.kt`
- Create: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogCheckmarkPreviewScreen.kt`
- Modify: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/navigation/PreviewEntryProvider.kt`, `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/catalog/PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox/CheckmarkContrastTest.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox/PushlogCheckmarkScreenshotTest.kt`

**Interfaces:**
- Consumes: `Modifier.pushlogPressIndication(interactionSource, color, pressedColor, shape, enabled, scale)` (Task 2), `pushlogThemedColor(light, dark)`.
- Produces:

```kotlin
enum class PushlogCheckmarkVariant { SQUARE, GHOST }   // KDoc: SQUARE 필수 선택·인지 필요, GHOST 선택 사항·3개 이하 (checkmark.yaml 설명)
enum class PushlogCheckmarkTone { NEUTRAL, BRAND }
enum class PushlogCheckmarkSize { MEDIUM, LARGE }      // 체크박스 줄 크기도 이 enum이다

internal class CheckmarkMetrics(
    val markSize: Dp,
    val cornerRadius: Dp,
    val squareIconSize: Dp,
    val ghostIconSize: Dp,
    val rowMinHeight: Dp,
    val regularLabelStyle: TextStyle,
    val boldLabelStyle: TextStyle,
)

// PushlogCheckmarkSize
@Composable internal fun metrics(): CheckmarkMetrics

// PushlogCheckmarkTone (when (this))
@Composable internal fun solid(pressed: Boolean): Color
@Composable internal fun onSolid(): Color
@Composable internal fun ghostSelectedIcon(): Color
@Composable internal fun ghostSelectedPressed(): Color

// PushlogCheckmarkVariant (when (this)); on = state != ToggleableState.Off
@Composable internal fun rootColor(tone: PushlogCheckmarkTone, on: Boolean, enabled: Boolean): Color
@Composable internal fun rootPressedColor(tone: PushlogCheckmarkTone, on: Boolean): Color
@Composable internal fun strokeColor(on: Boolean): Color?          // null이면 테두리 없음
@Composable internal fun iconColor(tone: PushlogCheckmarkTone, on: Boolean, enabled: Boolean): Color?  // null이면 아이콘 숨김
internal fun iconSize(metrics: CheckmarkMetrics): Dp

// CheckmarkIconDrawing.kt — 그리는 영역(size)을 24x24 viewBox로 보고 채워 그린다. PR3 select-box 체크 마크도 쓴다.
internal fun DrawScope.drawCheckIcon(color: Color)
internal fun DrawScope.drawDashIcon(color: Color)

@Composable
fun PushlogCheckmark(
    state: ToggleableState,
    modifier: Modifier = Modifier,
    variant: PushlogCheckmarkVariant = PushlogCheckmarkVariant.SQUARE,
    tone: PushlogCheckmarkTone = PushlogCheckmarkTone.NEUTRAL,
    size: PushlogCheckmarkSize = PushlogCheckmarkSize.MEDIUM,
    enabled: Boolean = true,
    scaleOnPress: Boolean = true,
    interactionSource: InteractionSource? = null,
)
```

값(SEED checkmark.yaml, checkbox.yaml):

| | MEDIUM | LARGE |
|---|---|---|
| `markSize` | `x5` (20) | `x6` (24) |
| `cornerRadius` | `r1` (4) | `r1` (4) |
| `squareIconSize` | `x3` (12) | `x3Half` (14) |
| `ghostIconSize` | `x3Half` (14) | `x4Half` (18) |
| `rowMinHeight` | `x8` (32) | `x9` (36) |
| 라벨 | `t4Regular` / `t4Bold` | `t5Regular` / `t5Bold` |

색(`on` = On 또는 Indeterminate, 비활성은 눌림 없음 — `PressColorNode`가 막는다):

| 슬롯 | SQUARE | GHOST |
|---|---|---|
| root, off | `bg.transparent` | `bg.transparent` |
| root, on | `tone.solid(false)` | `bg.transparent` |
| root, 비활성(on/off 모두) | `bg.disabled` | `bg.transparent` |
| root 눌림, off | `bg.transparentPressed` | `bg.transparentPressed` |
| root 눌림, on | `tone.solid(true)` | `tone.ghostSelectedPressed()` |
| 테두리 | off: 1dp `stroke.neutralWeak`(비활성도 같음), on: 없음 | 없음 |
| 아이콘, off | 숨김 | `fg.placeholder` |
| 아이콘, on | `tone.onSolid()` | `tone.ghostSelectedIcon()` |
| 아이콘, 비활성 | on이면 `fg.disabled`, off면 숨김 | `fg.disabled` |

tone 멤버:

| 멤버 | NEUTRAL | BRAND |
|---|---|---|
| `solid(false)` / `solid(true)` | `bg.neutralSolid` / `bg.neutralSolidPressed` | `bg.brandSolid` / `bg.brandSolidPressed` |
| `onSolid()` | `fg.onNeutralSolid` | `fg.onBrandSolid` |
| `ghostSelectedIcon()` | `fg.neutral` | `fg.brand` |
| `ghostSelectedPressed()` | `pushlogThemedColor(LightPalette.gray200, DarkPalette.gray200)` | `pushlogThemedColor(LightPalette.forest200, DarkPalette.forest200)` |

`ghostSelectedPressed()` BRAND의 근거(스펙 §6.3): SEED는 `palette.carrot-200`이다. 다크 단계는 다크 `fg.brand`(`forest700` `#39AA66`)와의 대비가 3:1 이상인 단계로 정한다. 이 계획을 쓸 때 잰 값은 `forest100` 5.05, `forest200` 4.36, `forest300` 3.43, `forest400` 2.56이고 라이트 `forest200`(`#DEF5E7`) 대 `fg.brand`(`forest600` `#20874A`)는 3.96이다. 그래서 SEED와 같은 단계인 `forest200`을 라이트·다크 모두 쓴다. 구현 뒤 `CheckmarkContrastTest`로 다시 재고, 값이 다르면 3:1을 넘는 가장 가까운 단계로 바꾼다.

그리기:
- `Box(modifier.size(markSize).pushlogPressIndication(source, rootColor, rootPressedColor, RoundedCornerShape(cornerRadius), enabled, scale = scaleOnPress).then(테두리), contentAlignment = Center) { 아이콘 }`. 테두리는 `pushlogPressIndication` 뒤라 함께 준다. 1dp는 파일 안 `private val StrokeWidth = 1.dp`(SEED checkmark.yaml `strokeWidth: 1px`).
- `interactionSource == null`이면 `remember { MutableInteractionSource() }`를 쓴다(눌리지 않는 마크).
- 아이콘은 `Canvas(Modifier.size(iconSize)) { drawCheckIcon(color) }`(Indeterminate는 `drawDashIcon`)로 그린다. GHOST off도 체크 모양(`fg.placeholder`)이다.
- `CheckmarkIconDrawing.kt`: SEED 체크박스 스니펫(`docs/registry/react/ui/checkbox.tsx`)이 쓰는 `@karrotmarket/react-monochrome-icon` 1.35.0(Apache-2.0)의 `IconCheckmarkFatFill`, `IconMinusFatFill` 경로를 그대로 옮긴다. 선이 아니라 채운 모양이다. `PathParser().parsePathString(d).toPath()`를 파일 안 `private val`로 한 번만 만들고, 그릴 때 `scale(size.width / 24f, size.height / 24f, pivot = Offset.Zero) { drawPath(path, color) }`. 경로 문자열은 `private const val`에 출처 주석과 함께 둔다.
  - 체크 `d`: `M 22.552 3.164 C 21.814 2.583 20.745 2.711 20.164 3.448 L 9.563 16.912 L 3.727 10.823 C 3.078 10.146 2.001 10.123 1.324 10.773 C 0.646 11.422 0.623 12.498 1.273 13.176 L 8.462 20.676 C 8.803 21.032 9.28 21.222 9.772 21.198 C 10.263 21.174 10.72 20.938 11.025 20.552 L 22.836 5.552 C 23.416 4.814 23.289 3.745 22.552 3.164 Z`
  - 대시 `d`: `M 4 10.3 C 3.061 10.3 2.3 11.061 2.3 12 C 2.3 12.939 3.061 13.7 4 13.7 H 20 C 20.939 13.7 21.7 12.939 21.7 12 C 21.7 11.061 20.939 10.3 20 10.3 H 4 Z`
- `seed-design-NOTICE` 끝에 한 단락: 체크·대시 경로는 `@karrotmarket/react-monochrome-icon` 1.35.0(Apache-2.0)의 `IconCheckmarkFatFill`, `IconMinusFatFill`에서 옮겼다(Copyright 주식회사 당근마켓, Apache License 2.0).
- 아이콘 색은 `animateColorAsState(target, tween(PushlogTheme.motion.duration.colorTransition, easing = PushlogTheme.motion.timingFunction.easing))`. 숨김일 때는 그리지 않는다.
- 마크는 의미 정보를 두지 않는다. KDoc: 탭을 처리하지 않는다, 단독으로 쓰면 호출하는 쪽이 줄에 `toggleable(indication = null)`을 붙이고 같은 `interactionSource`를 넘긴다, 감싸는 쪽이 이미 눌림 피드백을 주면 `scaleOnPress = false`.
- 파일 아래쪽에 `@PushlogPreview private fun PushlogCheckmarkPreview()`(variant × On/Off/Indeterminate 한 장, 칩 파일과 같은 방식).

미리보기 앱:
- `PushlogCheckmarkPreviewKey`(`@Serializable data object ... : NavKey`), `pushlogCheckmarkPreviewEntry(onBack)`, `PushlogCheckmarkPreviewContent()`.
- 섹션: variant마다 하나(tone 두 줄 × Off/On/Indeterminate/비활성 Off/비활성 On), size마다 하나. 섹션마다 `@PushlogPreview`(`PushlogCheckmarkSquarePreview`, `PushlogCheckmarkGhostPreview`, `PushlogCheckmarkSizeMediumPreview`, `PushlogCheckmarkSizeLargePreview`).
- `previewEntryProvider`에 `pushlogCheckmarkPreviewEntry(onBack)`, `previewCatalog`에 `PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogCheckmark", PushlogCheckmarkPreviewKey)`.

- [ ] **Step 1: 실패하는 테스트 작성 — `CheckmarkContrastTest`** (순수 JVM)

```kotlin
@Test fun lightGhostBrandPressedSelectedReadsAtLeast3To1()
// contrast(LightPushlogColors.fg.brand, LightPalette.forest200) >= 3.0   (계획 작성 시 3.96)
@Test fun darkGhostBrandPressedSelectedReadsAtLeast3To1()
// contrast(DarkPushlogColors.fg.brand, DarkPalette.forest200) >= 3.0     (계획 작성 시 4.36)
// contrast(a, b) = (max(La, Lb) + 0.05) / (min(La, Lb) + 0.05), L = androidx.compose.ui.graphics.luminance()
```

이 테스트는 지금도 통과한다(팔레트 값 고정 검사). 실패 확인 대신 Step 3에서 컴포넌트와 함께 돌린다.

- [ ] **Step 2: 눌림 스크린샷 테스트 작성 — `PushlogCheckmarkScreenshotTest`**

`PushlogChipScreenshotTest` 구조를 따른다(`@Config(qualifiers = "w1200dp-h1200dp")`, `autoAdvance = false`, Press 주입 뒤 `advanceTimeBy(800)`, 한 번만 캡처).
- `pressedLight()` → `build/outputs/roborazzi/checkmark_pressed_light.png`, `pressedDark()` → `checkmark_pressed_dark.png`.
- 줄: variant × tone마다 한 줄, 칸은 눌림 Off / 눌림 On / 눌림 Indeterminate. 칸마다 `MutableInteractionSource`를 따로 둔다.

- [ ] **Step 3: 구현 후 실행**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*CheckmarkContrastTest' --tests '*PushlogCheckmarkScreenshotTest'`
Expected: PASS. 캡처 이미지를 열어 GHOST 눌림 On이 라이트 연초록·다크 진초록 바탕 위 초록 체크인지 본다.

- [ ] **Step 4: 미리보기 확인**

prevue로 `PushlogCheckmarkPreviewScreen.kt`를 렌더링해 체크·대시 모양과 크기(12/14, 14/18)를 SEED 그림과 비교한다. 2배 글자 프리뷰가 캔버스를 넘지 않는지 본다.

- [ ] **Step 5: 규칙 테스트**

Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS(`PreviewCoverageRulesTest`, `PreviewEntryProviderTest` 포함).

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/licenses/seed-design-NOTICE core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: 체크 마크 PushlogCheckmark 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 5: 체크박스 `PushlogCheckbox`, 체크박스 그룹 `PushlogCheckboxGroup`

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox/PushlogCheckboxWeight.kt`
- Create: `.../component/checkbox/PushlogCheckbox.kt` (오버로드 둘)
- Create: `.../component/checkbox/PushlogCheckboxGroup.kt`
- Create: `app-preview/.../preview/component/PushlogCheckboxPreviewKey.kt`, `PushlogCheckboxPreviewScreen.kt`, `PushlogCheckboxGroupPreviewKey.kt`, `PushlogCheckboxGroupPreviewScreen.kt`
- Modify: `PreviewEntryProvider.kt`, `PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox/PushlogCheckboxTest.kt`

**Interfaces:**
- Consumes: `Modifier.pushlogTriStateToggleArea(state, interactionSource, enabled, onClick)` (Task 2), `PushlogCheckmark(...)`, `PushlogCheckmarkSize.metrics()` (Task 4).
- Produces:

```kotlin
enum class PushlogCheckboxWeight {
    REGULAR,
    BOLD,
    ;

    internal fun labelStyle(metrics: CheckmarkMetrics): TextStyle // REGULAR → regularLabelStyle, BOLD → boldLabelStyle
}

@Composable
fun PushlogCheckbox(
    state: ToggleableState,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    variant: PushlogCheckmarkVariant = PushlogCheckmarkVariant.SQUARE,
    tone: PushlogCheckmarkTone = PushlogCheckmarkTone.NEUTRAL,
    size: PushlogCheckmarkSize = PushlogCheckmarkSize.MEDIUM,
    weight: PushlogCheckboxWeight = PushlogCheckboxWeight.REGULAR,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
)

/** 편의 오버로드. ToggleableState(checked)로 위 함수를 부르고 onClick에서 onCheckedChange(!checked). */
@Composable
fun PushlogCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    variant: PushlogCheckmarkVariant = PushlogCheckmarkVariant.SQUARE,
    tone: PushlogCheckmarkTone = PushlogCheckmarkTone.NEUTRAL,
    size: PushlogCheckmarkSize = PushlogCheckmarkSize.MEDIUM,
    weight: PushlogCheckboxWeight = PushlogCheckboxWeight.REGULAR,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
)

@Composable
fun PushlogCheckboxGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) // Column(modifier.selectableGroup(), verticalArrangement = spacedBy(x1))
```

줄 레이아웃(SEED checkbox recipe `alignItems: flex-start`와 위치 보정 두 개를 옮긴 것):
- `val source = interactionSource ?: remember { MutableInteractionSource() }` 하나를 줄과 마크에 같이 넘긴다.
- `Row(modifier.pushlogTriStateToggleArea(state, source, enabled, onClick).heightIn(min = metrics.rowMinHeight), horizontalArrangement = spacedBy(x2), verticalAlignment = Alignment.Top)`.
- 마크: `Modifier.padding(top = (rowMinHeight - markSize) / 2)`.
- 라벨: `Text(label, style = weight.labelStyle(metrics), color = if (enabled) fg.neutral else fg.disabled, modifier = Modifier.weight(1f, fill = false).padding(top = ((rowMinHeight - lineHeight.toDp()) / 2).coerceAtLeast(0.dp)))`. `maxLines` 없음(줄바꿈 허용).
- KDoc: 부모의 일부 선택(Indeterminate)은 호출하는 쪽이 계산한다, 줄 전체가 탭 하나다.
- `@PushlogPreview` 하나씩 각 파일 아래쪽에(칩 방식).

미리보기 앱:
- `PushlogCheckboxPreviewScreen`: 섹션 = variant × tone(상태를 `remember`로 토글, 비활성 줄 포함), size × weight, 긴 라벨(`Modifier.width(200.dp)`, 여러 줄). 섹션별 `@PushlogPreview`.
- `PushlogCheckboxGroupPreviewScreen`: "전체 선택" 부모(자식 상태로 On/Off/Indeterminate 계산) + 자식 세 개. 섹션 하나, `@PushlogPreview` 하나.
- entry·catalog 행 둘(`"PushlogCheckbox"`, `"PushlogCheckboxGroup"`).

- [ ] **Step 1: 실패하는 테스트 작성 — `PushlogCheckboxTest`** (Robolectric, NATIVE)

마크 픽셀은 라벨 앞 `x < markSize` 구간에서 `bg.neutralSolid` 색(state On, SQUARE, NEUTRAL, 라이트)을 센다. 색 값은 컴포지션 안에서 `PushlogTheme.color.bg.neutralSolid`로 읽어 둔다.

```kotlin
@Test fun labelTapTogglesState()
// 오버로드(checked)로 상태를 호이스팅, onNodeWithText("알림").performClick() → assertIsOn(); 다시 → assertIsOff()

@Test fun exposesCheckboxRoleAndTriState()
// state = Indeterminate → Role.Checkbox, config[ToggleableState] == Indeterminate

@Test fun rowIsSingleTapTarget()
// onAllNodes(hasClickAction()).assertCountEquals(1)

@Test fun disabledIgnoresTap()
// enabled = false, performClick() → onClick 0회, assertIsNotEnabled()
// 같은 줄에 Press 주입 → 마크 픽셀 수 그대로

@Test fun pressShrinksMarkOnly()
// interactionSource에 Press 주입 + 300ms
// 마크 픽셀 수 < 누르기 전
// onNodeWithText(label, useUnmergedTree = true).captureToImage() 픽셀이 누르기 전과 같다

@Test fun realPressOnLabelShrinksMark()
// interactionSource 넘기지 않음. onNodeWithText(label).performTouchInput { down(center) }, advanceTimeBy(300)
// 마크 픽셀 수 < 누르기 전 (줄과 마크가 같은 소스를 쓴다)

@Test fun multiLineLabelKeepsMarkOnFirstLine()
// Box(Modifier.width(120.dp)) 안에 세 줄 넘는 라벨
// 마크 색 픽셀이 처음 나오는 y == (32 - 20) / 2 dp * density ± 1

@Test fun largeFontScaleGrowsRow()
// LocalDensity fontScale = 2f → 줄 높이 > 32dp * density, 예외 없음
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogCheckboxTest'`
Expected: 컴파일 실패(`PushlogCheckbox` 없음).

- [ ] **Step 3: 구현** (Interfaces 블록과 줄 레이아웃대로)

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*checkbox*'`
Expected: PASS.

- [ ] **Step 5: 미리보기와 규칙**

prevue로 두 화면을 렌더링해 마크가 라벨 첫 줄과 세로로 맞는지, 2배 글자에서 잘리지 않는지 본다.
Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: 체크박스와 체크박스 그룹 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 6: 라디오 마크 `PushlogRadiomark`

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/radio/PushlogRadiomarkTone.kt`
- Create: `.../component/radio/PushlogRadiomarkSize.kt`
- Create: `.../component/radio/RadiomarkMetrics.kt`
- Create: `.../component/radio/PushlogRadiomark.kt`
- Create: `app-preview/.../preview/component/PushlogRadiomarkPreviewKey.kt`, `PushlogRadiomarkPreviewScreen.kt`
- Modify: `PreviewEntryProvider.kt`, `PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/radio/PushlogRadiomarkScreenshotTest.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/radio/PushlogRadiomarkTest.kt`

**Interfaces:**
- Consumes: `Modifier.pushlogPressIndication(...)` (Task 2), `pushlogThemedColor`.
- Produces:

```kotlin
enum class PushlogRadiomarkTone { NEUTRAL, BRAND }
enum class PushlogRadiomarkSize { MEDIUM, LARGE }     // 라디오 줄 크기도 이 enum이다

internal class RadiomarkMetrics(
    val markSize: Dp,
    val dotSize: Dp,
    val disabledDotSize: Dp,
    val rowMinHeight: Dp,
    val regularLabelStyle: TextStyle,
    val boldLabelStyle: TextStyle,
)

// PushlogRadiomarkSize
@Composable internal fun metrics(): RadiomarkMetrics

// PushlogRadiomarkTone (when (this))
@Composable internal fun rootColor(selected: Boolean, enabled: Boolean): Color
@Composable internal fun rootPressedColor(selected: Boolean): Color
@Composable internal fun strokeColor(selected: Boolean, enabled: Boolean): Color?   // null이면 테두리 없음
@Composable internal fun dotColor(enabled: Boolean): Color

@Composable
fun PushlogRadiomark(
    selected: Boolean,
    modifier: Modifier = Modifier,
    tone: PushlogRadiomarkTone = PushlogRadiomarkTone.NEUTRAL,
    size: PushlogRadiomarkSize = PushlogRadiomarkSize.MEDIUM,
    enabled: Boolean = true,
    scaleOnPress: Boolean = true,
    interactionSource: InteractionSource? = null,
)
```

값(SEED radiomark.yaml, radio.yaml):

| | MEDIUM | LARGE |
|---|---|---|
| `markSize` | `x5` (20) | `x6` (24) |
| `dotSize` | `x2` (8) | `x2Half` (10) |
| `disabledDotSize` | `x2Half` (10) | `x3` (12) |
| `rowMinHeight` | `x8` (32) | `x9` (36) |
| 라벨 | `t4Regular` / `t4Bold` | `t5Regular` / `t5Bold` |

모양은 `PushlogTheme.radius.full`(원). 색(SEED recipe는 비활성에 tone 구분 없이 brand 값을 쓰고, 두 tone 값이 같다):

| 슬롯 | 켜짐 아님 | 선택 |
|---|---|---|
| root | `bg.transparent` | NEUTRAL `bg.neutralSolid` / BRAND `bg.brandSolid` |
| root 눌림 | `bg.transparentPressed` | NEUTRAL `bg.neutralSolidPressed` / BRAND `bg.brandSolidPressed` |
| root 비활성 | `pushlogThemedColor(LightPalette.gray300, DarkPalette.gray300)` | `bg.transparent` |
| 테두리 | 1dp `stroke.neutralWeak`(비활성도 같음) | 없음. 비활성이면 1dp `pushlogThemedColor(LightPalette.gray300, DarkPalette.gray300)` |
| 점 | 그리지 않음 | NEUTRAL `fg.onNeutralSolid` / BRAND `fg.onBrandSolid`. 비활성이면 gray300(위와 같은 themed 값), 크기 `disabledDotSize` |

그리기: `Box(modifier.size(markSize).pushlogPressIndication(source, rootColor, rootPressedColor, radius.full, enabled, scale = scaleOnPress).then(테두리), contentAlignment = Center) { if (selected) Box(Modifier.size(dot).background(dotColor, radius.full)) }`. 1dp는 `private val StrokeWidth = 1.dp`(SEED radiomark.yaml `strokeWidth: 1px`). KDoc은 체크 마크와 같은 세 가지(탭 없음, 단독 사용법, `scaleOnPress`). 파일 아래 `@PushlogPreview` 하나.

미리보기 앱: 섹션 = tone마다(꺼짐/선택/비활성 꺼짐/비활성 선택) 하나, size마다 하나. 섹션별 `@PushlogPreview`. entry·catalog 행(`"PushlogRadiomark"`).

- [ ] **Step 1: 테스트 작성**

`PushlogRadiomarkTest`(Robolectric, NATIVE):

```kotlin
@Test fun noScaleKeepsSizeWhilePressed()
// PushlogRadiomark(selected = true, scaleOnPress = false, interactionSource = source), Press 주입
// 점(8dp) 위쪽 가로줄(마크 가운데에서 6dp 위)의 bg.neutralSolid·bg.neutralSolidPressed 픽셀 수 == 누르기 전
// 같은 줄 가운데 픽셀 == bg.neutralSolidPressed (색만 바뀐다)
// PR3 select-box가 이 조합(MEDIUM, NEUTRAL, scaleOnPress = false)을 쓴다.

@Test fun disabledSelectedDotIsLarger()
// 비활성 선택 MEDIUM의 점 지름(가운데 줄 gray300 픽셀 중 가운데 연속 구간) == 10dp * density ± 1
```

`PushlogRadiomarkScreenshotTest`: 체크 마크 스크린샷 테스트와 같은 구조. `radiomark_pressed_light.png`, `radiomark_pressed_dark.png`. 줄 = tone마다, 칸 = 눌림 꺼짐 / 눌림 선택.

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogRadiomark*'`
Expected: 컴파일 실패.

- [ ] **Step 3: 구현** (Interfaces와 값 표대로)

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogRadiomark*'`
Expected: PASS.

- [ ] **Step 5: 미리보기와 규칙**

prevue로 렌더링해 비활성 점이 커지는지, 다크 gray300이 보이는지 본다.
Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/radio core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/radio app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: 라디오 마크 PushlogRadiomark 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 7: 라디오 `PushlogRadio`, 라디오 그룹 `PushlogRadioGroup`

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/radio/PushlogRadioWeight.kt`
- Create: `.../component/radio/PushlogRadio.kt`
- Create: `.../component/radio/PushlogRadioGroup.kt`
- Create: `app-preview/.../preview/component/PushlogRadioPreviewKey.kt`, `PushlogRadioPreviewScreen.kt`, `PushlogRadioGroupPreviewKey.kt`, `PushlogRadioGroupPreviewScreen.kt`
- Modify: `PreviewEntryProvider.kt`, `PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/radio/PushlogRadioTest.kt`

**Interfaces:**
- Consumes: `Modifier.pushlogSelectableArea(selected, interactionSource, enabled, onClick)` (Task 2), `PushlogRadiomark(...)`, `PushlogRadiomarkSize.metrics()` (Task 6).
- Produces:

```kotlin
enum class PushlogRadioWeight {
    REGULAR,
    BOLD,
    ;

    internal fun labelStyle(metrics: RadiomarkMetrics): TextStyle
}

@Composable
fun PushlogRadio(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    tone: PushlogRadiomarkTone = PushlogRadiomarkTone.NEUTRAL,
    size: PushlogRadiomarkSize = PushlogRadiomarkSize.MEDIUM,
    weight: PushlogRadioWeight = PushlogRadioWeight.REGULAR,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
)

@Composable
fun PushlogRadioGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) // Column(modifier.selectableGroup(), verticalArrangement = spacedBy(x1))
```

줄 레이아웃은 Task 5와 같다(간격 `x2`, `Alignment.Top`, 마크 위 `(rowMinHeight - markSize) / 2`, 라벨 위 `((rowMinHeight - lineHeight) / 2).coerceAtLeast(0.dp)`, 라벨 색 `fg.neutral`/비활성 `fg.disabled`). 탭 영역만 `pushlogSelectableArea`다.

미리보기 앱: `PushlogRadioPreviewScreen` 섹션 = tone마다(선택을 `remember`로 바꾸는 세 줄 + 비활성 두 줄), size × weight, 긴 라벨. `PushlogRadioGroupPreviewScreen` = 세 항목 단일 선택 하나. 섹션별 `@PushlogPreview`, entry·catalog 행 둘.

- [ ] **Step 1: 실패하는 테스트 작성 — `PushlogRadioTest`**

```kotlin
@Test fun labelTapSelects()
// 세 라디오를 상태 하나로 묶고 onNodeWithText("둘째").performClick() → "둘째" assertIsSelected(), "첫째" assertIsNotSelected()

@Test fun exposesRadioButtonRole()
// config[SemanticsProperties.Role] == Role.RadioButton

@Test fun rowIsSingleTapTarget()
// onAllNodes(hasClickAction()).assertCountEquals(1)

@Test fun disabledIgnoresTap()
// enabled = false, performClick() → onClick 0회

@Test fun realPressOnLabelShrinksMark()
// Task 5와 같은 방식, selected = true, 마크 색 bg.neutralSolid

@Test fun groupIsSelectableGroup()
// PushlogRadioGroup(Modifier.testTag("group")) → config.contains(SemanticsProperties.SelectableGroup)
// PushlogCheckboxGroup도 같은 검사를 PushlogCheckboxTest에 하나 더한다(checkboxGroupIsSelectableGroup)
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogRadioTest' --tests '*PushlogCheckboxTest'`
Expected: 컴파일 실패.

- [ ] **Step 3: 구현**

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*radio*' --tests '*checkbox*'`
Expected: PASS.

- [ ] **Step 5: 미리보기와 규칙**

Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: 라디오와 라디오 그룹 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 8: 규칙 확인, 문서, 전체 검증

**Files:**
- Modify: `docs/architecture/design-system.md`
- Modify: `.claude/rules/design-system-components.md`

- [ ] **Step 1: Konsist가 새 패키지를 잡는지 확인 (커밋하지 않음)**

1. `PushlogCheckbox.kt`에 `import androidx.compose.foundation.selection.toggleable`을 더한다 → `./gradlew :konsist:test` → `only the pressable package handles taps` FAIL을 확인 → 되돌린다.
2. `PushlogRadioPreviewScreen.kt` 이름을 잠시 `PushlogRadioPreviewScreenX.kt`로 바꾼다 → `./gradlew :konsist:test` → `every public design system component has a preview screen` FAIL을 확인 → 되돌린다.
3. `git status`로 남은 변경이 없는지 본다.

- [ ] **Step 2: `docs/architecture/design-system.md` 고치기**

- "동작 컴포넌트" 절 첫 줄의 컴포넌트 목록에 선택 컨트롤 스펙 링크(`../superpowers/specs/2026-11-16-design-system-selection-controls-design.md`)를 더하고, "새 동작 컴포넌트는 … `PushlogPressableSurface`에 내용을 채운다" 문장에 "탭 영역과 축소 대상이 다른 컨트롤(체크박스·라디오 줄)은 `pressable`의 탭 영역 modifier와 눌림 표시 modifier를 같은 `interactionSource`로 묶는다"를 더한다.
- 새 절 `## 선택 컨트롤`(동작 컴포넌트 절 다음). 코드로 복원할 수 없는 것만:
  - 패키지 `checkbox`(`PushlogCheckmark`, `PushlogCheckbox`, `PushlogCheckboxGroup`), `radio`(`PushlogRadiomark`, `PushlogRadio`, `PushlogRadioGroup`). 마크는 공개하고 탭을 처리하지 않는다. 이유: #22 리스트 줄이 조합한다.
  - 줄 전체가 탭 영역이고 마크만 준다(SEED). 탭 판정이 축소 층 바깥이라 `edgeTapSurvivesShrink` 규칙이 그대로 지켜진다. 20dp 마크는 기준 24dp라 22/24로 준다.
  - `pressable` 부품 표: 탭 영역 셋(`pushlogTriStateToggleArea`, `pushlogSelectableArea`, `pushlogToggleableArea`), 눌림 표시 둘(`pushlogPressIndication`, `pushlogPressScale`), 표면 인자 셋(`scaleScope`, `selectionMode`, `pressedBorder`), 토글 역할 인자. 각각 누가 쓰는지(PR2·PR3 대상 포함). 내용 축소 배율이 표면 크기 기준인 이유(SEED `scale-feedback.ts`)와, 안쪽 상자가 표면 최소 제약을 받아 크기가 같아지는 방식.
  - 기본값: recipe `defaultVariants`, tone만 `NEUTRAL`(SEED 문서 권장).
  - 상태 3개(`ToggleableState`)와 부모 일부 선택은 호출하는 쪽 계산.
  - 체크·대시는 SEED 체크박스가 쓰는 채운 아이콘(`IconCheckmarkFatFill`, `IconMinusFatFill`)의 경로를 `Canvas`로 그린다(아이콘 자원·의존을 늘리지 않음). 그리기 함수는 `CheckmarkIconDrawing.kt`에 두어 select-box 체크 마크와 같이 쓴다.
  - 라벨 여러 줄일 때 마크는 첫 줄에 붙는다(SEED 위치 보정), 2배 글자에서 보정이 음수면 0.
  - ghost "선택 + 눌림" 마크 배경: SEED `carrot-200` 대신 brand `forest200`(라이트·다크), neutral `gray200`. Task 4에서 잰 대비(라이트 `fg.brand` 대 `forest200`, 다크 `fg.brand` 대 `forest200`, 그리고 탈락한 다크 단계 값)를 적고 `CheckmarkContrastTest`가 3:1을 강제한다고 적는다.
- "프리뷰 스크린샷" 절의 직접 쓴 스크린샷 테스트 목록에 `PushlogCheckmarkScreenshotTest`, `PushlogRadiomarkScreenshotTest`(눌림 주입)를 더한다.

- [ ] **Step 3: `.claude/rules/design-system-components.md` 고치기**

"눌리는 컴포넌트는 `PushlogPressableSurface`에 내용을 채운다" 줄에 "줄 전체가 탭이고 마크만 줄어드는 컨트롤은 `pressable`의 탭 영역 modifier + 눌림 표시 modifier를 같은 `interactionSource`로 쓴다"를 더한다. SKILL/rules 고정 구조는 그대로 둔다.

- [ ] **Step 4: 링크 검사**

Run: `python3 docs/script/check_links.py docs`
Expected: 0건.

- [ ] **Step 5: 전체 검증**

Run: `./gradlew ktlintFormat && ./gradlew verifyStarter`
Expected: BUILD SUCCESSFUL. 실패하면 고치고 다시 돌린다. 건너뛰고 완료를 선언하지 않는다.

- [ ] **Step 6: 커밋**

```bash
git add docs/architecture/design-system.md .claude/rules/design-system-components.md
git commit -m "docs: 선택 컨트롤과 pressable 확장 부품을 디자인 시스템 문서에 남김

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 9: push와 PR (사용자 확인 후)

push와 PR 생성은 사용자에게 먼저 묻고, 승인을 받은 뒤에만 한다.

- [ ] **Step 1: 사용자 확인**

커밋 목록(`git log --oneline develop..HEAD`)과 `verifyStarter` 결과를 보여 주고 push·PR을 해도 되는지 묻는다.

- [ ] **Step 2: push**

Run: `git push -u origin feat/design-system-selection-controls`

- [ ] **Step 3: PR 생성**

`.github/PULL_REQUEST_TEMPLATE.md`의 절을 따라 본문을 쓴다(한국어). Summary에 pressable 확장 여섯 가지와 체크박스·라디오 여섯 컴포넌트, Related Issue에 `Part of #35`(닫지 않는다), Notes for Reviewer에 ghost 선택+눌림 색 결정과 대비 값, PR2·PR3이 쓸 부품(내용 축소·단일 선택·눌림 테두리)은 단위 테스트로만 검증했다는 점. 본문 끝 줄은 `🤖 Generated with [Claude Code](https://claude.com/claude-code)`.

```bash
gh pr create --base develop --head feat/design-system-selection-controls \
  --title "feat: add checkbox and radio selection controls with pressable extensions" \
  --body-file <본문 파일>
```

Expected: PR URL. UI diff 봇 코멘트가 달리면 새 프리뷰·스크린샷을 확인한다.

---

## PR2·PR3이 쓰는 인터페이스

PR1이 머지된 develop에 아래가 있다. 모두 `core:designsystem` 모듈 안에서만 보인다(`internal`), `PushlogRadiomark`·enum은 공개다.

패키지 `com.citytexi.pushlog.core.designsystem.component.pressable`:

```kotlin
// 탭 영역 (시각 효과 없음, indication = null)
internal fun Modifier.pushlogTriStateToggleArea(state: ToggleableState, interactionSource: MutableInteractionSource, enabled: Boolean, onClick: () -> Unit): Modifier          // Role.Checkbox
internal fun Modifier.pushlogSelectableArea(selected: Boolean, interactionSource: MutableInteractionSource, enabled: Boolean, onClick: () -> Unit): Modifier                 // Role.RadioButton
internal fun Modifier.pushlogToggleableArea(value: Boolean, interactionSource: MutableInteractionSource, enabled: Boolean, role: Role, onValueChange: (Boolean) -> Unit): Modifier  // 스위치 줄은 Role.Switch

// 눌림 표시 (탭 처리 없음). 뒤에 붙인 modifier는 축소 층 안이라 함께 준다.
internal fun Modifier.pushlogPressScale(interactionSource: InteractionSource, enabled: Boolean): Modifier   // 축소만. 스위치 마크(눌림 색 없음)
internal fun Modifier.pushlogPressIndication(interactionSource: InteractionSource, color: Color, pressedColor: Color, shape: Shape, enabled: Boolean, scale: Boolean = true): Modifier  // 축소(끌 수 있음) + clip + 표면 색

// 표면
internal enum class PressScaleScope { SELF, CONTENT }               // CONTENT: 표면 색·테두리 고정, 내용만 표면 크기 기준 배율로 축소
internal enum class PressableSelectionMode { TOGGLE, SINGLE }       // selected != null일 때. TOGGLE = toggleable/Role.Checkbox, SINGLE = selectable/Role.RadioButton

@Composable
internal fun PushlogPressableSurface(
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    selected: Boolean? = null,
    selectionMode: PressableSelectionMode = PressableSelectionMode.TOGGLE,
    scaleScope: PressScaleScope = PressScaleScope.SELF,
    border: BorderStroke? = null,
    pressedBorder: BorderStroke? = null,        // 눌린 동안(enabled일 때)만 그린다. 세그먼트 항목 1dp stroke.neutralMuted
    shadow: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
)

// 표면 modifier (PushlogPressableSurface가 고른다. 직접 쓸 일은 드물다)
internal fun Modifier.pushlogPressable(interactionSource: MutableInteractionSource, color: Color, pressedColor: Color, shape: Shape, enabled: Boolean, loading: Boolean, onClick: () -> Unit, role: Role = Role.Button, shadow: Modifier = Modifier, scaleSelf: Boolean = true): Modifier
internal fun Modifier.pushlogToggleablePressable(interactionSource: MutableInteractionSource, selected: Boolean, color: Color, pressedColor: Color, shape: Shape, enabled: Boolean, onClick: () -> Unit, role: Role = Role.Checkbox, shadow: Modifier = Modifier, scaleSelf: Boolean = true): Modifier
internal fun Modifier.pushlogSelectablePressable(interactionSource: MutableInteractionSource, selected: Boolean, color: Color, pressedColor: Color, shape: Shape, enabled: Boolean, onClick: () -> Unit, shadow: Modifier = Modifier, scaleSelf: Boolean = true): Modifier

// 기존(바뀌지 않음)
internal fun pressedScaleFactor(widthPx: Float, heightPx: Float, density: Float): Float
internal fun CoroutineScope.launchPressTracking(source: InteractionSource, onChange: (Boolean) -> Unit): Job
```

패키지 `com.citytexi.pushlog.core.designsystem.component.radio` (PR3 단일 선택 select-box는 `size = MEDIUM, tone = NEUTRAL, scaleOnPress = false`로 고정해 쓰고, 카드의 `interactionSource`를 넘겨 색만 바뀌게 한다):

```kotlin
enum class PushlogRadiomarkTone { NEUTRAL, BRAND }
enum class PushlogRadiomarkSize { MEDIUM, LARGE }

@Composable
fun PushlogRadiomark(
    selected: Boolean,
    modifier: Modifier = Modifier,
    tone: PushlogRadiomarkTone = PushlogRadiomarkTone.NEUTRAL,
    size: PushlogRadiomarkSize = PushlogRadiomarkSize.MEDIUM,
    enabled: Boolean = true,
    scaleOnPress: Boolean = true,
    interactionSource: InteractionSource? = null,
)
```

그 밖에 PR2·PR3이 기대도 되는 것:
- `pushlogThemedColor(light: Color, dark: Color): Color` (`component.button.PushlogButtonSupport.kt`, `internal`, `@Composable`) — 팔레트 값 읽기.
- 체크 마크 `PushlogCheckmark(state: ToggleableState, modifier, variant, tone, size, enabled, scaleOnPress, interactionSource)` (`component.checkbox`). PR3 다중 선택 표시는 이것이 아니라 `select-box-checkmark` 정의를 쓰는 별도 `internal` 부품이다(스펙 §6.2).
- 체크·대시 그리기 `internal fun DrawScope.drawCheckIcon(color: Color)`, `internal fun DrawScope.drawDashIcon(color: Color)` (`component.checkbox.CheckmarkIconDrawing.kt`). 그리는 영역을 24x24 viewBox로 보고 채워 그린다. PR3 select-box 체크 마크가 이것을 부르고 PR1 파일은 고치지 않는다.
- 줄 레이아웃 규칙(마크 위 `(rowMinHeight - markSize) / 2`, 라벨 위 `((rowMinHeight - lineHeight) / 2).coerceAtLeast(0.dp)`, `Alignment.Top`, 간격 `x2`)과 테스트 패턴(`realPressOnLabelShrinksMark`, `rowIsSingleTapTarget`)은 PR2 스위치 줄이 그대로 따른다.
