# 디자인 시스템 동작 컴포넌트 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `core:designsystem`에 `PushlogActionButton`, `PushlogChip`, `PushlogContextualFloatingButton`을 SEED 정의대로 만들고 `PushlogButton`을 교체한다.

**Architecture:** Compose foundation(`Box`/`Row` + `clickable(indication = null)`) 위에 직접 만든다. 세 컴포넌트는 내부 공용 눌림 modifier(`Modifier.pushlogPressable`)와 내부 로딩 원(`PushlogProgressCircle`)을 함께 쓴다. 값은 `PushlogTheme` 토큰에서만 읽는다.

**Tech Stack:** Kotlin, Jetpack Compose (foundation), Robolectric + Roborazzi, Konsist, Navigation 3(`app-preview`).

**Spec:** `docs/superpowers/specs/2026-11-13-design-system-action-components-design.md` (이하 "스펙"). SEED 값 출처는 `daangn/seed-design` 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`의 `packages/rootage/components/{action-button,chip,contextual-floating-button,progress-circle}.yaml`이다. 표에 없는 값은 yaml을 연다(`gh api "repos/daangn/seed-design/contents/packages/rootage/components/<name>.yaml?ref=5f25880e93fe6f6eedba6f3842805cd8f6f352d1" -q .content | base64 -d`).

## Global Constraints

- 작업 위치: worktree `/Users/jeonheehoon/orca/workspaces/Pushlog/design-system-action-components`, 브랜치 `feat/design-system-action-components`.
- `AGENTS.md` 금지 사항을 지킨다. `@Suppress`, detekt baseline, ktlint 비활성화, Konsist·`ModuleRules` 약화는 금지다.
- 주석·KDoc은 `docs/code-conventions.md`를 따른다. 코드가 이미 말하는 것은 쓰지 않는다. `@return`·`@param`은 이름이 말하지 못할 때만 쓴다. 다른 컴포넌트의 현재 상태를 단정하지 않는다. 알림 원문을 넣지 않는다.
- 커밋 메시지: `type: 한국어 설명`. 끝에 `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- 파일 하나에 최상위 class/interface/object/enum은 하나다(`FileStructureRulesTest`).
- `component` 패키지의 공개 최상위 `@Composable`마다 `app-preview`에 `<함수 이름>PreviewKey`, `<함수 이름>PreviewScreen.kt`가 있어야 한다(`PreviewCoverageRulesTest`). 공용 부품은 `internal`이다.
- 값: 토큰으로 표현되는 값은 토큰을 읽는다. SEED가 px로 적었어도 같은 크기의 토큰이 있으면 토큰을 쓴다(14→`x3Half`, 16→`x4`, 18→`x4Half`, 32→`x8`, 36→`x9`, 40→`x10`). 토큰이 없는 값(22dp, 44dp, 1dp, 2dp, progress 애니메이션 값)만 파일 안 `private val`로 두고 SEED 출처 주석을 단다. 새 foundation 토큰은 만들지 않는다.
- ripple 금지. 눌리면 표면 색만 바뀌고 글자·아이콘 색은 그대로다.
- `core:designsystem`에 아이콘 라이브러리 의존을 더하지 않는다.
- enum 상수는 UPPER_SNAKE(`PreviewCategory.COMPONENT`와 같다).
- 작업 중 `./gradlew testStarter`, 끝나기 전 `./gradlew verifyStarter`. 포맷은 `./gradlew ktlintFormat`.
- 개발 중 미리보기는 prevue(`prevue show <파일> --lang ko`)로 렌더링해 눈으로 확인한다. prevue가 준비 승인을 물으면 사용자에게 묻는다.

## Review Focus

