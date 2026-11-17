# 선택 컨트롤 PR2 구현 계획 (스위치 · 세그먼트 컨트롤)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** PR1이 넓힌 `pressable` 부품 위에 스위치 마크 `PushlogSwitchmark`, 스위치 `PushlogSwitch`, 세그먼트 컨트롤 `PushlogSegmentedControl`을 SEED 정의대로 만든다.

**Architecture:** 스위치는 PR1 체크박스와 같은 구조다. 줄 전체에 `pushlogToggleableArea(role = Role.Switch)`를 붙이고, 마크에는 같은 `interactionSource`로 `pushlogPressScale`(축소만, 눌림 색 없음)을 건다. 트랙·thumb 색은 20ms 지연이 있어서 `PressColorNode`가 아니라 마크가 직접 애니메이션한다. 세그먼트 컨트롤은 직접 짠 `Layout` 하나다. 항목을 같은 너비·높이 칸으로 나누고, indicator를 칸 나누기로 계산한 위치에 둔다(위치를 재지 않는다). 항목마다 PR1의 `PushlogPressableSurface(selectionMode = SINGLE, scaleScope = CONTENT, pressedBorder = …)`를 쓴다.

**Tech Stack:** Kotlin, Jetpack Compose foundation, Robolectric + Roborazzi, Konsist, Navigation 3(`app-preview`).

**Spec:** `docs/superpowers/specs/2026-11-16-design-system-selection-controls-design.md` (이하 "스펙"). 이 계획은 스펙 §7의 PR2만 다룬다(§6.5, §6.6, §6.9의 해당 항목).

## 시작하기 전에 읽을 것

