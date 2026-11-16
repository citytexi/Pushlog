# 선택 컨트롤 PR3 구현 계획 (select-box · select-box 그룹)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `core:designsystem`에 `component.selectbox` 패키지를 만들고, 공개 `PushlogSelectBox`·`PushlogSelectBoxGroup`과 내부 부품 select-box 체크 마크(`SelectBoxCheckmark`)를 SEED 정의대로 만든다.

**Architecture:** 카드는 PR1이 만든 `PushlogPressableSurface`를 `scaleScope = PressScaleScope.CONTENT`로 쓴다. 그래서 카드 색·테두리는 그대로 두고 내용만 카드 크기 기준 배율로 줄어든다. 단일 선택은 `selectionMode = SINGLE`(`Role.RadioButton`)에 `PushlogRadiomark`를, 다중 선택은 `TOGGLE`(`Role.Checkbox`)에 내부 `SelectBoxCheckmark`를 쓴다. 두 마크는 카드의 `interactionSource`를 받아 색만 바꾸고 스스로 줄지 않는다. 그룹은 상태가 없다. 열 나누기, 줄 높이 맞추기, `selectableGroup()`, 박스 `layout` 기본값만 맡는 커스텀 `Layout`이다.

**Tech Stack:** Kotlin, Jetpack Compose foundation(`updateTransition`, 커스텀 `Layout`), Robolectric + Roborazzi, Konsist, Navigation 3(`app-preview`).

**Spec:** `docs/superpowers/specs/2026-11-16-design-system-selection-controls-design.md` (이하 "스펙"). 이 계획은 스펙 §7의 PR3만 다룬다(§6.2 select-box 행, §6.7, §6.9의 select-box 항목). PR2(스위치·세그먼트)와는 서로 의존하지 않는다.

## 시작하기 전에 읽을 것