1. 글자 크기 200%에서 칩이 잘리지 않고 늘어나야 한다. 플로팅 버튼 라벨은 한 줄이고 말줄임이 된다. → Task 3·4 스크린샷의 fontScale 2 캡처와 Task 4의 `labelIsSingleLine` 테스트.
2. loading 중에 버튼 너비가 변하면 안 되고, 클릭도 안 되어야 한다. → Task 2 `loadingKeepsWidthAndBlocksClick`.
3. 다크에서 눌림 색이 라이트 값으로 남는 것(테마 전환). → Task 2·3·4 다크 pressed 캡처.
4. disabled 상태에서 눌러도 축소·색 변화가 없어야 한다. → Task 1 `disabledDoesNotShrink`.
5. 아이콘만 버튼의 터치 영역이 40dp 미만이면 안 된다(그려지는 크기는 36dp일 수 있음). → Task 2·4 `iconOnlyTouchTargetAtLeast40dp`.

---

### Task 1: 공용 부품 — 눌림 modifier, 로딩 원, 샘플 아이콘

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/PushlogPressable.kt`
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/PushlogProgressCircle.kt`
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/ReduceMotion.kt`
- Modify: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/theme/PushlogTheme.kt` (`LocalPushlogDarkTheme` 추가)
- Create: `core/ui/src/main/kotlin/com/citytexi/pushlog/core/ui/preview/PreviewSampleIcons.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/PushlogPressableTest.kt`

**Interfaces:**
- Produces:
  - `internal fun pressedScaleFactor(widthPx: Float, heightPx: Float, density: Float): Float`: 기준 길이 `ref = max(heightPx, widthPx / 4, 24 * density)`, 반환 `(ref - 2 * density) / ref`.
  - `internal fun Modifier.pushlogPressable(interactionSource: MutableInteractionSource, color: Color, pressedColor: Color, shape: Shape, enabled: Boolean, loading: Boolean, onClick: () -> Unit, role: Role = Role.Button): Modifier`: clip + 배경색(눌림 시 `pressedColor`, `animateColorAsState` + `motion.duration.colorTransition` + `motion.timingFunction.easing`) + 축소(`graphicsLayer`, `motion.duration.pressedScale` + `motion.timingFunction.pressedScale`) + `clickable(interactionSource, indication = null, enabled = enabled && !loading, role)`. loading이면 배경은 `pressedColor` 고정. disabled·loading·reduce motion이면 축소하지 않는다.
  - `internal fun Modifier.pushlogToggleablePressable(...)`: 위와 같되 `clickable` 대신 `toggleable(value = selected, role = Role.Checkbox, onValueChange = { onClick() })`. 칩 전용. 두 함수는 같은 private 구현을 쓴다.
  - `@Composable internal fun rememberReduceMotion(): Boolean`: `Settings.Global.ANIMATOR_DURATION_SCALE`가 0이면 true.
  - `internal val LocalPushlogDarkTheme = staticCompositionLocalOf { false }`: `PushlogTheme`이 `darkTheme`을 넣는다. 팔레트 트랙 색 선택에만 쓴다.
  - `@Composable internal fun PushlogProgressCircle(size: Dp, trackColor: Color, rangeColor: Color, modifier: Modifier = Modifier)`: SEED indeterminate 원. 두께 2dp(상수). 회전·호 길이 애니메이션은 `progress-circle.yaml`의 indeterminate 값(주기 1.2초, cubic-bezier 세 개)을 파일 안 상수로 둔다. 접근성 노드를 만들지 않는다(상태 설명은 컴포넌트가 붙인다).
  - `object PreviewSampleIcons { val Plus: ImageVector; val Close: ImageVector; val ChevronDown: ImageVector; val ArrowUp: ImageVector }` (`core:ui`, 24×24 viewport, 단색 fill `Color.Black` — 쓰는 쪽에서 `Painter`로 바꾸고 tint한다). 미리보기·테스트·`app-preview` 전용. KDoc에 "제품 아이콘 아님, 아이콘 세트는 범위 밖(스펙 §2)"을 적는다.

- [ ] **Step 1: 실패하는 테스트 작성** (`PushlogPressableTest`, Robolectric + `createComposeRule`)