1. 스펙 전체. 특히 §6.1, §6.5, §6.6, §6.8, §6.9.
2. PR1 계획의 마지막 절 "PR2·PR3이 쓰는 인터페이스": `docs/superpowers/plans/2026-11-16-selection-controls-pr1.md`. 줄 레이아웃 규칙과 테스트 패턴(`realPressOnLabelShrinksMark`, `rowIsSingleTapTarget`)도 거기 있다.
3. `AGENTS.md` (금지 사항, 검증)
4. `docs/code-conventions.md` (주석·KDoc. 서브에이전트 브리프에 이 링크를 넣는다)
5. `docs/architecture/design-system.md`의 "동작 컴포넌트", "선택 컨트롤"(PR1이 만든 절), "프리뷰 스크린샷", "미리보기 앱" 절
6. `.claude/rules/design-system-components.md`
7. `.claude/skills/git-flow/SKILL.md` (브랜치·워크트리·PR)
8. SEED 정의(기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`). 필요한 값은 각 Task에 옮겨 두었다. 표에 없는 것을 확인할 때만 연다.
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/switch.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/switchmark.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/segmented-control.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/segmented-control-item.yaml`
   - `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/segmented-control-indicator.yaml`
   - recipe: `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/qvism-preset/src/recipes/switch.ts`, `.../recipes/switchmark.ts`, `.../recipes/segmented-control.ts` (item·indicator는 `segmented-control.ts` 안에 있다. 따로 된 recipe 파일은 없다)
9. 기존 코드: PR1이 만든 `component/checkbox/PushlogCheckbox.kt`(줄 레이아웃), `component/checkbox/PushlogCheckmarkSize.kt`(Size·Metrics), `core/designsystem/src/test/.../component/checkbox/PushlogCheckboxTest.kt`(픽셀 측정, 실제 터치), `PushlogCheckmarkScreenshotTest.kt`(눌림 캡처), `app-preview/.../preview/component/PushlogCheckboxPreview{Key,Screen}.kt`.

## Global Constraints

- 선행 조건: PR1이 `develop`에 머지되어 있어야 한다. Task 1이 확인하고, 없으면 멈춘다.
- 브랜치: `feat/design-system-switch-segmented`, `origin/develop`에서 딴다. git-flow skill대로 Orca 안이면 Orca 워크트리, 밖이면 `git worktree`. PR 대상은 `develop`, 본문에 `Part of #35`(닫지 않는다).
- `AGENTS.md` 금지 사항을 지킨다. `@Suppress`, detekt baseline, ktlint 비활성화, Konsist·`ModuleRules` 약화는 금지다. 규칙이 막으면 구조를 고친다.
- 주석·KDoc은 `docs/code-conventions.md`를 따른다. `core:designsystem`은 한국어, `app-preview`는 기존 파일처럼 영어로 쓴다.
- 커밋 메시지: `type: 한국어 설명`, 빈 줄, `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- 파일 하나에 최상위 class/interface/object/enum은 하나다(`FileStructureRulesTest`).
- 패키지: `component.switches`(`switch`는 Kotlin 예약어), `component.segmentedcontrol`. 공개 `@Composable`은 `PushlogSwitchmark`, `PushlogSwitch`, `PushlogSegmentedControl` 셋뿐이다. 세그먼트 항목·indicator composable은 `internal`이다.
- `toggleable`·`selectable`·`clickable`·`indication`·`pointerInput`은 `component.pressable` 안에서만 쓴다(`ComponentRulesTest`). `Modifier.selectableGroup()`, `Modifier.semantics { contentDescription = … }`는 써도 된다.
- 크기 값은 `PushlogSwitchmarkSize.metrics()`가 `SwitchmarkMetrics`로, 색은 `PushlogSwitchmarkTone`의 `internal` 멤버가 `else` 없이 `when (this)`로 돌려준다. 세그먼트 컨트롤은 SEED에 variant·size가 없으므로 토큰을 컴포넌트 파일에서 바로 읽는다. `XxxDefaults`는 만들지 않는다.
- SEED px → 토큰: 2→`x0Half`, 4→`x1`, 6→`x1Half`, 8→`x2`, 10→`x2Half`, 12→`x3`, 16→`x4`, 20→`x5`, 24→`x6`, 32→`x8`. 토큰이 없는 값(1dp 선, 3dp 패딩, 26dp thumb, 트랙 너비 52/38/26, 항목 86×34, 20ms 지연, 0.8배, 0.38/0.58)은 그 값을 쓰는 파일 안 `private` 상수로 두고 SEED 출처를 주석으로 단다. 새 foundation 토큰은 만들지 않는다.
- 팔레트 값은 `pushlogThemedColor(LightPalette.x, DarkPalette.x)`로 읽는다.
- 기본값: 스위치 size `LARGE`(32), tone `NEUTRAL`(recipe의 brand 대신, 스펙 §6.3).
- ripple·state layer 금지. 스위치 줄 전체가 줄어들면 안 되고 마크만 준다. 라벨을 탭해도 상태가 바뀐다.
- 공개 `@Composable`마다 `app-preview`에 `<함수>PreviewKey`, `<함수>PreviewScreen.kt`(entry + 본문 + 섹션별 `@PushlogPreview`), `PreviewEntryProvider` 등록, `PreviewCatalog` 행. 섹션 하나가 320x470 캔버스(2배 글자 포함)를 넘지 않게 나눈다.
- 작업 중 `./gradlew :core:designsystem:testDebugUnitTest --tests '<클래스>'`, `./gradlew :konsist:test`, `./gradlew :app-preview:testDebugUnitTest`. 마지막 커밋 전 `./gradlew verifyStarter`. 포맷은 `./gradlew ktlintFormat`.
- 개발 중 미리보기는 prevue로 렌더링해 확인한다(`prevue` skill). 준비 승인을 물으면 사용자에게 묻는다.
- 기준 스크린샷(`build/outputs/roborazzi`)은 커밋하지 않는다.
- 픽셀 테스트는 내용을 `Box(Modifier.background(Color.White))` 안에 그리고, 반투명 토큰의 기대 색은 `token.compositeOver(아래 색)`로 계산한다(채널마다 ±2). 토큰 값은 컴포지션 안에서 `PushlogTheme.color.*`로 읽어 둔다.

## Review Focus

1. 스위치를 빠르게 두 번 눌러 애니메이션 중간에 되돌리면 thumb이 끝으로 튀지 않고 지금 위치에서 돌아와야 한다. → Task 2 `reversingMidwayDoesNotJump`.
2. 스위치 라벨이 여러 줄이면 마크는 첫 줄에 붙고, 2배 글자에서 줄이 늘어나야 한다(위치 보정이 음수가 되면 0). → Task 3 `multiLineLabelKeepsMarkOnFirstLine`, `largeFontScaleGrowsRow`.
3. 세그먼트 컨트롤을 처음 그릴 때 `selectedIndex`가 0이 아니면 indicator가 0번 칸에서 미끄러져 오지 않고 처음부터 그 칸에 있어야 한다. → Task 4 `initialIndicatorStartsAtSelectedSlot`.
4. `Modifier.fillMaxWidth()`로 넓히면 칸과 indicator가 함께 넓어져야 한다(indicator가 자연 너비에 머물면 안 된다). → Task 4 `fillMaxWidthStretchesCellsAndIndicator`.
5. 좁은 폭 + 2배 글자에서 라벨이 줄바꿈되어도 모든 항목 높이가 같고 indicator가 칸 높이를 다 덮어야 한다. → Task 4 `largeFontKeepsEqualHeights`.

---

### Task 1: 준비 — PR1 머지 확인, 브랜치

**Files:** 없음

- [ ] **Step 1: PR1이 develop에 있는지 확인**

```bash
git fetch origin
git grep -n -E "fun Modifier\.pushlogToggleableArea|fun Modifier\.pushlogPressScale|enum class PressScaleScope|fun Modifier\.pushlogSelectablePressable|pressedBorder: BorderStroke" origin/develop -- core/designsystem/src/main
```

Expected: 다섯 패턴이 모두 한 번 이상 나온다(`PushlogPressableArea.kt`, `PushlogPressIndication.kt`, `PressScaleScope.kt`, `PushlogPressable.kt`, `PushlogPressableSurface.kt`). 하나라도 없으면 **멈추고** 사용자에게 PR1 머지 여부를 묻는다. 시그니처가 PR1 계획의 "PR2·PR3이 쓰는 인터페이스"와 다르면 develop 쪽을 따르고 차이를 사용자에게 알린다.

- [ ] **Step 2: 워크트리와 브랜치 만들기 (git-flow skill)**

Orca 안(`$ORCA_WORKTREE_ID` 또는 `$ORCA_WORKSPACE_ID`가 있음):

```bash
orca worktree create --name design-system-switch-segmented --base-branch origin/develop \
  --branch feat/design-system-switch-segmented --issue 35 --parent-worktree active --setup skip --json
```

Orca 밖:

```bash
git worktree add ../Pushlog-worktrees/design-system-switch-segmented -b feat/design-system-switch-segmented origin/develop
```

이후 모든 명령은 워크트리 경로 안에서 돌린다.

- [ ] **Step 3: 시작 상태 확인**

Run: `./gradlew testStarter`
Expected: BUILD SUCCESSFUL. 실패하면 develop 자체 문제이므로 멈추고 사용자에게 알린다.

---

### Task 2: 스위치 마크 `PushlogSwitchmark`

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/switches/PushlogSwitchmarkTone.kt`
- Create: `.../component/switches/PushlogSwitchmarkSize.kt`
- Create: `.../component/switches/SwitchmarkMetrics.kt`
- Create: `.../component/switches/PushlogSwitchmark.kt`
- Create: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogSwitchmarkPreviewKey.kt`, `PushlogSwitchmarkPreviewScreen.kt`
- Modify: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/navigation/PreviewEntryProvider.kt`, `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/catalog/PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/switches/PushlogSwitchmarkTest.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/switches/PushlogSwitchmarkScreenshotTest.kt`

**Interfaces:**
- Consumes: `Modifier.pushlogPressScale(interactionSource: InteractionSource, enabled: Boolean): Modifier` (PR1, `component.pressable`), `pushlogThemedColor(light, dark)`.
- Produces:

```kotlin
enum class PushlogSwitchmarkTone { NEUTRAL, BRAND }

enum class PushlogSwitchmarkSize {
    /** 높이 16dp. 줄 높이는 24dp를 지킨다. */
    SMALL,
    /** 높이 24dp. */
    MEDIUM,
    /** 높이 32dp. */
    LARGE,
    ;
    @Composable internal fun metrics(): SwitchmarkMetrics
}

internal class SwitchmarkMetrics(
    val trackWidth: Dp,
    val trackHeight: Dp,
    val padding: Dp,
    val thumbSize: Dp,
    val rowMinHeight: Dp,   // 스위치 줄(Task 3)이 쓴다
    val gap: Dp,            // 마크와 라벨 사이
    val labelStyle: TextStyle,
)

// PushlogSwitchmarkTone (when (this))
@Composable internal fun trackColor(checked: Boolean, enabled: Boolean): Color
@Composable internal fun thumbColor(checked: Boolean, enabled: Boolean): Color

/** KDoc: 탭을 처리하지 않는다. 눌림은 축소만 보이고 색은 바뀌지 않는다(SEED switchmark).
 *  단독으로 쓰면 호출하는 쪽이 줄에 toggleable(indication = null, role = Role.Switch)을 붙이고
 *  같은 interactionSource를 넘긴다. 감싸는 쪽이 이미 눌림 피드백을 주면 scaleOnPress = false. */
@Composable
fun PushlogSwitchmark(
    checked: Boolean,
    modifier: Modifier = Modifier,
    tone: PushlogSwitchmarkTone = PushlogSwitchmarkTone.NEUTRAL,
    size: PushlogSwitchmarkSize = PushlogSwitchmarkSize.LARGE,
    enabled: Boolean = true,
    scaleOnPress: Boolean = true,
    interactionSource: InteractionSource? = null,
)
```

크기 값(SEED switchmark.yaml, switch.yaml):

| | SMALL | MEDIUM | LARGE |
|---|---|---|---|
| `trackWidth` | 26 (상수) | 38 (상수) | 52 (상수) |
| `trackHeight` | `x4` (16) | `x6` (24) | `x8` (32) |
| `padding` | `x0Half` (2) | `x0Half` (2) | 3 (상수) |
| `thumbSize` | `x3` (12) | `x5` (20) | 26 (상수) |
| `rowMinHeight` | `x6` (24) | `x6` (24) | `x8` (32) |
| `gap` | `x1Half` (6) | `x2` (8) | `x2Half` (10) |
| `labelStyle` | `t3Medium` | `t4Medium` | `t5Medium` |

상수 다섯(`SmallTrackWidth = 26.dp`, `MediumTrackWidth = 38.dp`, `LargeTrackWidth = 52.dp`, `LargePadding = 3.dp`, `LargeThumbSize = 26.dp`)은 `PushlogSwitchmarkSize.kt` 안 `private val`로 두고 `// SEED switchmark.yaml: size=32 …` 주석을 단다.

색(SEED switchmark.yaml; 꺼짐 트랙은 tone 구분 없이 `gray-600`):

| 멤버 | NEUTRAL | BRAND |
|---|---|---|
| `trackColor(false, *)` | `pushlogThemedColor(LightPalette.gray600, DarkPalette.gray600)` | 같음 |
| `trackColor(true, true)` | `bg.neutralSolid` | `bg.brandSolid` |
| `trackColor(true, false)` | `gray600` (위와 같은 themed 값) | `bg.brandSolid` (yaml에 비활성 덮어쓰기 없음) |
| `thumbColor(*, true)` | `fg.onNeutralSolid` | 꺼짐 `pushlogThemedColor(LightPalette.staticWhite, DarkPalette.staticWhite)`, 켜짐 `fg.onBrandSolid` |
| `thumbColor(*, false)` | `pushlogThemedColor(LightPalette.staticBlackAlpha700, DarkPalette.staticBlackAlpha700)` | 활성과 같음 |

모션(SEED switchmark.yaml·recipe; recipe는 `translateDuration`을 쓰지 않고 이동도 `scaleDuration`으로 묶는다):
- thumb 이동·크기: `fraction`을 `animateFloatAsState(if (checked) 1f else 0f, tween(motion.duration.d3 /*150*/, easing = timingFunction.easing))`로. 이동 거리 `trackWidth - trackHeight`(20/14/10dp), 크기 `lerp(0.8f, 1f, fraction)`. thumb 가운데 기준으로 줄인다. `fraction`은 `graphicsLayer { translationX; scaleX; scaleY }` 람다 안에서 읽어 그리기 단계에서만 바뀌게 한다.
- 트랙·thumb 색: `animateColorAsState(target, tween(motion.duration.d1 /*50*/, delayMillis = COLOR_DELAY_MILLIS, easing = timingFunction.easing))`. `private const val COLOR_DELAY_MILLIS = 20 // SEED switchmark.yaml colorDelay`.
- 비활성 투명도: `animateFloatAsState(if (enabled) 1f else DISABLED_ALPHA, tween(d1, easing))`, `private const val DISABLED_ALPHA = 0.38f // SEED switchmark.yaml disabled opacity`. 마크 전체(트랙+thumb)에 건다.
- 기기 애니메이션 배율 0은 Compose `MotionDurationScale`이 처리한다. 따로 분기하지 않는다(스펙 §6.5).

그리기 순서: `Box(modifier.size(trackWidth, trackHeight).then(if (scaleOnPress) Modifier.pushlogPressScale(source, enabled) else Modifier).graphicsLayer { alpha = disabledAlpha }.background(trackColor, radius.full).padding(padding), contentAlignment = CenterStart) { Box(Modifier.size(thumbSize).graphicsLayer { … }.background(thumbColor, radius.full)) }`. `interactionSource == null`이면 `remember { MutableInteractionSource() }`. 마크는 의미 정보를 두지 않는다. 파일 아래쪽에 `@PushlogPreview private fun PushlogSwitchmarkPreview()`(tone × 꺼짐/켜짐/비활성 꺼짐/비활성 켜짐 한 장).

미리보기 앱: 섹션 = tone마다 하나(LARGE, 네 상태, 하나는 `remember`로 토글), size마다 하나(꺼짐·켜짐). `@PushlogPreview`: `PushlogSwitchmarkNeutralPreview`, `PushlogSwitchmarkBrandPreview`, `PushlogSwitchmarkSizeSmallPreview`, `PushlogSwitchmarkSizeMediumPreview`, `PushlogSwitchmarkSizeLargePreview`. entry `pushlogSwitchmarkPreviewEntry(onBack)`, catalog 행 `PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogSwitchmark", PushlogSwitchmarkPreviewKey)`.

- [ ] **Step 1: 실패하는 테스트 작성 — `PushlogSwitchmarkTest`** (Robolectric, `@GraphicsMode(NATIVE)`, 라이트, NEUTRAL, LARGE)

thumb 왼쪽 끝 = 마크 가운데 가로줄에서 `fg.onNeutralSolid` 색 픽셀이 처음 나오는 x. `d` = density.

```kotlin
@Test fun thumbRestsAtStartWhenOff()
// checked = false → thumb 왼쪽 끝 == (3 + 26 * 0.1) dp * d ± 1   // 패딩 3 + 0.8배 축소 여백 2.6

@Test fun thumbTravelsWhenChecked()
// autoAdvance = false, checked를 false → true로 바꾸고 advanceTimeBy(300)
// thumb 왼쪽 끝 == (3 + 20) dp * d ± 1                          // 이동 거리 52 - 32

@Test fun reversingMidwayDoesNotJump()
// false → true, advanceTimeBy(75), 다시 false, advanceTimeBy(16)
// 5.6dp * d + 1 < thumb 왼쪽 끝 < 23dp * d - 1                    // 중간에서 돌아온다

@Test fun trackColorStartsAfter20msDelay()
// false → true, advanceTimeBy(16) → 트랙 픽셀(x = 2dp, 가운데 줄) == gray600(light)
// advanceTimeBy(150) 뒤 == bg.neutralSolid

@Test fun pressShrinksWithoutColorChange()
// interactionSource에 PressInteraction.Press 주입 + advanceTimeBy(300)
// 트랙 색 픽셀 수(가운데 줄) == round(52 * d * pressedScaleFactor(52 * d, 32 * d, d)) ± 1   // 30/32
// 트랙 가운데 왼쪽 픽셀 색 == 누르기 전과 같다

@Test fun noScaleIgnoresPress()
// scaleOnPress = false, Press 주입 → 트랙 색 픽셀 수 == 52dp * d ± 1

@Test fun disabledIsDimmedAndIgnoresPress()
// enabled = false, checked = false, Press 주입 + advanceTimeBy(300)
// 트랙 픽셀 == gray600.copy(alpha = 0.38f).compositeOver(Color.White) (채널 ±2)
// 트랙 색 픽셀 수 == 52dp * d ± 1
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSwitchmarkTest'`
Expected: 컴파일 실패(`PushlogSwitchmark` 없음).

- [ ] **Step 3: 구현** (Interfaces, 값·색·모션 표대로)

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSwitchmarkTest'`
Expected: PASS.

- [ ] **Step 5: 눌림 스크린샷 테스트 — `PushlogSwitchmarkScreenshotTest`**

PR1 `PushlogCheckmarkScreenshotTest` 구조(`@Config(qualifiers = "w1200dp-h1200dp")`, `autoAdvance = false`, Press 주입 뒤 `advanceTimeBy(800)`, 한 번 캡처). `pressedLight()` → `switchmark_pressed_light.png`, `pressedDark()` → `switchmark_pressed_dark.png`. 줄 = tone × size, 칸 = 눌림 꺼짐 / 눌림 켜짐. 칸마다 `MutableInteractionSource`를 따로 둔다.

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSwitchmarkScreenshotTest'`
Expected: PASS. 이미지를 열어 눌린 마크가 줄었고 색은 안 눌린 것과 같은지 본다.

- [ ] **Step 6: 미리보기와 규칙**

prevue로 `PushlogSwitchmarkPreviewScreen.kt`를 렌더링해 SEED 문서 그림(https://seed-design.io/components/switch)과 비율·thumb 크기를 비교한다. 다크 BRAND 비활성 켜짐이 흐린 초록인지 본다.
Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS(`PreviewCoverageRulesTest`, `PreviewEntryProviderTest` 포함).

- [ ] **Step 7: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/switches core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/switches app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: 스위치 마크 PushlogSwitchmark 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 3: 스위치 `PushlogSwitch`

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/switches/PushlogSwitch.kt`
- Create: `app-preview/.../preview/component/PushlogSwitchPreviewKey.kt`, `PushlogSwitchPreviewScreen.kt`
- Modify: `PreviewEntryProvider.kt`, `PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/switches/PushlogSwitchTest.kt`

**Interfaces:**
- Consumes: `Modifier.pushlogToggleableArea(value: Boolean, interactionSource: MutableInteractionSource, enabled: Boolean, role: Role, onValueChange: (Boolean) -> Unit): Modifier` (PR1), `PushlogSwitchmark(...)`, `PushlogSwitchmarkSize.metrics()` (Task 2).
- Produces:

```kotlin
/** KDoc: 바로 반영되는 설정에만 쓴다. 저장을 눌러야 반영되면 체크박스를 쓴다.
 *  [label]은 showLabel = false여도 접근성 이름으로 남는다. */
@Composable
fun PushlogSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    tone: PushlogSwitchmarkTone = PushlogSwitchmarkTone.NEUTRAL,
    size: PushlogSwitchmarkSize = PushlogSwitchmarkSize.LARGE,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
)
```

라벨 없는 스위치의 접근성 이름: `label`을 필수로 받고 `showLabel = false`면 그리지 않는 대신 줄에 `Modifier.semantics { contentDescription = label }`을 건다. 이름 없는 스위치를 컴파일 단계에서 막는다(`label: String?` + `require`보다 이른 실패).

줄 레이아웃(SEED switch recipe; PR1 체크박스 줄과 같은 규칙):
- `val source = interactionSource ?: remember { MutableInteractionSource() }` 하나를 줄과 마크에 넘긴다.
- `Row(modifier.pushlogToggleableArea(checked, source, enabled, Role.Switch, onCheckedChange).then(if (!showLabel) semantics { contentDescription = label } else Modifier).heightIn(min = metrics.rowMinHeight), horizontalArrangement = spacedBy(metrics.gap), verticalAlignment = Alignment.Top)`.
- 마크(라벨 앞, SEED 문서 순서): `PushlogSwitchmark(checked, Modifier.padding(top = (rowMinHeight - trackHeight) / 2), tone, size, enabled, scaleOnPress = true, interactionSource = source)`. 위 보정은 SMALL 4dp, MEDIUM·LARGE 0.
- 라벨(`showLabel`일 때): `Text(label, style = metrics.labelStyle, color = fg.neutral, modifier = Modifier.weight(1f, fill = false).graphicsLayer { alpha = labelAlpha }.padding(top = ((rowMinHeight - lineHeight.toDp()) / 2).coerceAtLeast(0.dp)))`. `labelAlpha`는 `animateFloatAsState(if (enabled) 1f else LABEL_DISABLED_ALPHA, tween(d1, easing))`, `private const val LABEL_DISABLED_ALPHA = 0.58f // SEED switch.yaml disabled label opacity`. 라벨 색은 비활성에서도 `fg.neutral`이다(투명도로만 흐려진다). `maxLines` 없음.
- 파일 아래쪽에 `@PushlogPreview` 하나.

미리보기 앱: 섹션 = tone마다(상태를 `remember`로 토글하는 줄 + 비활성 꺼짐·켜짐), size마다(SMALL은 줄 높이 24dp가 보이게), 긴 라벨(`Modifier.width(200.dp)`), 라벨 숨김. 섹션별 `@PushlogPreview`. entry·catalog 행(`"PushlogSwitch"`).

- [ ] **Step 1: 실패하는 테스트 작성 — `PushlogSwitchTest`** (Robolectric, NATIVE, 라이트, NEUTRAL)

마크 픽셀 = 라벨 앞 `x < 52dp` 구간의 트랙 색(`bg.neutralSolid`, checked = true) 픽셀 수.

```kotlin
@Test fun labelTapToggles()
// checked를 호이스팅, onNodeWithText("알림").performClick() → assertIsOn(); 다시 → assertIsOff()

@Test fun exposesSwitchRole()
// config[SemanticsProperties.Role] == Role.Switch, assertIsOff()

@Test fun rowIsSingleTapTarget()
// onAllNodes(hasClickAction()).assertCountEquals(1)

@Test fun disabledIgnoresTap()
// enabled = false, performClick() → onCheckedChange 0회, assertIsNotEnabled()

@Test fun pressShrinksMarkOnly()
// interactionSource에 Press 주입 + advanceTimeBy(300)
// 마크 픽셀 수 < 누르기 전, onNodeWithText(label, useUnmergedTree = true).captureToImage() 픽셀이 누르기 전과 같다

@Test fun realPressOnLabelShrinksMark()
// interactionSource 넘기지 않음, onNodeWithText(label).performTouchInput { down(center) }, advanceTimeBy(300)
// 마크 픽셀 수 < 누르기 전

@Test fun hiddenLabelBecomesAccessibilityName()
// showLabel = false, label = "알림"
// onNodeWithContentDescription("알림")의 Role == Role.Switch, performClick() → onCheckedChange(true)
// onNodeWithText("알림").assertDoesNotExist()

@Test fun smallSizeKeepsRow24dp()
// size = SMALL → 줄 높이 == 24dp * d ± 1, 마크 첫 트랙 픽셀 y == 4dp * d ± 1

@Test fun multiLineLabelKeepsMarkOnFirstLine()
// Box(Modifier.width(140.dp)) 안 세 줄 넘는 라벨, LARGE → 트랙 픽셀이 처음 나오는 y == 0 ± 1, 줄 높이 > 32dp * d

@Test fun largeFontScaleGrowsRow()
// LocalDensity fontScale = 2f, SMALL → 줄 높이 >= 24dp * d, 예외 없음, 트랙 첫 y >= 0
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSwitchTest'`
Expected: 컴파일 실패(`PushlogSwitch` 없음).

- [ ] **Step 3: 구현** (Interfaces와 줄 레이아웃대로)

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*switches*'`
Expected: PASS.

- [ ] **Step 5: 미리보기와 규칙**

prevue로 렌더링해 SMALL 마크가 24dp 줄 가운데에 있는지, 비활성 라벨이 흐린지, 2배 글자에서 잘리지 않는지 본다.
Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/switches core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/switches app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: 스위치 PushlogSwitch 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 4: 세그먼트 컨트롤 — 칸 나누기, 선택, indicator 이동

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/segmentedcontrol/PushlogSegmentedControlOption.kt`
- Create: `.../component/segmentedcontrol/PushlogSegmentedControl.kt`
- Create: `.../component/segmentedcontrol/SegmentedControlItem.kt`
- Create: `.../component/segmentedcontrol/SegmentedControlIndicator.kt`
- Create: `app-preview/.../preview/component/PushlogSegmentedControlPreviewKey.kt`, `PushlogSegmentedControlPreviewScreen.kt`
- Modify: `PreviewEntryProvider.kt`, `PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/segmentedcontrol/PushlogSegmentedControlTest.kt`

**항목 API 결정(스펙 §9가 빌더에게 맡김):** 슬롯이 아니라 라벨 목록이다. SEED item에는 라벨만 있고(아이콘·배지 슬롯 없음, 배지는 #21 뒤), 항목별 비활성이 필요하므로 `String` 목록 대신 작은 값 클래스 목록으로 받는다. 슬롯으로 열면 같은 너비·같은 높이·글자 색 규칙을 호출하는 쪽이 깰 수 있다. 배지가 붙을 때 이 클래스에 필드를 더한다.

**Interfaces:**
- Consumes: `PushlogPressableSurface(color, pressedColor, shape, enabled, onClick, modifier, selected, selectionMode = PressableSelectionMode.SINGLE, scaleScope = PressScaleScope.CONTENT, pressedBorder, interactionSource, content)` (PR1), `pushlogThemedColor`.
- Produces:

```kotlin
/** 세그먼트 항목 하나. [enabled]가 false거나 컨트롤 전체가 비활성이면 이 항목을 고를 수 없다. */
data class PushlogSegmentedControlOption(
    val label: String,
    val enabled: Boolean = true,
)

/** KDoc: 한 화면 안의 보기 전환에 쓴다. 화면 이동에는 쓰지 않는다(Tabs). 화면당 하나.
 *  항목은 2~4개다. 모든 항목이 같은 너비·높이이고, 좁으면 라벨이 줄바꿈된다. */
@Composable
fun PushlogSegmentedControl(
    options: List<PushlogSegmentedControlOption>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
)
// require(options.size in 2..4) { "PushlogSegmentedControl needs 2 to 4 options, got ${options.size}" }
// require(selectedIndex in options.indices) { "selectedIndex $selectedIndex is out of 0..${options.lastIndex}" }

@Composable
internal fun SegmentedControlItem(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
)

@Composable
internal fun SegmentedControlIndicator(
    modifier: Modifier = Modifier,
)   // 늘 gray00 + 1dp 테두리. 눌림·비활성 색은 선택 항목이 위에 덮어 칠한다(Task 5)
```

값(SEED segmented-control*.yaml, recipe):

| 부분 | 값 |
|---|---|
| 컨테이너 | 패딩 `x1`(4), 모양 `radius.full`, 색 `bg.neutralWeakAlpha` |
| 항목 | 최소 너비 86(상수, 칸 계산에만 쓴다), 최소 높이 34(상수), 가로 패딩 `x6`(24), 세로 패딩 `x1Half`(6), 모양 `radius.full` |
| 항목 라벨 | `t5Bold`, 가운데 정렬, `maxLines` 없음. 색: 비활성 `fg.disabled` > 선택 `fg.neutral` > 그 외 `fg.neutralSubtle`, `animateColorAsState(tween(colorTransition /*150*/, easing))` |
| indicator | 색 `pushlogThemedColor(LightPalette.gray00, DarkPalette.gray00)`, 1dp `stroke.neutralMuted` 테두리(항상), 모양 `radius.full`, 이동 `tween(d4 /*200*/, easing)` |

`ItemMinWidth = 86.dp`, `ItemMinHeight = 34.dp`, `StrokeWidth = 1.dp`는 쓰는 파일 안 `private val`에 SEED 출처 주석.

이 Task의 항목은 `PushlogPressableSurface(color = Color.Transparent, pressedColor = Color.Transparent, shape = radius.full, enabled, onClick, modifier, selected = selected, selectionMode = SINGLE, scaleScope = CONTENT, interactionSource)`로 둔다. 눌림 색·테두리와 선택 항목의 눌림·비활성 덮개는 Task 5에서 채운다. indicator는 끝까지 `gray00`이다.

`onClick`은 `{ if (index != selectedIndex) onSelectedIndexChange(index) }`다. 이미 선택된 항목을 다시 눌러도 알리지 않는다(라디오 그룹과 같은 동작).

레이아웃 — 이 계획에서 본문을 적는 유일한 알고리즘이다. 칸 너비를 재지 않고 나누기로 정하고, indicator 위치는 애니메이션되는 칸 번호 × 칸 너비다(SEED `translateX(index × 100%)`, 너비 `(100% − padding×2) / count`).

```kotlin
val sources = remember(options.size) { List(options.size) { MutableInteractionSource() } }
val position = remember { Animatable(selectedIndex.toFloat()) }   // 첫 그림은 이미 선택 칸에 있다
LaunchedEffect(selectedIndex) { position.animateTo(selectedIndex.toFloat(), tween(motion.duration.d4, easing = motion.timingFunction.easing)) }

Layout(
    content = {
        SegmentedControlIndicator()                                // measurables[0], 맨 뒤에 그린다
        options.forEachIndexed { index, option -> SegmentedControlItem(…, interactionSource = sources[index]) }
    },
    modifier = modifier.selectableGroup().clip(radius.full).background(bg.neutralWeakAlpha).padding(x1),
) { measurables, constraints ->                                     // constraints는 패딩 안쪽
    val indicator = measurables.first()
    val items = measurables.drop(1)
    val count = items.size
    val natural = maxOf(items.maxOf { it.maxIntrinsicWidth(Constraints.Infinity) }, ItemMinWidth.roundToPx())
    val width = (natural * count).coerceIn(constraints.minWidth, constraints.maxWidth)
    fun cellStart(i: Int) = width * i / count                      // 나머지 픽셀을 칸에 고루 나눈다
    val cell = width / count
    val height = maxOf(items.maxOf { it.maxIntrinsicHeight(cell) }, ItemMinHeight.roundToPx())
        .coerceIn(constraints.minHeight, constraints.maxHeight)
    val itemPlaceables = items.mapIndexed { i, m -> m.measure(Constraints.fixed(cellStart(i + 1) - cellStart(i), height)) }
    val indicatorPlaceable = indicator.measure(Constraints.fixed(cell, height))
    layout(width, height) {
        indicatorPlaceable.place((position.value * width / count).roundToInt(), 0)   // 배치 단계에서 읽어 재구성 없이 다시 배치만
        itemPlaceables.forEachIndexed { i, p -> p.place(cellStart(i), 0) }
    }
}
```

- 항목 최소 너비 86은 자연 너비 계산에만 쓰고 항목 modifier에는 걸지 않는다. 걸면 좁은 컨테이너에서 칸을 넘친다(SEED `max-width: 100%`).
- 항목 내용: `Box(Modifier.heightIn(min = ItemMinHeight).padding(horizontal = x6, vertical = x1Half), contentAlignment = Center) { Text(…) }`. 고정 크기로 측정되므로 표면이 칸을 다 채운다.

미리보기 앱: 섹션 = 항목 수 2/3/4(각각 `remember`로 선택을 바꾸는 컨트롤), 폭(`Modifier.fillMaxWidth()` 하나, `Modifier.width(200.dp)` + 긴 라벨 하나). 섹션별 `@PushlogPreview`(`PushlogSegmentedControlCountPreview`, `PushlogSegmentedControlWidthPreview`). entry·catalog 행(`"PushlogSegmentedControl"`). 파일 아래쪽 컴포넌트 `@PushlogPreview` 하나(3항목).

- [ ] **Step 1: 실패하는 테스트 작성 — `PushlogSegmentedControlTest`** (Robolectric, NATIVE, `@Config(qualifiers = "w600dp-h600dp")`, 라이트)

indicator 왼쪽 끝 = 컨트롤 위에서 `(4 + 3)dp` 줄(글자가 없는 높이)에서 `gray00`(라이트 흰색) 픽셀이 처음 나오는 x에서 테두리 1dp를 뺀 값. 칸 경계는 `4dp + width * i / count`.

```kotlin
@Test fun rejectsFewerThanTwoOptions()
// assertFailsWith<IllegalArgumentException> { setContent { PushlogSegmentedControl(listOf(opt("A")), 0, {}) } }

@Test fun rejectsMoreThanFourOptions()
// 다섯 개 → IllegalArgumentException

@Test fun rejectsSelectedIndexOutOfRange()
// 세 개, selectedIndex = 3 → IllegalArgumentException

@Test fun optionsAreRadioButtonsInSelectableGroup()
// 각 항목 노드 Role == Role.RadioButton, 선택 항목 assertIsSelected(), 나머지 assertIsNotSelected()
// 컨테이너(testTag) config.contains(SemanticsProperties.SelectableGroup)
// onAllNodes(hasClickAction()).assertCountEquals(3)

@Test fun tapSelectsOption()
// 상태 호이스팅, onNodeWithText("둘째").performClick() → "둘째" assertIsSelected(), "첫째" assertIsNotSelected()

@Test fun tappingSelectedOptionDoesNotNotify()
// selectedIndex = 0, "첫째" performClick() → onSelectedIndexChange 0회

@Test fun equalWidthCellsFromWidestLabel()
// 제약 없음, 라벨 "가", "아주 긴 라벨 하나", "나"
// 세 항목 노드 boundsInRoot 너비가 서로 같다(±1px)
// 컨트롤 너비 == 3 * max(86dp * d, 가장 긴 항목의 자연 너비) + 8dp * d ± 2

@Test fun shortLabelsUseMinimumWidth()
// 라벨 "A", "B" → 항목 너비 == 86dp * d ± 1, 높이 == 34dp * d ± 1, 컨트롤 높이 == 42dp * d ± 1

@Test fun narrowContainerWrapsLabels()
// Box(Modifier.width(200.dp)) 안 네 항목, 긴 라벨
// 항목 너비 모두 == (200 - 8) / 4 dp * d ± 1, 항목 높이 모두 같고 > 34dp * d, 컨트롤 너비 == 200dp * d

@Test fun fillMaxWidthStretchesCellsAndIndicator()
// Box(Modifier.width(400.dp)) 안 modifier = Modifier.fillMaxWidth(), 두 항목, selectedIndex = 1
// 항목 너비 == 196dp * d ± 1, indicator 왼쪽 끝 == (4 + 196) dp * d ± 2

@Test fun initialIndicatorStartsAtSelectedSlot()
// autoAdvance = false, 너비 300dp(Modifier.width), 세 항목, selectedIndex = 2로 처음 그림, 프레임 하나만 진행
// indicator 왼쪽 끝 == (4 + 292 * 2 / 3) dp * d ± 2

@Test fun indicatorMovesToNewSlot()
// autoAdvance = false, 너비 300dp, 0 → 2로 바꿈
// advanceTimeBy(100): 4dp * d + 2 < indicator 왼쪽 끝 < (4 + 292 * 2 / 3) dp * d - 2   // 이동 중
// advanceTimeBy(300) 뒤: == (4 + 292 * 2 / 3) dp * d ± 2

@Test fun largeFontKeepsEqualHeights()
// fontScale = 2f, Box(Modifier.width(240.dp)) 안 세 항목(한 라벨만 김)
// 항목 높이 모두 같다, indicator 세로 범위 == 선택 항목 세로 범위(±1px)
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSegmentedControlTest'`
Expected: 컴파일 실패.

- [ ] **Step 3: 구현** (Interfaces, 값 표, 레이아웃대로)

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSegmentedControlTest'`
Expected: PASS.

- [ ] **Step 5: 미리보기와 규칙**

prevue로 렌더링해 indicator가 칸과 정확히 겹치는지, 좁은 폭에서 줄바꿈된 라벨이 가운데인지 본다.
Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 6: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/segmentedcontrol core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/segmentedcontrol app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: 세그먼트 컨트롤 PushlogSegmentedControl 추가

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 5: 세그먼트 컨트롤 — 눌림, 비활성

**Files:**
- Modify: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/segmentedcontrol/SegmentedControlItem.kt`
- Modify: `.../component/segmentedcontrol/PushlogSegmentedControl.kt`
- Modify: `app-preview/.../preview/component/PushlogSegmentedControlPreviewScreen.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/segmentedcontrol/PushlogSegmentedControlTest.kt` (추가)
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/segmentedcontrol/PushlogSegmentedControlScreenshotTest.kt`

**Interfaces:**
- Consumes: Task 4의 `SegmentedControlItem`, `sources`.
- Produces: 시그니처 변경 없음. 동작만 채운다.

동작(SEED segmented-control recipe의 `engaged`·`checked`·`disabled` 조합):

| 상태 | 항목 표면 (`PushlogPressableSurface` 인자) | indicator |
|---|---|---|
| 선택 아님 + 눌림 | `color = Color.Transparent`, `pressedColor = bg.neutralWeakPressed`, `pressedBorder = BorderStroke(1.dp, stroke.neutralMuted)`, 내용만 축소(CONTENT) | `gray00` 그대로 |
| 선택 + 눌림 | `color = Color.Transparent`, `pressedColor = pushlogThemedColor(LightPalette.gray100, DarkPalette.gray100)`, `pressedBorder = BorderStroke(1.dp, stroke.neutralMuted)`, 내용만 축소 | `gray00` 그대로(항목이 덮는다) |
| 선택 + 비활성 | `color = bg.disabled`, `border = BorderStroke(1.dp, stroke.neutralMuted)`, 눌림 없음 | `gray00` 그대로(항목이 덮는다) |
| 그 외 | 투명 | `gray00` |

- 항목 실효 활성 = `option.enabled && enabled`(SEED React: 항목 disabled || 전체 disabled).
- SEED recipe를 그대로 따른다. indicator 색은 바뀌지 않는다. 선택 항목이 눌리거나 비활성이면 항목 자신의 배경(`indicator.pressed` `gray100`, `indicator.disabled` `bg.disabled`)과 indicator와 같은 1dp 테두리를 칠해 indicator를 덮는다(recipe 주석 "this covers the indicator"). 그래서 indicator가 이동하는 동안에도 덮개는 항목 칸에 붙어 있다.
- 색 전환은 `PushlogPressableSurface`의 눌림 색 전환을 쓴다(SEED item `background-color` 전환 `colorTransition`).

- [ ] **Step 1: 실패하는 테스트 추가 — `PushlogSegmentedControlTest`**

눌림은 실제 터치로 준다(`sources`는 컴포넌트 안에 있다): `onNodeWithText(x).performTouchInput { down(center) }` 뒤 `advanceTimeBy(300)`(autoAdvance = false). 표면 테두리 픽셀 = 항목 가로 가운데, 위에서 `(4 + 0.5)dp` 높이. indicator 픽셀 = 선택 칸 가로 가운데, 위에서 `(4 + 3)dp` 높이.

```kotlin
@Test fun pressedUnselectedShowsColorAndBorder()
// "둘째"(선택 아님) down → 테두리 픽셀 == stroke.neutralMuted.compositeOver(bg.neutralWeakPressed) (±2)
//   표면 픽셀(위에서 (4 + 3)dp) == bg.neutralWeakPressed
// up 뒤 advanceTimeBy(300) → 테두리 픽셀 == 누르기 전 색

@Test fun pressedSelectedItemCoversIndicatorWithGray100()
// "첫째"(선택) down → 선택 칸 픽셀(위에서 (4 + 3)dp) == LightPalette.gray100
//   테두리 픽셀 == stroke.neutralMuted.compositeOver(LightPalette.gray100) (±2)
// up 뒤 advanceTimeBy(300) → 선택 칸 픽셀 == LightPalette.gray00

@Test fun pressShrinksLabelOnly()
// "둘째" down → 라벨 글자 픽셀 가로 범위가 누르기 전보다 좁다, 항목 노드 boundsInRoot는 그대로

@Test fun disabledOptionIgnoresTap()
// options[1].enabled = false → "둘째" performClick() → 0회, assertIsNotEnabled(); "셋째" performClick() → 1회

@Test fun disabledOptionShowsNoPress()
// options[1].enabled = false, "둘째" down → 테두리 픽셀 == 누르기 전 색

@Test fun disabledControlIgnoresAllTaps()
// enabled = false → 세 항목 모두 assertIsNotEnabled(), performClick() 0회

@Test fun disabledSelectedItemCoversIndicatorWithBgDisabled()
// enabled = false, selectedIndex = 0 → 선택 칸 픽셀 == bg.disabled.compositeOver(LightPalette.gray00) (±2)
// 같은 검사를 options[0].enabled = false(전체는 활성)로도 한다
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSegmentedControlTest'`
Expected: 새 테스트 FAIL(색·테두리 없음), Task 4 테스트는 PASS.

- [ ] **Step 3: 구현** (동작 표대로)

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*segmentedcontrol*'`
Expected: PASS.

- [ ] **Step 5: 스크린샷 테스트 — `PushlogSegmentedControlScreenshotTest`**

`@Config(qualifiers = "w1200dp-h1200dp")`, `autoAdvance = false`, 캡처마다 컨트롤 하나(터치 포인터가 하나라서).
- `pressedUnselectedLight/Dark()` → `segmented_control_pressed_unselected_{light,dark}.png`: 세 항목, 둘째 down + `advanceTimeBy(800)`.
- `pressedSelectedLight/Dark()` → `segmented_control_pressed_selected_{light,dark}.png`: 첫째(선택) down.
- `movedLight/Dark()` → `segmented_control_moved_{light,dark}.png`: 0 → 2로 바꾸고 `advanceTimeBy(800)`.

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogSegmentedControlScreenshotTest'`
Expected: PASS. 이미지를 열어 눌린 항목에 테두리가 있고, 다크 indicator가 검정(`gray00`)이고 눌린 선택 항목이 `gray100`으로 덮였는지 본다.

- [ ] **Step 6: 미리보기 섹션 추가**

`PushlogSegmentedControlPreviewScreen`에 비활성 섹션(전체 비활성 하나, 둘째 항목만 비활성 하나, 선택 항목 비활성 하나)과 `@PushlogPreview PushlogSegmentedControlDisabledPreview`를 더한다.
Run: `./gradlew :konsist:test :app-preview:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 7: 커밋**

```bash
./gradlew ktlintFormat
git add core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/segmentedcontrol core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/segmentedcontrol app-preview/src/main/kotlin/com/citytexi/pushlog/preview
git commit -m "feat: 세그먼트 컨트롤에 눌림과 비활성 표시를 더함

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 6: 규칙 확인, 문서, 전체 검증

**Files:**
- Modify: `docs/architecture/design-system.md`
- Modify (필요할 때만): `.claude/rules/design-system-components.md`

- [ ] **Step 1: Konsist가 새 패키지를 잡는지 확인 (커밋하지 않음)**

1. `PushlogSegmentedControl.kt`에 `import androidx.compose.foundation.selection.selectable`을 더한다 → `./gradlew :konsist:test` → `only the pressable package handles taps` FAIL 확인 → 되돌린다.
2. `PushlogSwitchPreviewScreen.kt` 이름을 잠시 `PushlogSwitchPreviewScreenX.kt`로 바꾼다 → `./gradlew :konsist:test` → `every public design system component has a preview screen` FAIL 확인 → 되돌린다.
3. `git status`로 남은 변경이 없는지 본다.

- [ ] **Step 2: `docs/architecture/design-system.md` 고치기**

PR1이 만든 `## 선택 컨트롤` 절에 코드로 복원할 수 없는 것만 더한다.
- 패키지 `switches`(`PushlogSwitchmark`, `PushlogSwitch`; `switch`가 예약어라 이 이름), `segmentedcontrol`(`PushlogSegmentedControl`, 항목·indicator는 `internal`).
- 스위치 마크는 축소만 하고 눌림 색이 없다(SEED). 트랙·thumb 색은 20ms 지연이 있어서 `PressColorNode`가 아니라 마크가 직접 애니메이션한다. 애니메이션 배율 0은 Compose `MotionDurationScale`에 맡긴다(SEED는 reduced-motion에서 전환을 정하지 않는다).
- 스위치 크기 이름 `SMALL`/`MEDIUM`/`LARGE` = 높이 16/24/32. SMALL도 줄 높이 24dp. 기본값은 `LARGE`.
- 라벨 숨김은 `showLabel = false`이고 `label`은 접근성 이름으로 남는다. `label: String?`로 두지 않은 이유(이름 없는 스위치를 컴파일 단계에서 막는다).
- SEED recipe는 brand 켜짐 thumb에 `fg.onBrandSolid` 대신 `static-white`를 쓰지만 Pushlog에서는 둘이 같은 값이라 yaml을 따랐다.
- 세그먼트: 항목은 라벨 목록(`PushlogSegmentedControlOption`)으로 받는다(슬롯이 아닌 이유: 같은 크기·글자 색 규칙을 지키고, 배지는 #21 뒤 필드로 더한다). 항목 2~4개는 `require`.
- 세그먼트 레이아웃: 자연 너비 = max(가장 넓은 항목, 86dp) × 항목 수를 제약 안으로 자르고 칸을 나누기로 정한다. indicator 위치는 애니메이션되는 칸 번호 × 칸 너비라 위치를 재지 않는다. 86dp를 항목 modifier에 걸지 않는 이유(좁은 폭에서 넘침).
- 선택 항목의 눌림·비활성은 SEED처럼 항목 배경(`gray100`, `bg.disabled`)과 1dp 테두리로 indicator를 덮는다. indicator 색은 늘 `gray00`이다.
- "프리뷰 스크린샷" 절의 직접 쓴 스크린샷 테스트 목록에 `PushlogSwitchmarkScreenshotTest`(눌림 주입), `PushlogSegmentedControlScreenshotTest`(실제 터치 눌림, indicator 이동 뒤)를 더한다.

- [ ] **Step 3: `.claude/rules/design-system-components.md` 확인**

PR1이 더한 "탭 영역 modifier + 눌림 표시 modifier" 줄이 스위치에도 그대로 맞는지 본다. 이 PR에서 새로 생긴, 놓치기 쉬운 규칙은 "variant·size가 없는 컴포넌트(세그먼트)는 토큰을 컴포넌트 파일에서 바로 읽는다" 하나다. 그 줄을 "크기별 값은 …" 줄 뒤에 한 문장으로 더한다. 그 밖에는 고치지 않는다.

- [ ] **Step 4: 링크 검사**

Run: `python3 docs/script/check_links.py docs`
Expected: 0건.

- [ ] **Step 5: 전체 검증**

Run: `./gradlew ktlintFormat && ./gradlew verifyStarter`
Expected: BUILD SUCCESSFUL. 실패하면 고치고 다시 돌린다. 건너뛰고 완료를 선언하지 않는다.

- [ ] **Step 6: 커밋**

```bash
git add docs/architecture/design-system.md .claude/rules/design-system-components.md
git commit -m "docs: 스위치와 세그먼트 컨트롤 결정을 디자인 시스템 문서에 남김

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 7: push와 PR (사용자 확인 후)

push와 PR 생성은 사용자에게 먼저 묻고, 승인을 받은 뒤에만 한다.

- [ ] **Step 1: 사용자 확인**

`git log --oneline origin/develop..HEAD`와 `verifyStarter` 결과를 보여 주고 push·PR을 해도 되는지 묻는다.

- [ ] **Step 2: push**

Run: `git push -u origin feat/design-system-switch-segmented`

- [ ] **Step 3: PR 생성**

`.github/PULL_REQUEST_TEMPLATE.md`의 절을 따라 한국어로 쓴다. Summary에 세 컴포넌트, Related Issue에 `Part of #35`(PR3이 나중에 머지되면 그쪽이 `Closes #35`), Notes for Reviewer에 결정 셋(`showLabel` + 필수 `label`, 라벨 목록 API, 선택 항목 눌림·비활성을 SEED처럼 항목 배경으로 indicator 위에 덮음)과 SEED recipe·yaml이 다른 곳(brand thumb). 본문 끝 줄은 `🤖 Generated with [Claude Code](https://claude.com/claude-code)`.

```bash
gh pr create --base develop --head feat/design-system-switch-segmented \
  --title "feat: add switch and segmented control selection controls" \
  --body-file <본문 파일>
```

Expected: PR URL. UI diff 봇 코멘트가 달리면 새 프리뷰·스크린샷을 확인한다.