1. 스펙 전체, 특히 §6.1-3·5(내용 축소, 단일 선택 표면), §6.2, §6.7, §6.9
2. PR1 계획의 마지막 절 "PR2·PR3이 쓰는 인터페이스": `docs/superpowers/plans/2026-11-16-selection-controls-pr1.md`. 이 계획이 쓰는 이름과 시그니처는 모두 거기 있다.
3. `AGENTS.md` (금지 사항, 검증)
4. `docs/code-conventions.md` (주석·KDoc. 서브에이전트 브리프에 이 링크를 넣는다)
5. `docs/architecture/design-system.md`의 "동작 컴포넌트", "선택 컨트롤"(PR1이 추가), "프리뷰 스크린샷", "미리보기 앱" 절
6. `.claude/rules/design-system-components.md`
7. `.claude/skills/git-flow/SKILL.md` (작업 흐름: 워크트리, PR, 머지, 이슈 닫기)
8. SEED 정의(기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`). 필요한 값은 각 Task에 옮겨 두었다. 표에 없는 것을 확인할 때만 연다.
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/select-box.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/select-box-group.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/select-box-checkmark.yaml`
   - recipe(세 정의가 한 파일): `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/qvism-preset/src/recipes/select-box.ts`
   - React 조립(슬롯 순서, footer, columns): `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/docs/registry/react/ui/select-box.tsx`, `.../packages/react/src/components/SelectBox/CheckSelectBox.tsx`
   - 사용 기준: https://seed-design.io/components/select-box
9. 기존 코드(PR1 머지 뒤 develop): `component/pressable/*.kt`, `component/radio/PushlogRadiomark.kt`, `component/checkbox/PushlogCheckmark.kt`, `component/chip/PushlogChip.kt`(`Painter?` 아이콘, 파일 아래 `@PushlogPreview`), 테스트 `component/pressable/PushlogPressableSurfaceTest.kt`(내용 축소 픽셀 측정), `component/chip/PushlogChipScreenshotTest.kt`(`autoAdvance = false` 눌림 캡처), `app-preview/.../preview/component/PushlogChipPreview{Key,Screen}.kt`.

## Global Constraints

- 작업 위치: `origin/develop`(PR1 머지 뒤)에서 딴 워크트리, 브랜치 `feat/design-system-select-box`. PR 대상은 `develop`. PR1이 develop에 없으면 시작하지 않는다(Task 1).
- `AGENTS.md` 금지 사항을 지킨다. `@Suppress`, detekt baseline, ktlint 비활성화, Konsist·`ModuleRules` 약화는 금지다. 규칙이 막으면 구조를 고친다.
- 주석·KDoc은 `docs/code-conventions.md`를 따른다. `core:designsystem`은 한국어, `app-preview`는 기존 파일처럼 영어로 쓴다. 코드가 이미 말하는 것은 쓰지 않는다.
- 커밋 메시지: `type: 한국어 설명`, 빈 줄, `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- 파일 하나에 최상위 class/interface/object/enum은 하나다(`FileStructureRulesTest`). 최상위 함수·`val`은 여러 개 둘 수 있다.
- 패키지는 `com.citytexi.pushlog.core.designsystem.component.selectbox`. `toggleable`·`selectable`·`clickable`·`indication`·`pointerInput`은 쓰지 않는다(`ComponentRulesTest`). 탭은 `PushlogPressableSurface`가 맡는다. `Modifier.selectableGroup()`과 `collectIsPressedAsState()`는 써도 된다.
- 레이아웃 값은 `PushlogSelectBoxLayout.metrics()`가 `SelectBoxMetrics`(`internal class`)로, 선택 방식은 `PushlogSelectBoxType`의 `internal` 멤버가 돌려준다. 멤버는 `else` 없이 `when (this)`로 쓴다. `XxxDefaults`는 만들지 않는다. 색은 variant가 없으므로 컴포넌트 파일에서 상태로 토큰을 고른다.
- SEED px 값은 같은 크기 토큰으로 옮긴다(2→`x0Half`, 4→`x1`, 6→`x1Half`, 10→`x2Half`, 12→`x3`/`r3`, 16→`x4`, 20→`x5`). 토큰이 없는 값(1px·2px 선, 22px 앞 아이콘, 15px 체크 아이콘, 100ms·400ms)만 파일 안 `private` 상수에 SEED 출처 주석을 단다. 새 foundation 토큰은 만들지 않는다.
- 선택 상태는 호출하는 쪽이 갖는다. 박스는 `selected`와 `onClick`만 받는다. 최대 선택 수 제한·Snackbar는 범위 밖이다(스펙 §2).
- ripple·state layer 금지. 카드 전체가 탭 하나다. 눌리면 카드 색이 바뀌고 내용만 준다. 마크는 따로 줄지 않는다.
- 공개 `@Composable`마다 `app-preview`에 `<함수>PreviewKey`, `<함수>PreviewScreen.kt`(entry 함수 + 본문 + 섹션별 `@PushlogPreview`), `PreviewEntryProvider` 등록, `PreviewCatalog` 행. 섹션 하나가 320x470 캔버스(2배 글자 포함)를 넘지 않게 나눈다.
- 작업 중 `./gradlew :core:designsystem:testDebugUnitTest --tests '<클래스>'`, `./gradlew :konsist:test`. 마지막 커밋 전 `./gradlew verifyStarter`. 포맷은 `./gradlew ktlintFormat`.
- 개발 중 미리보기는 prevue로 렌더링해 확인한다(`prevue` skill). 준비 승인을 물으면 사용자에게 묻는다.
- 기준 스크린샷(`build/outputs/roborazzi`)은 커밋하지 않는다.
- push와 PR은 사용자 확인 후에만 한다.

## Review Focus

1. 3열 그룹에서 글자 크기 2배면 라벨이 여러 줄로 감긴다. 줄 높이는 intrinsic 높이로 정하므로, 라벨이 카드 밖으로 잘리지 않고 줄 전체가 늘어나야 한다. → Task 5 `largeFontInThreeColumnsKeepsLabelInsideCard`.
2. footer가 열리는 중에 선택을 다시 풀 수 있다. 애니메이션이 중간에서 되돌아가도 예외 없이 접히고, 끝나면 footer 내용이 컴포지션에서 빠져야 한다. → Task 4 `footerReversesMidAnimation`.
3. 호출하는 쪽이 `interactionSource`를 넘기지 않아도, 실제 터치로 라벨을 누르면 카드와 마크가 같은 눌림을 봐야 한다(마크 색이 바뀐다). → Task 3 `realPressOnLabelPressesMark`.
4. 비활성 카드에 눌림이 들어와도(주입) 카드 색과 내용 크기가 그대로여야 하고, 탭해도 `onClick`이 불리면 안 된다. → Task 3 `disabledIgnoresPressAndTap`.
5. 항목 수가 열 수의 배수가 아니면(3열에 5개) 마지막 줄 칸이 늘어나면 안 되고 같은 칸 너비를 지켜야 한다. 항목이 0개여도 예외가 나면 안 된다. → Task 5 `partialLastRowKeepsCellWidth`, `emptyGroupHasNoHeight`.

---

### Task 1: 준비 — PR1 확인, 워크트리

**Files:** 없음(확인만)

- [ ] **Step 1: develop에 PR1이 들어왔는지 확인**

```bash
git fetch origin
P=core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component
git grep -n "scaleOnPress" origin/develop -- $P/radio/PushlogRadiomark.kt
git grep -n "CONTENT" origin/develop -- $P/pressable/PressScaleScope.kt
git grep -n "SINGLE" origin/develop -- $P/pressable/PressableSelectionMode.kt
git grep -n "fun Modifier.pushlogSelectablePressable" origin/develop -- $P/pressable/PushlogPressable.kt
git grep -n "selectionMode\|scaleScope" origin/develop -- $P/pressable/PushlogPressableSurface.kt
git grep -n "fun PushlogCheckmark" origin/develop -- $P/checkbox/PushlogCheckmark.kt
git ls-tree origin/develop docs/superpowers/plans/2026-11-16-selection-controls-pr3.md
```

Expected: 모든 명령이 한 줄 이상 출력한다. 하나라도 비면 PR1이 아직 머지되지 않은 것이다. 멈추고 사용자에게 알린다. 시그니처가 PR1 계획의 "PR2·PR3이 쓰는 인터페이스"와 다르면 develop의 코드를 따르고, 다른 점을 사용자에게 알린다.

- [ ] **Step 2: 워크트리 만들기 (git-flow skill 2절)**

Orca 안(`$ORCA_WORKTREE_ID` 또는 `$ORCA_WORKSPACE_ID`가 있음):

```bash
orca worktree create --name design-system-select-box --base-branch origin/develop \
  --branch feat/design-system-select-box --issue 35 --parent-worktree active --setup skip --json
```

Orca 밖: `git worktree add ../Pushlog-worktrees/design-system-select-box -b feat/design-system-select-box origin/develop`.
이후 모든 명령은 워크트리 경로 안에서 돌린다.

- [ ] **Step 3: 기준선 확인**

Run: `./gradlew testStarter`
Expected: BUILD SUCCESSFUL. 실패하면 develop 상태 문제다. 멈추고 사용자에게 알린다.

---

### Task 2: 내부 `SelectBoxCheckmark`

select-box 체크 마크는 PR1 체크 마크와 같은 체크 모양을 쓴다. PR1이 `component.checkbox.CheckmarkIconDrawing.kt`에 `internal` 그리기 함수를 이미 두었으므로 그것을 부른다. PR1 파일은 고치지 않는다.

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox/SelectBoxCheckmark.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox/SelectBoxCheckmarkTest.kt`

**Interfaces:**
- Consumes: PR1 `internal fun DrawScope.drawCheckIcon(color: Color)` (`component.checkbox.CheckmarkIconDrawing.kt`, 그리는 영역을 24x24 viewBox로 보고 채워 그린다).
- Produces:

```kotlin
// component.selectbox/SelectBoxCheckmark.kt
@Composable
internal fun SelectBoxCheckmark(
    selected: Boolean,
    enabled: Boolean,
    interactionSource: InteractionSource,
    modifier: Modifier = Modifier,
)
```

값(SEED select-box-checkmark.yaml):

| 슬롯 | 값 |
|---|---|
| root 크기 | `x5` (20) |
| 아이콘 크기 | 15dp, `private val IconSize = 15.dp` (SEED select-box-checkmark.yaml `icon.size: 15px`) |
| 아이콘 색, 기본 | `fg.placeholder` |
| 아이콘 색, 눌림(활성일 때만) | `fg.neutralSubtle` |
| 아이콘 색, 선택(눌림 포함) | `fg.neutral` |
| 아이콘 색, 비활성(선택 포함) | `fg.disabled` |
| 색 전환 | `tween(PushlogTheme.motion.duration.colorTransition, easing = PushlogTheme.motion.timingFunction.easing)` |

우선순위는 비활성 > 선택 > 눌림 > 기본이다. 눌림은 `interactionSource.collectIsPressedAsState()`로 읽는다. 배경·테두리·축소는 없다. 그리기: `Box(modifier.size(x5), contentAlignment = Center) { Canvas(Modifier.size(IconSize)) { drawCheckIcon(animatedColor) } }`. KDoc 한 줄: SEED select-box 전용 체크 마크이고 탭과 축소를 맡지 않는다(카드가 내용 축소로 함께 줄인다).

- [ ] **Step 1: PR1 그리기 함수 확인**

Run: `grep -n "fun DrawScope.draw" core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/checkbox/CheckmarkIconDrawing.kt`
Expected: `drawCheckIcon`, `drawDashIcon` 두 줄. 없으면 멈추고 사용자에게 묻는다.

- [ ] **Step 2: 실패하는 테스트 작성 — `SelectBoxCheckmarkTest`** (Robolectric, `@GraphicsMode(NATIVE)`)

루트 `Box(Modifier.size(40.dp).background(PushlogTheme.color.bg.layerDefault), Alignment.Center) { SelectBoxCheckmark(selected, enabled, source) }`. Press는 `PressInteraction.Press`를 직접 주입하고 300ms 흘린다. 색 값은 컴포지션 안에서 토큰으로 읽어 둔다. `count(c)`는 캡처 이미지에서 정확히 색이 `c`인 픽셀 수다.

```kotlin
@Test fun idleIconIsPlaceholder()
// selected = false, enabled = true → count(fg.placeholder) > 0

@Test fun pressedIconIsNeutralSubtle()
// press 뒤 count(fg.neutralSubtle) > 0, count(fg.placeholder) == 0

@Test fun selectedIconIsNeutralEvenWhenPressed()
// selected = true → count(fg.neutral) > 0. press 뒤에도 count(fg.neutral) > 0, count(fg.neutralSubtle) == 0

@Test fun disabledIconIgnoresPress()
// enabled = false, selected = true, press 뒤 count(fg.disabled) > 0, count(fg.neutral) == 0

@Test fun pressDoesNotResize()
// 배경색이 아닌 픽셀의 경계 상자(left, top, right, bottom)가 press 전과 뒤에 같다
```

- [ ] **Step 3: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*SelectBoxCheckmarkTest'`
Expected: 컴파일 실패(`SelectBoxCheckmark` 없음).

- [ ] **Step 4: 구현**

`SelectBoxCheckmark`를 Interfaces와 값 표대로 만든다.

- [ ] **Step 5: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*SelectBoxCheckmarkTest'`
Expected: PASS.

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox
git commit -m "feat: select-box 체크 마크 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 3: `PushlogSelectBox` (카드, 선택 방식, 레이아웃, 선택 테두리)

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox/PushlogSelectBoxType.kt`
- Create: `.../component/selectbox/PushlogSelectBoxLayout.kt` (enum + `LocalSelectBoxLayout`)
- Create: `.../component/selectbox/SelectBoxMetrics.kt`
- Create: `.../component/selectbox/PushlogSelectBox.kt`
- Create: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogSelectBoxPreviewKey.kt`
- Create: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogSelectBoxPreviewScreen.kt`
- Modify: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/navigation/PreviewEntryProvider.kt`, `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/catalog/PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox/PushlogSelectBoxTest.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox/PushlogSelectBoxScreenshotTest.kt`

**Interfaces:**
- Consumes: `PushlogPressableSurface(color, pressedColor, shape, enabled, onClick, modifier, selected, selectionMode, scaleScope, border, interactionSource, content)`, `PressScaleScope.CONTENT`, `PressableSelectionMode.{TOGGLE, SINGLE}`, `pressedScaleFactor(widthPx, heightPx, density)` (PR1, `component.pressable`). `PushlogRadiomark(selected, modifier, tone, size, enabled, scaleOnPress, interactionSource)` (PR1, `component.radio`). `SelectBoxCheckmark(selected, enabled, interactionSource, modifier)` (Task 2).
- Produces:

```kotlin
/** 선택 방식. 마크와 접근성 역할이 따라 바뀐다. */
enum class PushlogSelectBoxType {
    /** 하나만 고른다. 라디오 마크, Role.RadioButton. */
    SINGLE,

    /** 여러 개를 고른다. 체크 마크, Role.Checkbox. */
    MULTIPLE,
    ;

    internal fun selectionMode(): PressableSelectionMode // SINGLE → SINGLE, MULTIPLE → TOGGLE
}

/** 카드 안 배치. SEED `layout`. */
enum class PushlogSelectBoxLayout {
    /** 앞 아이콘·글자를 가로로 놓는다. 한 열에 쓴다. */
    HORIZONTAL,

    /** 앞 아이콘 아래에 글자를 놓는다. 여러 열에 쓴다. */
    VERTICAL,
    ;

    @Composable internal fun metrics(): SelectBoxMetrics
}

/** 그룹이 박스에 주는 layout 기본값. 그룹 밖이면 HORIZONTAL. */
internal val LocalSelectBoxLayout = staticCompositionLocalOf { PushlogSelectBoxLayout.HORIZONTAL }

internal class SelectBoxMetrics(
    val paddingStart: Dp,
    val paddingEnd: Dp,
    val paddingVertical: Dp,
    val contentGap: Dp,
)

@Composable
fun PushlogSelectBox(
    selected: Boolean,
    onClick: () -> Unit,
    type: PushlogSelectBoxType,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    prefixIcon: Painter? = null,
    layout: PushlogSelectBoxLayout? = null,     // null이면 LocalSelectBoxLayout.current
    showControl: Boolean = true,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    suffix: (@Composable () -> Unit)? = null,
)
```

`footer`·`footerVisibility`는 Task 4에서 이 시그니처 끝(`suffix` 뒤)에 더한다.

값(SEED select-box.yaml, recipe `select-box.ts`):

| | HORIZONTAL | VERTICAL |
|---|---|---|
| `paddingStart` | `x5` (20) | `x4` (16) |
| `paddingEnd` | `x4` (16) | `x4` (16) |
| `paddingVertical` | `x4` (16) | `x5` (20) |
| `contentGap` (앞 아이콘 ↔ 글자 묶음) | `x3` (12), 가로 `Row`, 세로 가운데 | `x2Half` (10), 세로 `Column` |
| 줄(trigger) 세로 정렬 | 가운데 | 위 |

| 슬롯 | 값 |
|---|---|
| 카드 모양 | `RoundedCornerShape(PushlogTheme.radius.r3)` (12) |
| 카드 색 / 눌림 | `bg.transparent` / `bg.transparentPressed` (비활성은 눌림 없음, `PressColorNode`가 막는다) |
| 기본 테두리 | 1dp `stroke.neutralWeak`. 비활성 + 선택이면 2dp `stroke.neutralWeak` |
| 선택 테두리 | 2dp, 활성 + 선택일 때 `stroke.neutralContrast`, 아니면 같은 색 `alpha = 0f`. `animateColorAsState(tween(100, easing = timingFunction.easing))` |
| 줄 간격(글자 묶음 ↔ `suffix` ↔ 마크) | `x1Half` (6) |
| 앞 아이콘 | 22dp, `fg.neutral`, 비활성 `fg.disabled` |
| 글자 묶음 | `Column(spacedBy(x0Half))`, 오른쪽 안쪽 여백 `x1` (4), 줄에서 `weight(1f)` |
| 라벨 | `t5Medium`, `fg.neutral`, 비활성 `fg.disabled`, `maxLines` 없음 |
| 설명 | `t3Regular`, `fg.neutralMuted`, 비활성 `fg.disabled`, `maxLines = 2`, `TextOverflow.Ellipsis` |
| 마크 | SINGLE: `PushlogRadiomark(selected, tone = NEUTRAL, size = MEDIUM, enabled, scaleOnPress = false, interactionSource = source)`. MULTIPLE: `SelectBoxCheckmark(selected, enabled, source)`. `showControl = false`면 그리지 않는다 |

파일 안 상수(출처 주석): `StrokeWidth = 1.dp`(select-box.yaml `strokeWidth: 1px`), `SelectedStrokeWidth = 2.dp`(`selected.root.strokeWidth: 2px`), `PrefixIconSize = 22.dp`(`prefixIcon.size: 22px`), `SELECTED_STROKE_DURATION_MS = 100`(`strokeDuration: 0.1s`).

조립:
- `val source = interactionSource ?: remember { MutableInteractionSource() }` 하나를 표면과 마크에 같이 넘긴다.
- 선택 테두리는 표면 바깥 `modifier`에 붙인다: `PushlogPressableSurface(modifier = modifier.border(SelectedStrokeWidth, selectedStrokeColor, shape), ...)`. 바깥 `border`는 표면 내용 위에 그려지므로 기본 테두리를 덮는다(SEED `::after`). `scaleScope = CONTENT`라 표면은 줄지 않으니 테두리도 그대로다.
- 표면: `selected = selected`, `selectionMode = type.selectionMode()`, `scaleScope = PressScaleScope.CONTENT`, `border = BorderStroke(기본 테두리)`.
- 내용: `Column(Modifier.fillMaxWidth())` 안에 줄 `Row(padding(start, end, vertical), spacedBy(x1Half))` = [글자 묶음(앞 아이콘 포함, `weight(1f)`), `suffix?.invoke()`, 마크]. `suffix`는 SEED React처럼 마크 바로 앞에 둔다. Task 4가 이 `Column`에 footer를 더한다.
- `layout ?: LocalSelectBoxLayout.current`로 실제 배치를 정한다.
- KDoc: 카드 전체가 탭 하나다. 선택 상태와 최대 선택 수 제한은 호출하는 쪽이 갖는다. `showControl = false`여도 역할은 `type`을 따른다. 즉시 실행 버튼으로 쓰지 말고 별도 확인 버튼으로 반영한다(SEED 가이드). `suffix`에는 intrinsic 측정을 지원하지 않는 레이아웃(`LazyColumn`, `BoxWithConstraints`)을 넣지 않는다(여러 열 그룹이 intrinsic 높이로 줄을 맞춘다, Task 5).
- 파일 아래 `@PushlogPreview private fun PushlogSelectBoxPreview()`: SINGLE 선택·미선택, MULTIPLE 선택 한 장(칩 파일 방식).

미리보기 앱:
- `PushlogSelectBoxPreviewKey`(`@Serializable data object ... : NavKey`), `pushlogSelectBoxPreviewEntry(onBack)`, `PushlogSelectBoxPreviewContent()`.
- 섹션(각각 박스 3개 이하, 상태는 `remember`로 토글): `SINGLE`(설명 있는 것 하나, 비활성 하나, 비활성 선택 하나), `MULTIPLE`(같은 구성), `no control`(`showControl = false` 두 개), `prefix and suffix`(`PreviewSampleIcons.Plus` 앞 아이콘, `suffix = { Text("+1,000원", style = t4Medium, color = fg.neutral) }`), `vertical`(`layout = VERTICAL`, `Modifier.width(160.dp)`), `long text`(긴 라벨, 3줄 넘는 설명 → 2줄 말줄임).
- 섹션마다 `@PushlogPreview`: `PushlogSelectBoxSinglePreview`, `PushlogSelectBoxMultiplePreview`, `PushlogSelectBoxNoControlPreview`, `PushlogSelectBoxPrefixSuffixPreview`, `PushlogSelectBoxVerticalPreview`, `PushlogSelectBoxLongTextPreview`.
- `previewEntryProvider`에 `pushlogSelectBoxPreviewEntry(onBack)`, `previewCatalog`에 `PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogSelectBox", PushlogSelectBoxPreviewKey)`.

- [ ] **Step 1: 실패하는 테스트 작성 — `PushlogSelectBoxTest`** (Robolectric, NATIVE, `@Config(qualifiers = "w600dp-h600dp")`)

색 값은 컴포지션 안에서 토큰으로 읽어 둔다. 카드는 `Modifier.testTag("box")`로 찾는다. `d`는 density다.

```kotlin
@Test fun singleExposesRadioButtonRole()
// type = SINGLE, selected = true → config[SemanticsProperties.Role] == Role.RadioButton, assertIsSelected(),
// config에 SemanticsProperties.ToggleableState 없음

@Test fun multipleExposesCheckboxRole()
// type = MULTIPLE, selected = true → Role.Checkbox, assertIsOn()

@Test fun hiddenControlKeepsRole()
// showControl = false로 두 type 각각 위 두 검사와 같은 역할

@Test fun labelTapCallsOnClickOnce()
// onNodeWithText("사과").performClick() → clicks == 1

@Test fun cardIsSingleTapTarget()
// suffix = { Text("+1,000원") }, description 있음 → onAllNodes(hasClickAction()).assertCountEquals(1)

@Test fun disabledIgnoresPressAndTap()
// enabled = false: performClick() → clicks == 0, assertIsNotEnabled()
// interactionSource에 Press 주입 + 300ms → 캡처 이미지가 주입 전과 픽셀 단위로 같다(카드 색·내용 크기 그대로)

@Test fun pressShrinksContentByCardSize()
// Modifier.size(300.dp, 80.dp), type = SINGLE, selected = true, Press 주입 + 300ms
// 카드 세로 가운데 줄, 카드 오른쪽 60dp 구간에서 bg.neutralSolid 또는 bg.neutralSolidPressed 픽셀 수(마크 지름)
//   == round(20 * d * pressedScaleFactor(300 * d, 80 * d, d)) ± 1     // 기준 80dp → 78/80
//   != round(20 * d * pressedScaleFactor(300 * d, 80 * d, d) * 22 / 24) // 마크가 스스로 한 번 더 줄면 실패
// onNodeWithTag("box").getBoundsInRoot() 크기 그대로(카드는 줄지 않는다)

@Test fun realPressOnLabelPressesMark()
// interactionSource 넘기지 않음, type = SINGLE, selected = true
// onNodeWithText(label).performTouchInput { down(center) }, mainClock.advanceTimeBy(300)
// 위와 같은 구간에서 bg.neutralSolidPressed 픽셀 수 > 0 (카드와 마크가 같은 소스를 쓴다)

@Test fun selectedStrokeFadesIn()
// autoAdvance = false, selected 상태를 false → true로 바꾼다
// 0ms: (1dp, 세로 가운데) 픽셀 != stroke.neutralContrast
// 150ms 흘린 뒤: 같은 픽셀 == stroke.neutralContrast

@Test fun disabledSelectedShowsWeakTwoDpStroke()
// enabled = false, selected = true → (1.5dp, 세로 가운데) 픽셀 == stroke.neutralWeak, stroke.neutralContrast 픽셀 0개

@Test fun descriptionStopsAtTwoLines()
// 긴 설명, Modifier.width(240.dp) → 설명 노드(useUnmergedTree)의 GetTextLayoutResult lineCount == 2

@Test fun layoutChangesPadding()
// 앞 아이콘 없음. HORIZONTAL: 라벨 left - 카드 left == 20dp ± 1px, VERTICAL: == 16dp ± 1px
// layout = null, 그룹 밖 → HORIZONTAL 값(20dp)
```

- [ ] **Step 2: 눌림 스크린샷 테스트 작성 — `PushlogSelectBoxScreenshotTest`**

`PushlogChipScreenshotTest` 구조를 따른다(`@Config(qualifiers = "w1200dp-h1200dp")`, `autoAdvance = false`, 칸마다 `MutableInteractionSource`, Press 주입 뒤 `advanceTimeBy(800)`, 한 번만 캡처).
- `pressedLight()` → `build/outputs/roborazzi/select_box_pressed_light.png`, `pressedDark()` → `select_box_pressed_dark.png`.
- 칸(각 `Modifier.width(280.dp)`): 눌림 SINGLE 미선택, 눌림 SINGLE 선택, 눌림 MULTIPLE 미선택, 눌림 MULTIPLE 선택, 눌림 비활성 선택.

- [ ] **Step 3: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSelectBoxTest' --tests '*PushlogSelectBoxScreenshotTest'`
Expected: 컴파일 실패(`PushlogSelectBox` 없음).

- [ ] **Step 4: 구현** (Interfaces, 값 표, 조립, 미리보기 앱대로)

- [ ] **Step 5: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*selectbox*'`
Expected: PASS. 캡처 이미지를 열어 눌린 카드 색이 바뀌고 마크 색만 눌림 색인지, 비활성 칸이 그대로인지 본다.

- [ ] **Step 6: 미리보기와 규칙**

prevue로 `PushlogSelectBoxPreviewScreen.kt`를 렌더링해 SEED 문서 그림(https://seed-design.io/components/select-box)과 여백·글자·테두리를 비교한다. 2배 글자 프리뷰가 캔버스를 넘지 않는지 본다(넘으면 섹션을 나눈다).
Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS(`PreviewCoverageRulesTest`, `PreviewEntryProviderTest` 포함).

- [ ] **Step 7: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: select-box PushlogSelectBox 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 4: select-box footer (표시 조건 세 가지, 열고 닫는 모션)

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox/PushlogSelectBoxFooterVisibility.kt`
- Create: `.../component/selectbox/SelectBoxFooter.kt`
- Modify: `.../component/selectbox/PushlogSelectBox.kt` (인자 둘 추가, 내용 `Column`에 footer)
- Modify: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogSelectBoxPreviewScreen.kt` (footer 섹션)
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox/PushlogSelectBoxFooterTest.kt`

**Interfaces:**
- Consumes: `PushlogSelectBox(...)` (Task 3).
- Produces:

```kotlin
/** footer를 언제 보일지. SEED `footerVisibility`. */
enum class PushlogSelectBoxFooterVisibility {
    WHEN_SELECTED,
    ALWAYS,
    WHEN_NOT_SELECTED,
    ;

    internal fun isVisible(selected: Boolean): Boolean // selected / true / !selected
}

@Composable
internal fun SelectBoxFooter(
    visible: Boolean,
    content: @Composable () -> Unit,
)

// PushlogSelectBox 끝에 더하는 인자 (suffix 뒤)
    footerVisibility: PushlogSelectBoxFooterVisibility = PushlogSelectBoxFooterVisibility.WHEN_SELECTED,
    footer: (@Composable () -> Unit)? = null,
```

모션(SEED select-box.yaml footer):

| | 높이 | 투명도 |
|---|---|---|
| 열 때 | 400ms | `duration.d6` (300ms) |
| 닫을 때 | `duration.d6` (300ms) | 400ms |

easing은 모두 `timingFunction.easing`. 400은 파일 안 `private const val FOOTER_SLOW_DURATION_MS = 400`(select-box.yaml `expandHeightDuration`, `collapseOpacityDuration`).

`SelectBoxFooter` 동작:
- `val transition = updateTransition(visible)`. 높이 비율과 투명도를 `transition.animateFloat(transitionSpec = { if (targetState) 열 때 tween else 닫을 때 tween })`으로 0f↔1f 애니메이션한다.
- `visible || transition.isRunning`일 때만 내용을 컴포즈한다. 다 닫히면 컴포지션에서 빠지므로 스크린 리더가 숨은 footer를 읽지 않는다.
- 커스텀 `Layout(modifier = Modifier.clipToBounds())`: 자식을 `constraints.copy(minHeight = 0)`으로 한 번 재고, 높이 `(placeable.height * fraction).roundToInt()`로 놓는다. 비율 `State`는 measure 블록 안에서 읽는다(재구성 없이 다시 재기만 한다). 투명도는 자식 `graphicsLayer { alpha = ... }`에서 읽는다.
- 처음 컴포즈될 때 `updateTransition`의 시작 상태가 목표와 같아 애니메이션이 없다. 그래서 프리뷰는 끝 상태를 그린다.
- footer는 카드 내용 `Column`의 줄 아래에 둔다. 내용 축소 층 안이라 함께 준다(SEED도 footer가 `ContentScale` 안이다). 안쪽 여백은 호출하는 쪽이 준다(SEED 예시 `px x5, pb x5`). KDoc에 그 점을 적는다.

미리보기 앱: 섹션 `footer` = 세 박스(`WHEN_SELECTED`·`ALWAYS`·`WHEN_NOT_SELECTED`, MULTIPLE, 상태 토글, footer = `Box(Modifier.padding(start = x5, end = x5, bottom = x5)) { Text(..., t3Medium) }`). `@PushlogPreview private fun PushlogSelectBoxFooterPreview()`.

- [ ] **Step 1: 실패하는 테스트 작성 — `PushlogSelectBoxFooterTest`** (Robolectric, NATIVE)

footer 내용은 `Text("추가 정보")`. "있다/없다"는 `onNodeWithText("추가 정보", useUnmergedTree = true).assertExists()` / `assertDoesNotExist()`다. 카드 높이는 `onNodeWithTag("box").getBoundsInRoot().height`.

```kotlin
@Test fun whenSelectedShowsOnlySelected()
// WHEN_SELECTED: selected = true → 있다, false → 없다

@Test fun alwaysShowsInBothStates()
// ALWAYS: true·false 모두 있다

@Test fun whenNotSelectedShowsOnlyUnselected()
// WHEN_NOT_SELECTED: true → 없다, false → 있다

@Test fun footerOpensOver400msAndClosesOver300ms()
// autoAdvance = false, WHEN_SELECTED. 닫힌 높이 H0, 열린 높이 H1을 먼저 잰다(각 상태로 1000ms 흘린 뒤)
// false → true: 200ms 뒤 H0 < 높이 < H1, 450ms 뒤 높이 == H1
// true → false: 150ms 뒤 H0 < 높이 < H1, 350ms 뒤 높이 == H0, 450ms 뒤 footer 없다(투명도 400ms까지 끝남)

@Test fun footerReversesMidAnimation()
// false → true, 150ms 뒤 다시 false, 500ms 뒤: 예외 없음, footer 없다, 높이 == H0

@Test fun footerIsInsideCardTapTarget()
// footer가 보일 때 onAllNodes(hasClickAction()).assertCountEquals(1), footer 텍스트를 performClick() → clicks == 1
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSelectBoxFooterTest'`
Expected: 컴파일 실패(`PushlogSelectBoxFooterVisibility` 없음).

- [ ] **Step 3: 구현** (Interfaces, 모션 표, 동작대로)

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*selectbox*'`
Expected: PASS(Task 3 테스트 포함).

- [ ] **Step 5: 미리보기와 규칙**

prevue로 footer 섹션을 렌더링해 선택된 박스에만 footer가 보이는지 본다.
Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: select-box footer 표시 조건과 열고 닫는 모션 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 5: `PushlogSelectBoxGroup` (열, 같은 줄 높이, layout 기본값)

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox/PushlogSelectBoxGroup.kt`
- Create: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogSelectBoxGroupPreviewKey.kt`
- Create: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogSelectBoxGroupPreviewScreen.kt`
- Modify: `PreviewEntryProvider.kt`, `PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox/PushlogSelectBoxGroupTest.kt`

**Interfaces:**
- Consumes: `LocalSelectBoxLayout`, `PushlogSelectBoxLayout` (Task 3), `PushlogSelectBox(...)` (Task 3·4).
- Produces:

```kotlin
@Composable
fun PushlogSelectBoxGroup(
    modifier: Modifier = Modifier,
    columns: Int = 1,
    content: @Composable () -> Unit,
)
```

값(SEED select-box-group.yaml, recipe `selectBoxGroup`):

| | 값 |
|---|---|
| 열 사이 | `x3` (12) |
| 줄 사이 | `dimension.spacingY.componentDefault` (12) |
| 열 수 | 1~3. 밖이면 `require(columns in 1..3)` (SEED 문서 "최대 3개") |
| 줄 높이 | `columns == 1`이면 박스마다 제 높이, 2 이상이면 줄 안에서 가장 큰 박스 높이로 맞춘다(`grid-auto-rows: 1fr`, `data-columns != 1`) |
| 박스 layout 기본값 | `columns >= 2`면 `VERTICAL`, 아니면 `HORIZONTAL`. `CompositionLocalProvider(LocalSelectBoxLayout provides ...)`. 박스의 `layout` 인자가 덮어쓴다 |

그룹 루트는 `Layout(content, modifier.fillMaxWidth().selectableGroup(), measurePolicy)`. 측정 정책은 파일 안 `private`이고 아래 알고리즘이다. 같은 줄의 높이를 맞추려면 재기 전에 높이를 알아야 하는데, 측정은 자식마다 한 번뿐이라 intrinsic 높이로 먼저 정한다.

```kotlin
private fun selectBoxGridPolicy(columns: Int, gapX: Int, gapY: Int) = MeasurePolicy { measurables, constraints ->
    require(constraints.hasBoundedWidth) { "PushlogSelectBoxGroup needs a bounded width" }
    val width = constraints.maxWidth
    val cellWidth = ((width - gapX * (columns - 1)) / columns).coerceAtLeast(0)
    val rows = measurables.chunked(columns).map { row ->
        if (columns == 1) {
            row.map { it.measure(Constraints.fixedWidth(cellWidth)) }
        } else {
            val rowHeight = row.maxOf { it.maxIntrinsicHeight(cellWidth) }
            row.map { it.measure(Constraints.fixed(cellWidth, rowHeight)) }
        }
    }
    val rowHeights = rows.map { row -> row.maxOf { it.height } }
    val height = rowHeights.sum() + gapY * (rows.size - 1).coerceAtLeast(0)
    layout(width, constraints.constrainHeight(height)) {
        var y = 0
        rows.forEachIndexed { r, row ->
            row.forEachIndexed { c, p -> p.placeRelative(c * (cellWidth + gapX), y) }
            y += rowHeights[r] + gapY
        }
    }
}
```

- `columns`·간격(px)이 바뀔 때만 정책을 새로 만든다(`remember(columns, gapXPx, gapYPx)`).
- 박스가 고정 높이를 받으면 카드 표면이 그 높이를 채운다(PR1 CONTENT 경로가 표면 최소 제약을 안쪽 상자에 넘긴다). 박스 내용 `Column`에 `fillMaxHeight()`를 주고 세로 배치를 정한다. footer가 있으면 `Arrangement.SpaceBetween`(줄은 위, footer는 아래)이다. footer가 없으면 HORIZONTAL은 `Arrangement.Center`(SEED trigger `alignItems: center`), VERTICAL은 `Arrangement.Top`이다. Task 3의 `pressShrinksContentByCardSize`는 마크가 카드 세로 가운데에 있다고 보므로 이 조정 뒤에도 통과해야 한다. `weight`는 쓰지 않는다. 한 열(높이 제한 없음)에서는 weight 자식이 높이 0을 받는다. 이 조정은 `PushlogSelectBox.kt`에서 한다.
- KDoc: 상태를 갖지 않는다. 열이 2개 이상이면 박스 기본 layout이 세로다. 너비 제한이 없는 곳(가로 스크롤)에는 둘 수 없다. 그룹 이름은 #36 `field`가 붙인다.
- 파일 아래 `@PushlogPreview private fun PushlogSelectBoxGroupPreview()`: 2열 박스 4개.

미리보기 앱: `PushlogSelectBoxGroupPreviewScreen` 섹션 = `1 column`(SINGLE 세 개, 단일 선택 상태), `2 columns`(MULTIPLE 네 개, 앞 아이콘, 하나는 설명이 길어 줄 높이 맞춤이 보이게, 하나는 `layout = HORIZONTAL` 덮어쓰기), `3 columns`(SINGLE 여섯 개, 짧은 라벨, 설명 없음). 섹션마다 `@PushlogPreview`(`PushlogSelectBoxGroupOneColumnPreview`, `PushlogSelectBoxGroupTwoColumnsPreview`, `PushlogSelectBoxGroupThreeColumnsPreview`). 2배 글자에서 캔버스를 넘으면 3열 섹션을 한 줄(박스 3개)로 줄인다. entry·catalog 행(`"PushlogSelectBoxGroup"`).

- [ ] **Step 1: 실패하는 테스트 작성 — `PushlogSelectBoxGroupTest`** (Robolectric, NATIVE, `@Config(qualifiers = "w400dp-h800dp")`)

박스마다 `Modifier.testTag("b<i>")`. `bounds(i)` = `onNodeWithTag("b$i").getBoundsInRoot()`. 그룹은 `Modifier.width(360.dp).testTag("group")`.

```kotlin
@Test fun groupIsSelectableGroup()
// config.contains(SemanticsProperties.SelectableGroup)

@Test fun columnsOutOfRangeFails()
// columns = 0, 4 각각 setContent에서 IllegalArgumentException

@Test fun cellsSplitWidthEquallyWithGaps()
// columns = 3, 박스 3개: 각 너비 == (360 - 2 * 12) / 3 dp ± 1px, bounds(1).left - bounds(0).right == 12dp ± 1px

@Test fun rowGapIsComponentDefault()
// columns = 2, 박스 4개: bounds(2).top - bounds(0).bottom == 12dp ± 1px

@Test fun rowsShareHeight()
// columns = 2, b0 설명 3줄, b1 라벨만 → bounds(0).height == bounds(1).height

@Test fun singleColumnKeepsOwnHeights()
// columns = 1, 같은 두 박스 → bounds(0).height > bounds(1).height

@Test fun defaultLayoutIsVerticalWithColumns()
// columns = 2, 앞 아이콘 없는 박스(layout 생략) → 라벨 left - 카드 left == 16dp ± 1px (VERTICAL)
// columns = 1 → 20dp ± 1px (HORIZONTAL)

@Test fun boxLayoutOverridesGroup()
// columns = 2, layout = HORIZONTAL → 20dp ± 1px

@Test fun partialLastRowKeepsCellWidth()
// columns = 3, 박스 5개 → bounds(3).width == bounds(0).width, bounds(4).left == bounds(1).left

@Test fun emptyGroupHasNoHeight()
// columns = 2, 내용 없음 → 예외 없음, onNodeWithTag("group") 높이 == 0

@Test fun largeFontInThreeColumnsKeepsLabelInsideCard()
// LocalDensity fontScale = 2f, columns = 3, 라벨 "아주 긴 선택지 이름" → 각 박스에서
// onNodeWithText(라벨, useUnmergedTree = true).getBoundsInRoot().bottom <= bounds(i).bottom, 세 박스 높이 같음
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSelectBoxGroupTest'`
Expected: 컴파일 실패(`PushlogSelectBoxGroup` 없음).

- [ ] **Step 3: 구현** (Interfaces, 값 표, 알고리즘, 박스 쪽 `fillMaxHeight` 조정대로)

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*selectbox*'`
Expected: PASS(Task 2~4 테스트 포함).

- [ ] **Step 5: 미리보기와 규칙**

prevue로 그룹 화면을 렌더링해 2열 줄 높이가 맞는지, 덮어쓴 가로 박스가 같은 줄 높이를 따르는지, 2배 글자 3열이 잘리지 않는지 본다.
Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/selectbox app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: select-box 그룹 PushlogSelectBoxGroup 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 6: 규칙 확인, 문서, 전체 검증

**Files:**
- Modify: `docs/architecture/design-system.md`
- Modify: `.claude/rules/design-system-components.md` (Step 3 조건일 때만)

- [ ] **Step 1: Konsist가 새 패키지를 잡는지 확인 (커밋하지 않음)**

1. `PushlogSelectBox.kt`에 `import androidx.compose.foundation.selection.selectable`을 더한다 → `./gradlew :konsist:test` → `only the pressable package handles taps` FAIL 확인 → 되돌린다.
2. `PushlogSelectBoxGroupPreviewScreen.kt` 이름을 잠시 `PushlogSelectBoxGroupPreviewScreenX.kt`로 바꾼다 → `./gradlew :konsist:test` → `every public design system component has a preview screen` FAIL 확인 → 되돌린다.
3. `git status`로 남은 변경이 없는지 본다.

- [ ] **Step 2: `docs/architecture/design-system.md` 고치기**

PR1이 만든 `## 선택 컨트롤` 절에 select-box 항목을 더한다. 코드로 복원할 수 없는 것만 쓴다.
- 패키지 `selectbox`: `PushlogSelectBox`, `PushlogSelectBoxGroup`. 체크 마크는 `PushlogCheckmark`가 아니라 SEED `select-box-checkmark`를 옮긴 `internal SelectBoxCheckmark`다. 체크 그림은 `checkbox`의 `internal` 그리기 함수를 같이 쓴다. 공개 API를 늘리지 않으려는 것이다.
- `type`(SINGLE/MULTIPLE) + `showControl`로 SEED 컨트롤 세 가지(Checkmark, Radiomark, None)를 표현한다. 숨겨도 역할은 `type`을 따른다.
- 눌림: 카드 색 + 내용 축소(`PressScaleScope.CONTENT`, 배율은 카드 크기 기준). 마크는 `scaleOnPress = false`이고 색만 바뀐다. SEED도 `--seed-radiomark-feedback-scale: 1`로 두 번 줄지 않게 한다.
- 선택 테두리 2dp는 표면 바깥 `border`로 그려 기본 1dp를 덮는다(SEED `::after`). 100ms 페이드.
- `suffix`는 마크 바로 앞이다. SEED React는 `suffix` 자리에 마크를 넣는데, Pushlog는 마크를 `type`으로 그리므로 둘을 나란히 둔다.
- footer: 표시 조건 세 가지, 열 때 높이 400/투명도 300, 닫을 때 300/400. 다 닫히면 컴포지션에서 빼서 스크린 리더가 숨은 내용을 읽지 않는다.
- 그룹: 상태 없음, 1~3열, `columns >= 2`면 박스 기본 layout이 세로(`LocalSelectBoxLayout`)이고 박스가 덮어쓴다. 여러 열의 줄 높이는 intrinsic 높이로 미리 정하고 고정 높이로 잰다. Compose는 자식을 한 번만 잴 수 있기 때문이다. 그래서 슬롯에 intrinsic을 지원하지 않는 레이아웃을 넣으면 안 된다. 한 열에서는 줄 높이를 맞추지 않는다(SEED `data-columns != 1`). 박스 내용에 `weight`를 쓰지 않는 이유(한 열은 높이 제한이 없다).
- "프리뷰 스크린샷" 절의 직접 쓴 스크린샷 테스트 목록에 `PushlogSelectBoxScreenshotTest`(눌림 주입)를 더한다.

- [ ] **Step 3: `.claude/rules/design-system-components.md` 확인**

이 PR에서 새로 생긴, 놓치기 쉬운 규칙은 "그룹이 자식 기본값을 줄 때는 `internal` `CompositionLocal`, 자식 인자가 덮어쓴다" 하나다. PR2가 이미 비슷한 줄을 더했으면 건너뛴다. 아니면 한 줄로 더한다. 고정 구조(프론트매터, 헤딩)는 그대로 둔다.

- [ ] **Step 4: 링크 검사**

Run: `python3 docs/script/check_links.py docs`
Expected: 0건.

- [ ] **Step 5: 전체 검증**

Run: `./gradlew ktlintFormat && ./gradlew verifyStarter`
Expected: BUILD SUCCESSFUL. 실패하면 고치고 다시 돌린다. 건너뛰고 완료를 선언하지 않는다.

- [ ] **Step 6: 커밋**

```bash
git add docs/architecture/design-system.md .claude/rules/design-system-components.md
git commit -m "docs: select-box와 그룹 결정을 디자인 시스템 문서에 남김

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 7: push와 PR (사용자 확인 후)

push, PR 생성, 머지, 이슈 닫기는 사용자에게 먼저 묻고, 승인을 받은 뒤에만 한다.

- [ ] **Step 1: PR2가 머지됐는지 확인**

Run: `gh pr list --state merged --base develop --head feat/design-system-switch-segmented --json number,mergedAt`
Expected: 결과가 있으면 이 PR이 #35의 마지막이다. Related Issue에 `Closes #35`를 쓴다. 결과가 비면 `Part of #35`를 쓴다. PR2가 열려 있는데 머지되지 않았으면 `Part of #35`로 두고, 나중에 머지되는 PR이 #35를 닫는다고 Notes에 적는다.

- [ ] **Step 2: 사용자 확인**

커밋 목록(`git log --oneline origin/develop..HEAD`), `verifyStarter` 결과, Step 1 결론(`Closes`/`Part of`)을 보여 주고 push·PR을 해도 되는지 묻는다.

- [ ] **Step 3: push**

Run: `git push -u origin feat/design-system-select-box`

- [ ] **Step 4: PR 생성**

`.github/PULL_REQUEST_TEMPLATE.md`의 절을 따라 한국어로 본문을 쓴다. 들어갈 내용:
- Summary: `PushlogSelectBox`, `PushlogSelectBoxGroup`, 내부 `SelectBoxCheckmark`(PR1 체크 그림 함수 재사용).
- Related Issue: Step 1 결론.
- Notes for Reviewer: `type` 필수 인자 결정, `suffix`를 마크 앞에 둔 결정, 여러 열 줄 높이를 intrinsic으로 맞춘 방식과 슬롯 제약, footer가 닫히면 컴포지션에서 빠진다는 점.
- 본문 끝 줄: `🤖 Generated with [Claude Code](https://claude.com/claude-code)`.

```bash
gh pr create --base develop --head feat/design-system-select-box \
  --title "feat: add select box and select box group" \
  --body-file <본문 파일>
```

Expected: PR URL. UI diff 봇 코멘트가 달리면 새 프리뷰·스크린샷을 확인한다.

- [ ] **Step 5: 머지 뒤 (사용자 확인 후, git-flow skill)**

`gh pr merge <PR> --merge --delete-branch`. `Closes #35`였으면 `gh issue close 35 --comment "#<PR>로 develop에 반영"`(develop 머지는 이슈를 자동으로 닫지 않는다). 워크트리를 지운다(Orca: `orca worktree rm --worktree <id> --json`, 밖: `git worktree remove <path>`).