```kotlin
@Test fun scaleUsesHeightWhenTallest() = assertEquals((40f - 2f) / 40f, pressedScaleFactor(widthPx = 100f, heightPx = 40f, density = 1f), 1e-4f)
@Test fun scaleUsesQuarterWidthForWideElements() = assertEquals((80f - 2f) / 80f, pressedScaleFactor(320f, 40f, 1f), 1e-4f)
@Test fun scaleUses24dpFloorForSmallElements() = assertEquals((48f - 4f) / 48f, pressedScaleFactor(40f, 20f, 2f), 1e-4f)
@Test fun disabledDoesNotShrink()  // Box(Modifier.size(100.dp).pushlogPressable(..., enabled = false)), Press 주입 후 mainClock 300ms 진행 → 노드 graphicsLayer scale 1f (테스트용 semantics 대신 onGloballyPositioned로 boundsInRoot 비교: 크기 변화 없음)
@Test fun pressedSwapsBackgroundOnly()  // Press 주입 후 captureToImage 중앙 픽셀 == pressedColor
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests "*PushlogPressableTest*"`
Expected: FAIL (`pressedScaleFactor` 미정의로 컴파일 실패)

- [ ] **Step 3: 위 Interfaces의 internal 부품 구현**, `PushlogTheme`에 `LocalPushlogDarkTheme provides darkTheme` 추가, `PreviewSampleIcons` 작성.

- [ ] **Step 4: 통과 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests "*PushlogPressableTest*"`
Expected: PASS

- [ ] **Step 5: 커밋**

```bash
git add core/designsystem core/ui
git commit -m "feat: 디자인 시스템 공용 눌림 부품과 로딩 원 추가"
```

---

### Task 2: `PushlogActionButton`

**Files:**
- Create: `core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/component/PushlogActionButton.kt`
- Create: `.../component/PushlogActionButtonVariant.kt` (`enum class PushlogActionButtonVariant { BRAND_SOLID, NEUTRAL_SOLID, NEUTRAL_WEAK, CRITICAL_SOLID, GHOST }`)
- Create: `.../component/PushlogActionButtonSize.kt` (`enum class PushlogActionButtonSize { SMALL, MEDIUM, LARGE }`)
- Create: `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/component/PushlogActionButtonPreviewKey.kt`, `PushlogActionButtonPreviewScreen.kt`
- Modify: `app-preview/.../navigation/PreviewEntryProvider.kt`, `app-preview/.../catalog/PreviewCatalog.kt`
- Test: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/component/PushlogActionButtonTest.kt`, `PushlogActionButtonScreenshotTest.kt`

**Interfaces:**
- Consumes: Task 1 `pushlogPressable`, `PushlogProgressCircle`, `LocalPushlogDarkTheme`, `PreviewSampleIcons`.
- Produces:

```kotlin
@Composable fun PushlogActionButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    variant: PushlogActionButtonVariant = PushlogActionButtonVariant.NEUTRAL_WEAK,
    size: PushlogActionButtonSize = PushlogActionButtonSize.MEDIUM,
    prefixIcon: Painter? = null, enabled: Boolean = true, loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
)
@Composable fun PushlogActionButton(
    icon: Painter, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    variant: PushlogActionButtonVariant = PushlogActionButtonVariant.NEUTRAL_WEAK,
    size: PushlogActionButtonSize = PushlogActionButtonSize.MEDIUM,
    enabled: Boolean = true, loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
)
```

기본 variant가 `NEUTRAL_WEAK`인 이유: SEED 문서가 "대부분의 액션은 Neutral Weak"라고 한다. KDoc에 강조 Solid는 화면당 하나라는 SEED 규칙을 한 줄 적는다.

값 (`action-button.yaml`):

| | 표면 / 눌림 | 글자·아이콘 | progress 트랙 |
|---|---|---|---|
| BRAND_SOLID | `bg.brandSolid` / `bg.brandSolidPressed` | `fg.onBrandSolid` | `staticWhiteAlpha300` |
| NEUTRAL_SOLID | `bg.neutralSolid` / `bg.neutralSolidPressed` | `fg.onNeutralSolid` | `staticWhiteAlpha300` |
| NEUTRAL_WEAK | `bg.neutralWeak` / `bg.neutralWeakPressed` | `fg.neutral` | `gray500` |
| CRITICAL_SOLID | `bg.criticalSolid` / `bg.criticalSolidPressed` | `fg.onCriticalSolid` | `staticWhiteAlpha300` |
| GHOST | `bg.transparent` / `bg.transparentPressed` | `fg.neutral` | `gray500` |
| disabled(전부) | `bg.disabled`, GHOST는 `bg.transparent` | `fg.disabled` | — |

