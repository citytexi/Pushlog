# 디자인 시스템 foundation 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `core:designsystem`에 SEED 구조의 foundation 토큰(색, 글자, 간격, 모서리, 그림자, 모션)과 이를 제공하는 `PushlogTheme`을 만든다.

**Architecture:** 토큰 묶음은 `@Immutable data class`이고 `staticCompositionLocalOf`(기본값 라이트)로 제공한다. `object PushlogTheme`이 읽기 접근자를 연다. `PushlogTheme(darkTheme)` composable은 같은 값을 `MaterialTheme`에도 연결해 M3 컴포넌트가 브랜드 색과 Pretendard로 보이게 한다.

**Tech Stack:** Kotlin, Jetpack Compose(BOM `2026.08.00`, ui 1.12.0, material3 1.4.0), Robolectric + Roborazzi, Konsist, detekt 2.0.0-alpha.4, ktlint 14.2.0.

**Spec:** [`docs/superpowers/specs/2026-11-13-design-system-foundation-design.md`](../specs/2026-11-13-design-system-foundation-design.md). 이슈 #18(상위 #17).

## Global Constraints

- 코드 규약: [`docs/code-conventions.md`](../../code-conventions.md). KDoc은 의도 한 줄, 결정 설명은 `docs/architecture/design-system.md`에 두고 코드에는 포인터만.
- SEED 값 출처: `daangn/seed-design` 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`(기본 브랜치 `dev`), `packages/rootage/*.yaml`. 읽기: `gh api "repos/daangn/seed-design/contents/packages/rootage/<file>.yaml?ref=5f25880e93fe6f6eedba6f3842805cd8f6f352d1" --jq .content | base64 -d`. 값을 Kotlin으로 옮길 때 일회용 스크립트를 써도 되지만 저장소에 커밋하지 않는다.
- 값 단위: 1px = 1dp, 1rem = 16sp. 색은 `Color(0xAARRGGBB)`.
- 이름: SEED 토큰 이름을 camelCase로 옮긴다(`bg.brand-solid-pressed` → `bg.brandSolidPressed`). ktlint `property-naming`이 밑줄을 막으므로 `_5`는 `Half`로 쓴다: `x0_5` → `x0Half`, `x1_5` → `x1Half`, `r0_5` → `r0Half`(나머지 동일 규칙).
- 브랜드: `forest-600` 라이트 = `#20874A`. carrot은 옮기지 않는다. `manner-temp.*`, `banner.*`, `scale.*`, `-static` 글자 크기는 옮기지 않는다.
- detekt 설정, Konsist 기존 규칙, `ModuleRules`를 약화하지 않는다. `@Suppress`·baseline 금지. 기준 스크린샷은 커밋하지 않는다.
- 커밋 메시지 `type: 한국어 설명`, 끝에 `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- 작업 위치: 메인 체크아웃의 `feat/design-system-foundation` 브랜치(워크트리 없음).

## Review Focus

1. 시스템 글자 크기 200% — `sp` 글자와 줄 높이가 함께 커져 잘리지 않아야 한다. Task 6 카탈로그에 `fontScale = 2f` 캡처를 둔다.
2. 다크 눌린 브랜드 버튼 — 흰 글자 대비가 4.5 이상이어야 한다. Task 2 대비 테스트가 `bg.brandSolidPressed`를 검사한다.
3. `PushlogTheme`으로 감싸지 않은 Preview·테스트 — 크래시 없이 라이트 값이어야 한다. Task 5 `defaultsWithoutThemeAreLight` 테스트.
4. 우리 테마 아래의 M3 컴포넌트(Card, Snackbar, FilledTonalButton) — 보라 기본값이 나오면 안 된다. Task 5는 `lightColorScheme()` 대신 `ColorScheme` 생성자를 써서 모든 칸을 컴파일 단계에서 강제하고, 카탈로그(Task 6)에 이 셋을 그린다.
5. 한글 굵기 — Pretendard 400/500/700이 한글에서 실제로 다르게 렌더링돼야 한다. Task 1 폭 비교 테스트.

---

## 파일 구조

`core/designsystem/src/main/kotlin/com/citytexi/pushlog/core/designsystem/theme/` 아래:

| 파일 | 책임 |
|---|---|
| `color/PushlogPalette.kt` | `internal object LightPalette`, `DarkPalette`: SEED 팔레트 + `forest` 값 |
| `color/PushlogColors.kt` | `PushlogColors`, `PushlogBgColors`, `PushlogFgColors`, `PushlogStrokeColors` data class, `LightPushlogColors`, `DarkPushlogColors` |
| `typography/Pretendard.kt` | `internal val PretendardFontFamily` |
| `typography/PushlogTypography.kt` | `PushlogTypography` data class, `DefaultPushlogTypography` |
| `dimension/PushlogDimension.kt` | `PushlogDimension`, `PushlogSpacingX`, `PushlogSpacingY`, `PushlogRadius` |
| `shadow/PushlogShadow.kt` | `PushlogShadowToken`, `PushlogShadows`, `Modifier.pushlogShadow` |
| `motion/PushlogMotion.kt` | `PushlogMotion`, `PushlogDuration`, `PushlogTimingFunction` |
| `MaterialMapping.kt` | `internal fun PushlogColors.toColorScheme()`, `PushlogTypography.toMaterialTypography()`, `PushlogRadius.toMaterialShapes()` |
| `PushlogTheme.kt` | `CompositionLocal`들, `@Composable fun PushlogTheme`, `object PushlogTheme` |

삭제: `theme/Color.kt`, `theme/Type.kt`. 리소스: `src/main/res/font/pretendard_{regular,medium,bold}.otf`. 라이선스: `core/designsystem/licenses/`.

---

### Task 1: Pretendard 폰트와 라이선스

**Files:**
- Create: `core/designsystem/src/main/res/font/pretendard_regular.otf`, `pretendard_medium.otf`, `pretendard_bold.otf`
- Create: `core/designsystem/licenses/pretendard-OFL.txt`, `seed-design-LICENSE`, `seed-design-NOTICE`
- Create: `core/designsystem/src/main/kotlin/.../theme/typography/Pretendard.kt`
- Test: `core/designsystem/src/test/kotlin/.../theme/PretendardWeightTest.kt`

**Interfaces:**
- Produces: `internal val PretendardFontFamily: FontFamily` (400/500/700)

- [ ] **Step 1: 폰트·라이선스 내려받기**

```bash
B=https://raw.githubusercontent.com/orioncactus/pretendard/v1.3.9
R=core/designsystem/src/main/res/font
mkdir -p $R core/designsystem/licenses
curl -fsSL $B/packages/pretendard/dist/public/static/Pretendard-Regular.otf -o $R/pretendard_regular.otf
curl -fsSL $B/packages/pretendard/dist/public/static/Pretendard-Medium.otf -o $R/pretendard_medium.otf
curl -fsSL $B/packages/pretendard/dist/public/static/Pretendard-Bold.otf -o $R/pretendard_bold.otf
curl -fsSL $B/LICENSE -o core/designsystem/licenses/pretendard-OFL.txt
S=https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage
curl -fsSL $S/LICENSE -o core/designsystem/licenses/seed-design-LICENSE
curl -fsSL $S/NOTICE -o core/designsystem/licenses/seed-design-NOTICE
ls -l $R core/designsystem/licenses
```

Expected: OTF 세 개 각각 약 1.5MB, 라이선스 세 파일.

- [ ] **Step 2: 실패하는 테스트 작성** — Robolectric(`@GraphicsMode(NATIVE)`)에서 같은 한글 문자열 `"알림 보관함 가나다"`를 `TextMeasurer`로 400과 700으로 잰다.

```kotlin
@Test fun boldIsWiderThanRegular() {
    // measure(text, TextStyle(fontFamily = PretendardFontFamily, fontWeight = W400/W700, fontSize = 16.sp))
    assertTrue(boldWidth > regularWidth)
}
```

- [ ] **Step 3: 실패 확인** — Run: `./gradlew :core:designsystem:testDebugUnitTest --tests '*PretendardWeightTest*'` Expected: 컴파일 실패(`PretendardFontFamily` 없음).
- [ ] **Step 4: `Pretendard.kt` 구현** — `FontFamily(Font(R.font.pretendard_regular, FontWeight.Normal), Font(R.font.pretendard_medium, FontWeight.Medium), Font(R.font.pretendard_bold, FontWeight.Bold))`. KDoc 한 줄 + 라이선스 위치 포인터.
- [ ] **Step 5: 통과 확인** — 같은 명령, PASS.
- [ ] **Step 6: 커밋** — `feat: Pretendard 폰트와 라이선스 추가`

---

### Task 2: 팔레트와 용도별 색

**Files:**
- Create: `theme/color/PushlogPalette.kt`, `theme/color/PushlogColors.kt`
- Test: `core/designsystem/src/test/kotlin/.../theme/color/BrandContrastTest.kt`

**Interfaces:**
- Produces:
  - `internal object LightPalette` / `DarkPalette`: `val gray00 … gray1000`, `grayAlpha100 … grayAlpha1000`, `blue100 … blue1000`, `red…`, `green…`, `yellow…`, `purple…`, `forest100 … forest1000`, `staticWhite`, `staticBlack`, `staticWhiteAlpha50 … staticWhiteAlpha1000`, `staticBlackAlpha100 … staticBlackAlpha1000`: `Color`
  - `@Immutable data class PushlogColors(val bg: PushlogBgColors, val fg: PushlogFgColors, val stroke: PushlogStrokeColors)`
  - `PushlogBgColors` / `PushlogFgColors` / `PushlogStrokeColors`: SEED `color.yaml`의 `$color.bg.*`(45), `$color.fg.*`(22), `$color.stroke.*`(16) 전부를 camelCase `Color` 프로퍼티로
  - `internal val LightPushlogColors: PushlogColors`, `internal val DarkPushlogColors: PushlogColors`

- [ ] **Step 1: 실패하는 테스트 작성** — WCAG 상대 휘도를 테스트 안에 직접 구현(`fun contrast(a: Color, b: Color): Double`, sRGB 채널 `c <= 0.03928 ? c/12.92 : ((c+0.055)/1.055)^2.4`, 알파는 1로 본다).

```kotlin
@Test fun light() = assertBrandContrast(LightPushlogColors)
@Test fun dark() = assertBrandContrast(DarkPushlogColors)
@Test fun lightBrandSolidIsExactBrand() = assertEquals(Color(0xFF20874A), LightPushlogColors.bg.brandSolid)

private fun assertBrandContrast(c: PushlogColors) {
    assertAtLeast(4.5, c.fg.onBrandSolid, c.bg.brandSolid)
    assertAtLeast(4.5, c.fg.onBrandSolid, c.bg.brandSolidPressed)
    assertAtLeast(4.5, c.fg.brand, c.bg.layerDefault)
    assertAtLeast(4.5, c.fg.brandContrast, c.bg.brandWeak)
    assertAtLeast(4.5, c.fg.neutral, c.bg.layerDefault)
    assertAtLeast(3.0, c.stroke.brandSolid, c.bg.layerDefault)
}
```

- [ ] **Step 2: 실패 확인** — `./gradlew :core:designsystem:testDebugUnitTest --tests '*BrandContrastTest*'` Expected: 컴파일 실패.
- [ ] **Step 3: `PushlogPalette.kt` 구현** — SEED `color.yaml`의 `$color.palette.*`(carrot 제외)를 라이트/다크 값 그대로. `forest`는 아래 값(색상 144° 고정, 대비 조건을 만족하도록 계산한 값):

| 단계 | 라이트 | 다크 |
|---|---|---|
| 100 | `#EEFAF3` | `#1A2B20` |
| 200 | `#DEF5E7` | `#1E3728` |
| 300 | `#B7EACB` | `#234933` |
| 400 | `#85DCA7` | `#245F3B` |
| 500 | `#30C16A` | `#218248` |
| 600 | `#20874A` | `#2B9556` |
| 700 | `#1A6E3C` | `#39AA66` |
| 800 | `#15542E` | `#61CA8C` |
| 900 | `#123D23` | `#A0E4BB` |
| 1000 | `#0D2717` | `#D9F6E5` |

- [ ] **Step 4: `PushlogColors.kt` 구현** — SEED 연결을 그대로 옮기되 `carrot-N` → `forest-N`. 다크 예외 두 개만:

| 토큰 | 라이트 | 다크 | SEED 다크 |
|---|---|---|---|
| `bg.brandSolid` | forest600 | **forest500** | carrot-700 |
| `bg.brandSolidPressed` | forest700 | **forest400** | carrot-800 |
| `bg.brandWeak` / `Pressed` | forest100 / 200 | forest100 / 200 | 같음 |
| `fg.brand`, `fg.brandContrast` | forest600, forest700 | forest700, forest700 | 같음 |
| `stroke.brandSolid` / `brandWeak` | forest700 / 300 | forest700 / 300 | 같음 |
| `fg.onBrandSolid` | staticWhite | staticWhite | 같음 |

리터럴 값 토큰(`bg.magicWeak`, `bg.transparent` 등)도 SEED 값 그대로. 예상 대비: 라이트 4.54 / 6.29 / 4.54 / 5.87, 다크 4.83 / 7.57 / 6.07 / 5.05. KDoc에 SEED 커밋과 "다크 예외는 design-system.md" 포인터.

- [ ] **Step 5: 통과 확인** — 같은 명령, PASS.
- [ ] **Step 6: 커밋** — `feat: 디자인 시스템 팔레트와 용도별 색 토큰 추가`

---

### Task 3: 글자 토큰

**Files:**
- Create: `theme/typography/PushlogTypography.kt`
- Test: `core/designsystem/src/test/kotlin/.../theme/typography/PushlogTypographyTest.kt`

**Interfaces:**
- Consumes: `PretendardFontFamily` (Task 1)
- Produces: `@Immutable data class PushlogTypography(val screenTitle: TextStyle, val articleBody: TextStyle, val articleNote: TextStyle, val t1Regular … val t14Bold: TextStyle)`(45개), `internal val DefaultPushlogTypography`

- [ ] **Step 1: 실패하는 테스트 작성**

```kotlin
@Test fun t5Bold() = assertStyle(DefaultPushlogTypography.t5Bold, 16.sp, 22.sp, FontWeight.W700)
@Test fun t14Regular() = assertStyle(DefaultPushlogTypography.t14Regular, 48.sp, 60.sp, FontWeight.W400)
@Test fun screenTitle() = assertStyle(DefaultPushlogTypography.screenTitle, 26.sp, 35.sp, FontWeight.W700)
@Test fun articleBody() = assertStyle(DefaultPushlogTypography.articleBody, 16.sp, 24.sp, FontWeight.W400)
@Test fun articleNote() = assertStyle(DefaultPushlogTypography.articleNote, 14.sp, 22.sp, FontWeight.W400)
@Test fun allUsePretendard() // 45개 모두 fontFamily == PretendardFontFamily (data class 프로퍼티를 리플렉션 없이 목록으로 나열)
```

- [ ] **Step 2: 실패 확인** — `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogTypographyTest*'` Expected: 컴파일 실패.
- [ ] **Step 3: 구현** — 크기(sp) t1..t14 = 11, 12, 13, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 48. 줄 높이(sp) t1..t14 = 15, 16, 18, 19, 22, 24, 27, 30, 32, 35, 38, 42, 52, 60. 굵기 regular 400 / medium 500 / bold 700. `screenTitle` = t10 크기·t10 줄 높이·bold, `articleBody` = t5 크기·t6 줄 높이·regular, `articleNote` = t4 크기·t5 줄 높이·regular. 모든 스타일: `platformStyle = PlatformTextStyle(includeFontPadding = false)`, `lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None)`. 반복은 private 헬퍼 `style(size: Int, lineHeight: Int, weight: FontWeight)`로.
- [ ] **Step 4: 통과 확인** — PASS.
- [ ] **Step 5: 커밋** — `feat: 디자인 시스템 글자 토큰 추가`

---

### Task 4: 간격·모서리·그림자·모션 토큰

**Files:**
- Create: `theme/dimension/PushlogDimension.kt`, `theme/shadow/PushlogShadow.kt`, `theme/motion/PushlogMotion.kt`
- Test: `core/designsystem/src/test/kotlin/.../theme/FoundationScaleTest.kt`

**Interfaces:**
- Produces:
  - `@Immutable data class PushlogDimension(val x0Half: Dp, val x1: Dp, val x1Half: Dp, val x2: Dp, val x2Half: Dp, val x3: Dp, val x3Half: Dp, val x4: Dp, val x4Half: Dp, val x5: Dp, val x6: Dp, val x7: Dp, val x8: Dp, val x9: Dp, val x10: Dp, val x12: Dp, val x13: Dp, val x14: Dp, val x16: Dp, val spacingX: PushlogSpacingX, val spacingY: PushlogSpacingY)`, `internal val DefaultPushlogDimension`
  - `PushlogSpacingX(globalGutter, betweenChips)`, `PushlogSpacingY(betweenText, componentDefault, navToTitle, screenBottom)`: `Dp`
  - `@Immutable data class PushlogRadius(val r0Half … val r6: Dp, val full: Shape)`, `internal val DefaultPushlogRadius`
  - `@Immutable data class PushlogShadowToken(val offsetX: Dp, val offsetY: Dp, val blur: Dp, val spread: Dp, val color: Color)`, `@Immutable data class PushlogShadows(val s1: PushlogShadowToken, val s2: …, val s3: …)`, `internal val LightPushlogShadows`, `DarkPushlogShadows`
  - `fun Modifier.pushlogShadow(token: PushlogShadowToken, shape: Shape): Modifier`
  - `@Immutable data class PushlogMotion(val duration: PushlogDuration, val timingFunction: PushlogTimingFunction)`, `PushlogDuration(d1…d6, colorTransition, pressedScale: Int)`, `PushlogTimingFunction(linear, easing, enter, exit, enterExpressive, exitExpressive, pressedScale: Easing)`, `internal val DefaultPushlogMotion`

- [ ] **Step 1: 실패하는 테스트 작성**

```kotlin
@Test fun dimension() { assertEquals(2.dp, DefaultPushlogDimension.x0Half); assertEquals(64.dp, DefaultPushlogDimension.x16) }
@Test fun semanticSpacingFollowsSeed() // SEED dimension.yaml의 spacing-x/y 연결값과 같은지 (globalGutter == x4 등, 값은 yaml에서)
@Test fun radius() // r0Half, r6 값 = SEED radius.yaml
@Test fun shadowS1() { assertEquals(1.dp, LightPushlogShadows.s1.offsetY); assertEquals(4.dp, LightPushlogShadows.s1.blur); assertEquals(Color(0x14000000), LightPushlogShadows.s1.color); assertEquals(Color(0x80000000), DarkPushlogShadows.s1.color) }
@Test fun timingFunctionEnter() = assertEquals(CubicBezierEasing(0f, 0f, 0.15f, 1f), DefaultPushlogMotion.timingFunction.enter)
@Test fun durationD3() // SEED duration.yaml 값
```

- [ ] **Step 2: 실패 확인** — `./gradlew :core:designsystem:testDebugUnitTest --tests '*FoundationScaleTest*'` Expected: 컴파일 실패.
- [ ] **Step 3: 구현** — 값은 SEED `dimension.yaml`, `radius.yaml`, `shadow.yaml`, `duration.yaml`, `timing-function.yaml`. `full`은 `RoundedCornerShape(percent = 50)`. `pushlogShadow`는 `Modifier.dropShadow(shape, Shadow(radius = token.blur, spread = token.spread, offset = DpOffset(token.offsetX, token.offsetY), color = token.color))`(`androidx.compose.ui.draw.dropShadow`, `androidx.compose.ui.graphics.shadow.Shadow`).
- [ ] **Step 4: 통과 확인** — PASS.
- [ ] **Step 5: 커밋** — `feat: 간격·모서리·그림자·모션 토큰 추가`

---

### Task 5: `PushlogTheme`과 Material3 연결

**Files:**
- Create: `theme/MaterialMapping.kt`
- Modify: `theme/PushlogTheme.kt` (전체 교체)
- Delete: `theme/Color.kt`, `theme/Type.kt`
- Test: `core/designsystem/src/test/kotlin/.../theme/PushlogThemeTest.kt`

**Interfaces:**
- Consumes: Task 1~4의 모든 `Default*`/`Light*`/`Dark*` 값
- Produces:
  - `@Composable fun PushlogTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit)` (기존 시그니처 유지)
  - `object PushlogTheme { val color: PushlogColors; val typography: PushlogTypography; val dimension: PushlogDimension; val radius: PushlogRadius; val shadow: PushlogShadows; val motion: PushlogMotion }` — 각 `@Composable @ReadOnlyComposable get() = Local….current`
  - `internal val LocalPushlogColors = staticCompositionLocalOf { LightPushlogColors }` 외 묶음마다 하나(기본값 라이트)

- [ ] **Step 1: 실패하는 테스트 작성** (Robolectric, `createComposeRule`)

```kotlin
@Test fun lightMapsBrandIntoMaterial()  // PushlogTheme(false) 안에서: MaterialTheme.colorScheme.primary == Color(0xFF20874A), onPrimary == White, surfaceTint == Color.Transparent, onPrimaryContainer == PushlogTheme.color.fg.brandContrast
@Test fun darkSelectsDarkTokens()       // PushlogTheme(true): PushlogTheme.color == DarkPushlogColors, colorScheme.primary == DarkPushlogColors.bg.brandSolid
@Test fun outlineUsesNeutralWeak()      // colorScheme.outline == PushlogTheme.color.stroke.neutralWeak
@Test fun materialTypographyIsPretendard() // MaterialTheme.typography의 15칸 모두 fontFamily == PretendardFontFamily
@Test fun defaultsWithoutThemeAreLight()   // PushlogTheme 없이 setContent: PushlogTheme.color == LightPushlogColors, 예외 없음
```

- [ ] **Step 2: 실패 확인** — `./gradlew :core:designsystem:testDebugUnitTest --tests '*PushlogThemeTest*'` Expected: 컴파일 실패.
- [ ] **Step 3: `MaterialMapping.kt` 구현** — `internal fun PushlogColors.toColorScheme(): ColorScheme`는 `lightColorScheme()`/`darkColorScheme()`가 아니라 **`ColorScheme(...)` 생성자**를 모든 인자 이름과 함께 호출한다(빠진 칸은 컴파일 오류). 스펙 §5.6 표의 연결 + `surfaceTint = Color.Transparent` + 나머지 칸은 가장 가까운 토큰(빈칸 없음). `toMaterialTypography()`: display L/M/S = t14/t13/t12 Bold, headline L/M/S = t11/t10/t9 Bold, title L/M/S = t7 Bold / t5 Bold / t4 Bold, body L/M/S = t5/t4/t3 Regular, label L/M/S = t4/t3/t2 Medium. `toMaterialShapes()`: extraSmall r1, small r2, medium r3, large r4, extraLarge r6(SEED radius 값).
- [ ] **Step 4: `PushlogTheme.kt` 구현** — `CompositionLocalProvider`로 6개 묶음을 제공하고 그 안에서 `MaterialTheme(colorScheme, typography, shapes, content)`. `Color.kt`, `Type.kt` 삭제.
- [ ] **Step 5: 통과 확인** — PASS. 이어서 `./gradlew :core:designsystem:testDebugUnitTest` 전체 PASS(기존 `PushlogButtonScreenshotTest` 포함).
- [ ] **Step 6: 커밋** — `feat: PushlogTheme이 foundation 토큰을 제공하고 Material3에 연결`

---

### Task 6: 카탈로그 스크린샷

**Files:**
- Create: `core/designsystem/src/test/kotlin/.../catalog/FoundationCatalogScreenshotTest.kt`

**Interfaces:**
- Consumes: `PushlogTheme` 전체(Task 5)

- [ ] **Step 1: 테스트 작성** — `PushlogButtonScreenshotTest` 패턴(Robolectric `NATIVE`, `captureRoboImage("build/outputs/roborazzi/<name>.png")`). 라이트/다크 각각:
  - `catalog_color_{light,dark}`: `bg`/`fg`/`stroke` 견본(이름 + 색 칸)
  - `catalog_typography_{light,dark}`: 45개 스타일을 `"알림 보관함 Aa 123"`으로
  - `catalog_typography_fontscale2_light`: `LocalDensity`를 `Density(density, fontScale = 2f)`로 덮고 t1~t14 Regular
  - `catalog_shape_{light,dark}`: 간격 막대, 모서리 상자, 그림자 s1~s3 카드
  - `catalog_material_{light,dark}`: M3 `Button`, `FilledTonalButton`, `OutlinedButton`, `Card`, `Snackbar`, `OutlinedTextField`
- [ ] **Step 2: 실행** — `./gradlew :core:designsystem:recordRoborazziDebug` Expected: BUILD SUCCESSFUL, `core/designsystem/build/outputs/roborazzi/catalog_*.png` 11개.
- [ ] **Step 3: 눈으로 확인** — `catalog_material_*`에 보라색이 없고, 다크 버튼이 짙은 초록 + 흰 글자이며, 한글 굵기가 세 단계로 구분되는지 연다. 이미지는 커밋하지 않는다.
- [ ] **Step 4: 커밋** — `test: foundation 카탈로그 스크린샷 추가`

---

### Task 7: Konsist 규칙과 기존 화면 이전

**Files:**
- Create: `konsist/src/test/kotlin/com/citytexi/pushlog/konsist/DesignTokenRulesTest.kt`
- Modify: `feature/sample/src/main/kotlin/com/citytexi/pushlog/feature/sample/SampleScreen.kt:48,88,95`

**Interfaces:**
- Consumes: `productionFiles(layer)`, `isInPackage` (`ArchitectureScope.kt`), `PushlogTheme.typography` (Task 5)

- [ ] **Step 1: 실패하는 테스트 작성** — 대상 `productionFiles("feature") + productionFiles("app")`, `assertTrue(files.isNotEmpty(), …)` 먼저.

```kotlin
@Test fun `feature and app do not import MaterialTheme`()   // import 이름 == "androidx.compose.material3.MaterialTheme" (별칭 포함)
@Test fun `feature and app do not create raw colors`()     // file.text에 Regex("""\bColor\(\s*(0x|red\s*=)""") 없음
```

- [ ] **Step 2: 실패 확인** — `./gradlew :konsist:test --tests '*DesignTokenRulesTest*'` Expected: FAIL, `SampleScreen.kt`의 `MaterialTheme` import 보고.
- [ ] **Step 3: `SampleScreen.kt` 이전** — `MaterialTheme.typography.headlineSmall` → `PushlogTheme.typography.t9Bold`, `bodyMedium` → `t4Regular`(두 곳). `MaterialTheme` import 제거. `app`에서 같은 사용이 보고되면 같이 바꾼다.
- [ ] **Step 4: 통과 확인** — `./gradlew :konsist:test` 전체 PASS.
- [ ] **Step 5: 커밋** — `feat: 화면이 디자인 토큰을 거치도록 Konsist 규칙 추가`

---

### Task 8: 문서

**Files:**
- Create: `docs/architecture/design-system.md`
- Modify: `docs/index.md`(작업별 표), `AGENTS.md`(문서 라우팅 표), `docs/synthesis/open-questions.md`, `docs/superpowers/specs/2026-11-13-design-system-foundation-design.md`(상태 줄)

- [ ] **Step 1: `design-system.md` 작성** — 프론트매터는 `build-and-release.md`와 같은 형식(`id: design-system`, `category: architecture`, `status: living`, `related_spec`에 이 스펙). 내용(코드로 복원할 수 없는 것만): 토큰 묶음 구조와 접근 방법, 이름 규칙(camelCase, `_5` → `Half`), 팔레트 `internal` 이유, `forest` 단계 값을 정한 방법(144° 고정, 대비 조건)과 다크 예외 두 개의 이유, M3 연결은 컴포넌트 전까지의 임시 다리이고 `ColorScheme` 생성자를 쓰는 이유, SEED 기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`, Konsist 규칙.
- [ ] **Step 2: 라우팅 추가** — `AGENTS.md`와 `docs/index.md` 표에 `| 디자인 시스템 토큰·컴포넌트 | docs/architecture/design-system.md |` 행.
- [ ] **Step 3: 미결 추가** — `open-questions.md` 미해결에 `### [2026-11-13] 앱 안 오픈소스 라이선스 고지` (Pretendard OFL, SEED Apache-2.0 §4(d) NOTICE가 APK에 들어가야 함. `core/designsystem/licenses/`는 APK에 안 들어감).
- [ ] **Step 4: 스펙 상태** — "상태: 승인됨 (2026-11-13)".
- [ ] **Step 5: 검사** — `python3 docs/script/check_links.py docs` Expected: 깨진 링크 0건.
- [ ] **Step 6: 커밋** — `docs: 디자인 시스템 foundation 구조 문서 추가`

---

### Task 9: 브랜드 색 결정 위키 기록

**Files:** `wiki/` (`wiki-ingest` skill이 정하는 경로)

- [ ] **Step 1:** `wiki-ingest` skill을 따른다. 원본은 이번 결정: 초록 = 안정감·"알림이 안전하게 보관된다", 경쟁 앱 아이콘 색(NotiFold 파랑+민트, Notisave 보라~파랑, Notistory 분홍·코랄)과 겹치지 않음, 후보 비교(A `#21884A` 4.48 → `#20874A` 4.54, B `#1B7A43`, C `#17694A`, 연두 `#a3e635`), 다크 모드 A안(짙은 초록 + 흰 글자) 선택 이유.
- [ ] **Step 2:** `python3 wiki/script/lint.py` Expected: 0건.
- [ ] **Step 3: 커밋** — `docs: 브랜드 색 결정을 위키에 기록`

---

### Task 10: 최종 검증과 PR

- [ ] **Step 1:** `./gradlew ktlintFormat` 후 `./gradlew verifyStarter` Expected: BUILD SUCCESSFUL.
- [ ] **Step 2:** `git-flow` skill Work flow 4~5단계: `git push -u origin feat/design-system-foundation`, `gh pr create --base develop`, 제목 `feat: add design system foundation tokens and theme`, 본문에 `Closes #18`. CI 통과 후 merge, #18 닫고 #17 체크리스트 갱신.

---

### Task 11: 파일당 타입 하나로 나누기와 최종 리뷰 수정

develop에 #25(`FileStructureRulesTest`: production kt 파일은 최상위 class/interface/object/enum/annotation 최대 1개, 예외 없음)가 들어갔다. 이 브랜치는 그 위로 rebase했고, 이 작업에서 자기 파일을 규칙에 맞춘다. 최종 리뷰 지적(I1, M1~M6)도 함께 고친다. Task 10보다 먼저 한다.

**Files:**
- Split (파일 이름 = 타입 이름, 패키지·가시성·API 변경 없음, Light/Dark/Default 인스턴스는 자기 타입 파일에 둔다):
  - `theme/color/PushlogColors.kt` → `PushlogColors.kt`(+`LightPushlogColors`, `DarkPushlogColors`), `PushlogBgColors.kt`, `PushlogFgColors.kt`, `PushlogStrokeColors.kt`
  - `theme/color/PushlogPalette.kt` → `LightPalette.kt`, `DarkPalette.kt`
  - `theme/dimension/PushlogDimension.kt` → `PushlogDimension.kt`(+Default), `PushlogSpacingX.kt`, `PushlogSpacingY.kt`, `PushlogRadius.kt`(+Default)
  - `theme/motion/PushlogMotion.kt` → `PushlogMotion.kt`(+Default), `PushlogDuration.kt`, `PushlogTimingFunction.kt`
  - `theme/shadow/PushlogShadow.kt` → `PushlogShadowToken.kt`, `PushlogShadows.kt`(+Light/Dark), `PushlogShadowModifier.kt`(`Modifier.pushlogShadow`)
- Modify: `app/src/main/res/values/colors.xml`, `app/src/main/res/values-night/colors.xml`, `theme/MaterialMapping.kt`, `theme/PushlogTheme.kt`, `feature/sample/.../SampleScreen.kt`, `konsist/.../DesignTokenRulesTest.kt`, catalog test, `BrandContrastTest.kt`, `docs/architecture/design-system.md`, 스펙 §4, `wiki/pages/concepts/brand-color.md`

- [ ] **Step 1: 규칙 실패 확인** — `./gradlew :konsist:test --tests '*FileStructureRulesTest*'` Expected: FAIL, 위 5개 파일.
- [ ] **Step 2: 나누기** — 위 목록대로. `./gradlew :konsist:test :core:designsystem:testDebugUnitTest` PASS. 커밋 `refactor: 디자인 시스템 토큰 파일을 타입마다 나눔`.
- [ ] **Step 3: I1 스플래시** — `splash_background`를 라이트 `#FFFFFF`, 다크 `#16171B`(`bg.layerDefault`)로. 런처·스플래시 아이콘 색은 그대로 두고, 디자인 시스템과 같다고 주장하는 주석은 사실대로 고친다(아이콘 교체 전까지 남긴 옛 색).
- [ ] **Step 4: M1 Snackbar 액션 색** — 테스트 먼저: `BrandContrastTest`에 `MaterialTheme` 매핑의 `inversePrimary` on `inverseSurface` ≥ 4.5(라이트·다크) 추가 → 지금 값으로 실패 확인. 브랜드로 읽히는 forest 단계로 바꾼다(팔레트는 모듈 internal이라 `MaterialMapping`에서 직접 써도 된다). 카탈로그 Snackbar에 `action = { TextButton … }` 추가.
- [ ] **Step 5: M2** — `toColorScheme()`/`toMaterialTypography()`/`toMaterialShapes()` 결과를 `internal val`(라이트/다크 ColorScheme, Typography, Shapes)로 미리 만들고 `PushlogTheme`은 고르기만 한다.
- [ ] **Step 6: M4 Konsist** — `DesignTokenRulesTest`에 파일 본문 텍스트 검사 `material3.MaterialTheme` 추가(전체 이름 사용 차단). 기존 테스트 이름·검사는 유지.
- [ ] **Step 7: M5** — `SampleScreen`의 `16.dp`/`12.dp`를 `PushlogTheme.dimension.x4`/`x3`로.
- [ ] **Step 8: M6** — `Modifier.pushlogShadow`에 KDoc 한 줄 + `design-system.md` 포인터(CSS blur보다 약간 퍼지는 점).
- [ ] **Step 9: M3 문서** — 다크 버튼과 배경 대비 "약 4:1" → "약 3.7:1"(스펙 §4, `design-system.md`, 위키). 위키 `brand-color.md`의 "기준(4.5)을 넘는 후보 중 가장 밝고"를 "차분하면서 가장 밝고"로. `design-system.md`의 파일 경로·`inversePrimary` 연결 갱신. `python3 docs/script/check_links.py docs`, `python3 wiki/script/lint.py` 0건.
- [ ] **Step 10:** `./gradlew ktlintFormat` 후 `./gradlew verifyStarter` PASS. 커밋은 단계 성격별(`fix:`, `refactor:`, `docs:`).