progress 범위 색 = 글자 색. 트랙 팔레트는 `LocalPushlogDarkTheme`으로 `LightPalette`/`DarkPalette`에서 고른다.

| size | 최소 높이 / 모서리 | 글자 레이아웃: paddingX / paddingY / gap / 아이콘 / 글자 | 아이콘만: 최소 너비 / padding / 아이콘 | progress |
|---|---|---|---|---|
| SMALL | `x9` / `r2` | `x3Half` / `x2` / `x1` / `x3Half` / `t4Bold` | `x9` / `x2` / `x4` | `x3Half` |
| MEDIUM | `x10` / `r2` | `x4` / `x2Half` / `x1Half` / `x4` / `t4Bold` | `x10` / `x2Half` / `x4Half` | `x4` |
| LARGE | `x13` / `r3` | `x5` / `x3Half` / `x2` / 22dp / `t6Bold` | `x13` / `x3Half` / 22dp | `x4Half` |

loading: 라벨·아이콘은 `alpha 0`으로 자리를 지키고 가운데에 `PushlogProgressCircle`. semantics: 라벨(아이콘만은 `contentDescription`)을 이름으로 두고 `stateDescription = stringResource(R.string.designsystem_loading)`. 클릭 불가지만 `disabled()`를 붙이지 않는다. 글자 200%에서 라벨은 줄바꿈하고 높이가 늘어난다(최소 높이).

- [ ] **Step 1: 실패하는 테스트 작성** (`PushlogActionButtonTest`)

```kotlin
@Test fun iconOnlyExposesContentDescription()  // onNodeWithContentDescription("추가").assertHasClickAction()
@Test fun loadingKeepsWidthAndBlocksClick()   // 같은 text로 loading=false/true 너비 비교 assertEquals, loading 상태 performClick 후 클릭 카운트 0, stateDescription == "불러오는 중", SemanticsProperties.Disabled 없음
@Test fun disabledIsNotClickable()             // enabled=false → assertIsNotEnabled()
@Test fun iconOnlyTouchTargetAtLeast40dp()     // SMALL 아이콘만: fetchSemanticsNode().touchBoundsInRoot 너비·높이 >= 40.dp.toPx(), 그려진 크기는 36dp
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests "*PushlogActionButtonTest*"`
Expected: FAIL (미정의)

- [ ] **Step 3: 두 enum과 두 오버로드 구현.** 같은 파일에 `@PushlogPreview` 하나(variant 5개 × enabled·loading·disabled, `PushlogTheme`으로 감쌈). 구현 뒤 `prevue show <PushlogActionButton.kt 절대 경로> --lang ko`로 확인한다. 터치 영역은 Compose 기본 확장(`LocalViewConfiguration.minimumTouchTargetSize` 48dp)으로 충족되는지 테스트로 확인하고, 모자라면 그때 보강한다.

- [ ] **Step 4: 통과 확인** — 같은 명령, Expected: PASS

- [ ] **Step 5: 스크린샷 테스트 작성** (`PushlogActionButtonScreenshotTest`, 기존 `PushlogButtonScreenshotTest` 형식). 라이트/다크 × 세 장: `action_button_states_{light,dark}`(variant × enabled/pressed/disabled/loading, pressed는 `MutableInteractionSource().tryEmit(PressInteraction.Press(Offset.Zero))` 후 `mainClock.advanceTimeBy(300)`), `action_button_sizes_{light,dark}`(size × 글자/앞 아이콘/아이콘만), `action_button_font2x_{light,dark}`(상태 표를 `CompositionLocalProvider(LocalDensity provides Density(density, fontScale = 2f))`로).

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests "*PushlogActionButtonScreenshotTest*"`
Expected: PASS, `core/designsystem/build/outputs/roborazzi/action_button_*.png` 6장 생성

- [ ] **Step 6: 미리보기 앱 화면.** `PushlogActionButtonPreviewKey`(`@Serializable data object ... : NavKey`), `PushlogActionButtonPreviewContent()`(섹션: variant별 상태, size별 레이아웃, 긴 라벨) + `pushlogActionButtonPreviewEntry(onBack)`, `previewEntryProvider`에 호출 추가, `previewCatalog`에 `PreviewCatalogItem(PreviewCategory.COMPONENT, "PushlogActionButton", PushlogActionButtonPreviewKey)` 추가.

Run: `./gradlew :app-preview:testDebugUnitTest :konsist:test`
Expected: PASS(`PreviewEntryProviderTest`, `PreviewCoverageRulesTest` 포함)

- [ ] **Step 7: 커밋**

```bash
git add core/designsystem app-preview
git commit -m "feat: PushlogActionButton 추가"
```

---

### Task 3: `PushlogChip`

**Files:**
- Create: `.../component/PushlogChip.kt`, `PushlogChipVariant.kt` (`SOLID, OUTLINE_WEAK`), `PushlogChipSize.kt` (`SMALL, MEDIUM, LARGE`)
- Create: `app-preview/.../component/PushlogChipPreviewKey.kt`, `PushlogChipPreviewScreen.kt`; Modify entry provider, catalog
- Test: `.../component/PushlogChipTest.kt`, `PushlogChipScreenshotTest.kt`

**Interfaces:**
- Consumes: Task 1 `pushlogPressable`, `pushlogToggleablePressable`, `PreviewSampleIcons`.
- Produces:

```kotlin
@Composable fun PushlogChip(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    variant: PushlogChipVariant = PushlogChipVariant.OUTLINE_WEAK,
    size: PushlogChipSize = PushlogChipSize.MEDIUM,
    selected: Boolean? = null, prefixIcon: Painter? = null, suffixIcon: Painter? = null,
    enabled: Boolean = true, interactionSource: MutableInteractionSource? = null,
)
```

`selected == null`이면 버튼(`pushlogPressable`, `Role.Button`), 아니면 체크 상태(`pushlogToggleablePressable`, `Role.Checkbox`). 뒤 아이콘은 그림이고 칩 전체가 탭 하나다(스펙 §6.3).

값 (`chip.yaml`, 모서리 `radius.full`, 글자 `t4Medium`, 글자·아이콘 `fg.neutral`):

| variant | 기본 / 눌림 | selected / selected+눌림 | 테두리 |
|---|---|---|---|
| SOLID | `bg.neutralWeakAlpha` / `bg.neutralWeakAlphaPressed` | `bg.neutralSolid` / `bg.neutralSolidPressed`, 글자·아이콘 `fg.onNeutralSolid` | 없음 |
| OUTLINE_WEAK | `bg.transparent` / `bg.transparentPressed` | `bg.neutralWeak` / `bg.neutralWeakPressed`, 글자 `fg.neutral` | 1dp `stroke.neutralWeak`, selected는 `stroke.neutralContrast` |

disabled: 전체 `alpha 0.5`(selected 포함).

| size | 최소 높이 | root paddingX | 앞 아이콘 | 뒤 아이콘 | 최소 너비 |
|---|---|---|---|---|---|
| SMALL | `x8` | `x1Half` | `x3Half` | `x3Half` | 44dp |
| MEDIUM | `x9` | `x2` | `x4` | `x3Half` | `x12` |
| LARGE | `x10` | `x2Half` | `x4` | `x4` | `x13` |

라벨 paddingX `x1Half`, 앞 아이콘 paddingLeft `x1Half`, 뒤 아이콘 paddingRight `x1Half`. 높이는 최소 높이(`heightIn(min = …)`), 라벨 한 줄 + 말줄임(스펙 §8).

- [ ] **Step 1: 실패하는 테스트 작성** (`PushlogChipTest`)

```kotlin
@Test fun selectableChipExposesToggleState()  // selected=true → assertIsOn(); selected=false → assertIsOff(); role == Role.Checkbox
@Test fun plainChipIsButton()                 // selected=null → role == Role.Button, ToggleableState 없음
@Test fun suffixIconIsNotSeparateTarget()     // suffixIcon 있음 → 클릭 가능한 노드가 1개(onAllNodes(hasClickAction()).assertCountEquals(1))
@Test fun growsWithFontScale()                // fontScale 2에서 MEDIUM 높이 > 36dp
```

- [ ] **Step 2: 실패 확인** — `./gradlew :core:designsystem:testDebugUnitTest --tests "*PushlogChipTest*"`, Expected: FAIL

- [ ] **Step 3: enum 두 개와 `PushlogChip` 구현**, `@PushlogPreview` 하나(variant × 기본/selected/disabled, 앞뒤 아이콘). prevue로 확인.

- [ ] **Step 4: 통과 확인** — 같은 명령, Expected: PASS

- [ ] **Step 5: 스크린샷** `chip_states_{light,dark}`(variant × enabled/pressed/selected/selected+pressed/disabled), `chip_sizes_{light,dark}`(size × 글자/앞 아이콘/뒤 아이콘), `chip_font2x_{light,dark}`. 방법은 Task 2 Step 5와 같다.

Run: `./gradlew :core:designsystem:testDebugUnitTest --tests "*PushlogChipScreenshotTest*"`
Expected: PASS, `chip_*.png` 6장

- [ ] **Step 6: 미리보기 앱 화면** (`PushlogChipPreviewKey`, `PushlogChipPreviewContent`, `pushlogChipPreviewEntry`, 등록, 카탈로그 `"PushlogChip"`). 선택 칩은 `remember`로 실제로 켜고 끈다.

Run: `./gradlew :app-preview:testDebugUnitTest :konsist:test`, Expected: PASS

- [ ] **Step 7: 커밋** — `git commit -m "feat: PushlogChip 추가"`

---

### Task 4: `PushlogContextualFloatingButton`

**Files:**
- Create: `.../component/PushlogContextualFloatingButton.kt`, `PushlogContextualFloatingButtonVariant.kt` (`SOLID, LAYER`)
- Create: `app-preview/.../component/PushlogContextualFloatingButtonPreviewKey.kt`, `PushlogContextualFloatingButtonPreviewScreen.kt`; Modify entry provider, catalog
- Test: `.../component/PushlogContextualFloatingButtonTest.kt`, `PushlogContextualFloatingButtonScreenshotTest.kt`

**Interfaces:**
- Consumes: Task 1 부품, `Modifier.pushlogShadow(token, shape)`.
- Produces:

```kotlin
@Composable fun PushlogContextualFloatingButton(
    text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    variant: PushlogContextualFloatingButtonVariant = PushlogContextualFloatingButtonVariant.SOLID,
    prefixIcon: Painter? = null, enabled: Boolean = true, loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
)
@Composable fun PushlogContextualFloatingButton(  // 항상 LAYER (스펙 §6.4)
    icon: Painter, contentDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
)
```

값 (`contextual-floating-button.yaml`): 모서리 `radius.full`, 그림자 `shadow.s3`, progress 16dp(`x4`).

| variant | 표면 / 눌림 | 글자·아이콘 | progress 트랙 |
|---|---|---|---|
| SOLID | `bg.neutralSolid` / `bg.neutralSolidPressed` | `fg.onNeutralSolid` | `gray700` |
| LAYER | `bg.layerFloating` / `bg.layerFloatingPressed` | `fg.neutral` | `gray500` |
| disabled | `bg.disabled` | `fg.disabled` | — |

글자 레이아웃: 최소 높이 `x9`, paddingX `x3Half`, paddingY `x2`, gap `x1`, 앞 아이콘 `x4`, 글자 `t4Medium` 한 줄 말줄임. 아이콘만: 44dp 원, 아이콘 22dp. loading은 Task 2와 같은 방식.

- [ ] **Step 1: 실패하는 테스트 작성**

```kotlin
@Test fun labelIsSingleLine()                 // 폭 120dp 안에 긴 라벨 → 텍스트 레이아웃 lineCount == 1 (onNodeWithText(...).fetchSemanticsNode()의 GetTextLayoutResult)
@Test fun iconOnlyTouchTargetAtLeast40dp()    // touchBoundsInRoot >= 40dp
@Test fun loadingBlocksClickKeepsName()       // stateDescription "불러오는 중", 클릭 카운트 0
```

- [ ] **Step 2: 실패 확인** — `./gradlew :core:designsystem:testDebugUnitTest --tests "*PushlogContextualFloatingButtonTest*"`, Expected: FAIL

- [ ] **Step 3: 구현 + `@PushlogPreview`**(그림자가 보이게 `bg.layerDefault` 위에 둔다), prevue로 확인

- [ ] **Step 4: 통과 확인** — Expected: PASS

- [ ] **Step 5: 스크린샷** `contextual_floating_button_states_{light,dark}`, `..._sizes_{light,dark}`(글자/앞 아이콘/아이콘만), `..._font2x_{light,dark}`. Expected: PASS, 6장

- [ ] **Step 6: 미리보기 앱 화면** (카탈로그 제목 `"PushlogContextualFloatingButton"`). `./gradlew :app-preview:testDebugUnitTest :konsist:test` PASS

- [ ] **Step 7: 커밋** — `git commit -m "feat: PushlogContextualFloatingButton 추가"`

---

### Task 5: `PushlogButton` 교체와 문서

**Files:**
- Delete: `core/designsystem/.../component/PushlogButton.kt`, `core/designsystem/src/test/.../PushlogButtonScreenshotTest.kt`, `app-preview/.../component/PushlogButtonPreviewKey.kt`, `PushlogButtonPreviewScreen.kt`
- Modify: `app-preview/.../navigation/PreviewEntryProvider.kt`, `app-preview/.../catalog/PreviewCatalog.kt`(PushlogButton 행 제거), `app-preview/.../scaffold/PreviewScaffold.kt:61`(미리보기 제목 `"PushlogActionButton"`)
- Modify: `feature/sample/src/main/kotlin/com/citytexi/pushlog/feature/sample/SampleScreen.kt:50-54`
- Modify: `docs/architecture/design-system.md`

**Interfaces:**
- Consumes: Task 2 `PushlogActionButton`.

- [ ] **Step 1: SampleScreen 교체.** `PushlogActionButton(text = stringResource(R.string.sample_refresh), onClick = { onIntent(SampleIntent.Refresh) }, loading = state.isRefreshing)`. variant는 기본값(`NEUTRAL_WEAK`)이 아니라 `BRAND_SOLID` — 화면의 유일한 주 동작이다.

- [ ] **Step 2: PushlogButton 관련 파일 삭제와 등록 제거.** `rg PushlogButton\b` 결과가 `docs/superpowers/{specs,plans}`의 과거 문서만 남는지 확인.

- [ ] **Step 3: 문서 갱신** (`docs/architecture/design-system.md`)
  - "컴포넌트를 추가할 때" 예시를 `PushlogActionButton`으로 바꾼다.
  - 새 절 "동작 컴포넌트": 공용 눌림 부품(`pushlogPressable`)을 새 컴포넌트가 재사용한다는 것, ripple을 쓰지 않는 이유, SEED px 값→토큰 매핑 규칙, 아이콘만 버튼이 같은 이름 오버로드인 이유, `PreviewSampleIcons`는 제품 아이콘이 아니라는 것. 결정 배경은 스펙 링크.
  - "M3 연결" 절의 `PushlogButton` 언급이 있으면 고친다.
  - `related_code` frontmatter에 세 컴포넌트를 더한다.

Run: `python3 docs/script/check_links.py docs`
Expected: 깨진 링크 0건

- [ ] **Step 4: 전체 검증**

Run: `./gradlew ktlintFormat && ./gradlew verifyStarter`
Expected: BUILD SUCCESSFUL. `sample_screen_refreshing*` 스크린샷이 바뀌는 것은 의도한 변화다(기준 스크린샷은 커밋하지 않는다).

- [ ] **Step 5: 커밋**

```bash
git add -A
git commit -m "refactor: PushlogButton을 PushlogActionButton으로 교체"
```
