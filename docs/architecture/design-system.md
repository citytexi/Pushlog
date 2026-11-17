---
id: design-system
title: 디자인 시스템 foundation 구조
category: architecture
status: living
platforms: android
verified: 2026-11-17
related_spec: [docs/superpowers/specs/2026-11-13-design-system-foundation-design.md]
related_adr:
related_architecture: [module-structure]
related_code: [PushlogTheme, PushlogColors, toColorScheme, PushlogActionButton, PushlogChip, PushlogContextualFloatingButton]
tags: [architecture, design-system]
---

# 디자인 시스템 foundation 구조

`core:designsystem`의 토큰과 `PushlogTheme`을 다룬다. 값 자체는 코드가 정답이다. 여기에는 코드로 복원할 수 없는 선택 이유만 적는다. 결정 배경은 [설계 스펙](../superpowers/specs/2026-11-13-design-system-foundation-design.md).

## 토큰 묶음과 접근

`PushlogTheme`이 여섯 묶음을 `CompositionLocal`로 제공한다. 화면은 `PushlogTheme.color`처럼 읽는다.

| 접근 | 타입 | 내용 |
|---|---|---|
| `PushlogTheme.color` | `PushlogColors` | 용도별 색 `bg`/`fg`/`stroke`. 눌림 상태 포함 |
| `PushlogTheme.typography` | `PushlogTypography` | `t1`~`t14` × regular/medium/bold, `screenTitle`/`articleBody`/`articleNote` |
| `PushlogTheme.dimension` | `PushlogDimension` | 간격 |
| `PushlogTheme.radius` | `PushlogRadius` | 모서리 |
| `PushlogTheme.shadow` | `PushlogShadows` | 그림자 |
| `PushlogTheme.motion` | `PushlogMotion` | 시간(ms)과 easing |

- 기본값은 라이트다. `PushlogTheme`으로 감싸지 않은 Preview·테스트도 라이트 값으로 그려진다.
- 컴포넌트·화면 미리보기는 `@PushlogPreview`(`core:ui`, 라이트·다크·글자 크기 200%)를 붙이고 본문을 `PushlogTheme { }`로 감싼다. 다크 변형은 uiMode로 바뀌고 `PushlogTheme`의 기본 `darkTheme = isSystemInDarkTheme()`가 따라간다.
- 파일은 타입마다 하나다(`FileStructureRulesTest`). 파일 이름이 타입 이름이고 라이트/다크/기본 인스턴스(`LightPushlogColors`, `DefaultPushlogDimension` 등)는 자기 타입 파일에 둔다. `theme/color`(`PushlogColors`, `PushlogBgColors`, `PushlogFgColors`, `PushlogStrokeColors`, `LightPalette`, `DarkPalette`), `theme/dimension`, `theme/motion`, `theme/shadow`(`PushlogShadowToken`, `PushlogShadows`, `PushlogShadowModifier.kt`의 `Modifier.pushlogShadow`)로 나뉜다.
- 팔레트(`LightPalette`, `DarkPalette`, 단계별 원색)는 `internal`이다. 화면이 원색을 직접 집으면 다크 전환과 용도 분리가 깨지므로 용도별 색만 공개한다.

## 이름 규칙

- SEED 이름을 camelCase로 옮긴다. `bg.brandSolid`, `fg.onBrandSolid`.
- SEED의 `_5`는 `Half`로 쓴다(`x0_5` → `x0Half`). ktlint property-naming이 밑줄을 막는다.

## SEED 기준

기준 커밋은 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`(기본 브랜치 `dev`)이다. 값이 이것과 어긋나는지는 `SeedMappingTest`가 일부 잡는다. SEED·Pretendard 라이선스 원문은 `core/designsystem/licenses/`에 있다(APK에는 들어가지 않는다. [미결](../synthesis/open-questions.md) 참고).

## 색

### forest 팔레트

- SEED에는 브랜드 팔레트가 없어서 Pushlog 고유색 `forest`를 만들었다. 색상(hue)은 약 144°로 고정하고 단계별로 밝기를 정했다(채도도 단계마다 다르다). 라이트 `forest-600`은 `#20874A`다.
- 단계 값은 스펙 §5.2의 대비 조건이 성립하도록 정했다. 기준 대비는 아래 순서의 네 쌍이다: 버튼 흰 글자, 눌린 버튼 흰 글자, 기본 배경 위 `fg.brand`, `bg.brandWeak` 위 `fg.brandContrast`. 라이트는 4.54 / 6.29 / 4.54 / 5.87, 다크는 4.83 / 7.57 / 6.07 / 5.05다. `BrandContrastTest`가 이 조건을 강제한다.

### 다크 예외 두 개

SEED 매핑과 다른 곳은 이 둘뿐이다.

| 토큰 | SEED 다크 | Pushlog 다크 | 버튼 흰 글자 대비 |
|---|---|---|---|
| `bg.brandSolid` | carrot-700 | `forest500` (`#218248`) | 4.83 (SEED 단계 따르면 forest700 `#39AA66` = 2.95) |
| `bg.brandSolidPressed` | carrot-800 | `forest400` (`#245F3B`) | 7.57 (SEED 단계 따르면 forest800 `#61CA8C` = 2.03) |

대가로 다크에서 버튼(`forest500`)과 배경(`bg.layerDefault`)의 대비는 약 3.7:1로 덜 튄다. UI 요소 기준 3:1은 넘는다.

다크에서는 숫자가 클수록 밝다. SEED는 눌림(800)이 기본(700)보다 밝다. Pushlog 다크는 반대로 눌림(`forest400`)이 기본(`forest500`)보다 어둡다.

이유: 초록 하나로 "버튼 위 흰 글씨"와 "어두운 배경 위 글씨"를 둘 다 만족시킬 수 없다. SEED의 다크 단계를 그대로 따르면 흰 글씨 대비가 4.5 밑(2.95, 2.03)으로 떨어진다. 그래서 솔리드를 SEED보다 두 단계 낮춘 `forest500`으로 잡았다.

## 동작 컴포넌트

`PushlogActionButton`, `PushlogChip`, `PushlogContextualFloatingButton`을 다룬다. 결정 배경은 [동작 컴포넌트 스펙](../superpowers/specs/2026-11-13-design-system-action-components-design.md), 선택 컨트롤은 [선택 컨트롤 스펙](../superpowers/specs/2026-11-16-design-system-selection-controls-design.md).

- 컴포넌트는 `component` 아래 종류별 하위 패키지에 둔다. `button`(액션 버튼, 플로팅 버튼, 둘이 함께 쓰는 `PushlogButtonSupport.kt`), `chip`, `loading`, `pressable`(눌림 부품, 모두 `internal`)이다. `component` 바로 아래에 두면 Konsist `ComponentRulesTest`가 막는다.
- 크기·variant에 따라 갈리는 값은 그 enum의 `internal` 멤버로 둔다. 크기는 `PushlogXxxSize.metrics()`가 `XxxMetrics`(같은 패키지, `Pushlog` 접두사 없이 값만 담는 `internal class`, 예: `PushlogChipSize` → `ChipMetrics`)를 돌려주고, 색은 `PushlogXxxVariant.surfaceColor(...)`·`contentColor(...)`처럼 variant가 답한다. 컴포넌트 파일에는 그리는 코드와 거기서만 쓰는 상수만 남긴다. 멤버는 `else`·`==` 없이 `when (this)`로 모든 항목을 적는다. 그래야 새 항목을 추가했을 때 컴파일러가 빠진 곳을 알려 준다.
- `XxxDefaults` object는 호출하는 쪽이 값을 바꿀 수 있게 파라미터를 열 때만 만든다(예: 나중에 `colors = PushlogChipDefaults.colors(...)`처럼 열 때). 컴포넌트 안에서만 쓰는 값은 파일 안 `private` 상수로 둔다. `Defaults`라는 이름은 바꿔도 되는 공개 값으로 읽히고, 공개하면 값 변경이 API 변경이 된다.
- 새 동작 컴포넌트는 표면을 새로 만들지 않고 `PushlogPressableSurface`(눌림 색·축소, 클릭 또는 토글, 테두리, 그림자, 로딩 의미 정보)에 내용을 채운다. 탭 영역과 축소 대상이 다른 컨트롤(체크박스·라디오 줄)은 `pressable`의 탭 영역 modifier와 눌림 표시 modifier를 같은 `interactionSource`로 묶는다. `pressable` 밖에서 `clickable`·`toggleable`·`selectable`·`indication`·`pointerInput`·ripple을 직접 쓰거나 Icon·Text·진행 표시 외의 Material 3 컴포넌트를 쓰면 `ComponentRulesTest`가 막는다. 눌림 모양과 로딩 규칙이 컴포넌트마다 갈라지지 않게 하려는 것이다. 버튼류는 `PushlogButtonSupport.kt`의 로딩 내용·색 도우미를 함께 쓴다. 크기와 접근성 이름은 `modifier`로 넘긴다.
- ripple을 쓰지 않는다. 눌림은 표면 색 교체와 2dp 축소로만 보인다. 축소 기준 길이는 max(높이, 너비÷4, 24dp)다. 글자·아이콘 색은 그대로다.
- 축소는 그림자까지 요소 전체를 줄인다(SEED `scaleScope: self`). 그래서 그림자는 `PushlogPressableSurface`의 `shadow` 인자로 넘겨 축소 층 안에 그린다. modifier에 따로 두면 표면만 줄고 그림자는 그대로 남는다. 탭 판정 영역은 줄이지 않는다. 판정 영역까지 줄면 가장자리를 누른 손가락이 줄어든 표면 밖으로 밀려나 탭이 취소된다(`edgeTapSurvivesShrink`).
- 로딩은 비활성으로 읽히지 않는다. 의미 정보에는 `enabled`가 남고 클릭만 삼킨다. 스크린 리더가 "사용 불가"로 안내하면 곧 다시 쓸 수 있는 버튼이 막힌 것처럼 들린다.
- 칩 높이는 최솟값이다. SEED는 `height`를 고정하지만 Pushlog는 최솟값(`heightIn(min)`)으로 둬서 글자가 잘리지 않고, 글자가 필요로 하면 칩이 커진다. 글자 크기 200%에서도 칩이 SEED 높이(36dp) 그대로인 것은 Compose가 큰 글자를 비선형으로 확대해 t4 한 줄이 36dp 안에 들어서다. 더 큰 배율에서는 커진다(`growsWithFontScale`이 fontScale 4로 확인한다).
- SEED의 px 값은 같은 크기의 토큰으로 옮긴다(14→`x3Half`, 16→`x4`, 32→`x8` 등). 토큰이 없는 22dp, 44dp, 1dp(칩 테두리)와 progress 애니메이션 값만 파일 안 `private` 상수로 두고 SEED 출처를 주석으로 단다. progress 두께 2dp는 `x0Half`를 읽는다. 눌림 축소량 2dp는 배율 계산 안의 숫자 상수다.
- 아이콘만 버튼은 같은 이름의 오버로드다. 한 이름에 미리보기 화면이 하나라서(`PreviewCoverageRulesTest`) 두 오버로드를 한 화면에 함께 보여 준다.
- `PreviewSampleIcons`(`core:ui`)는 미리보기·테스트 전용이다. 제품 아이콘 세트가 아니다.

## 선택 컨트롤

체크박스와 라디오를 다룬다. 스위치·세그먼트·select-box는 뒤따르는 PR이 같은 부품 위에 얹는다. 결정 배경은 [선택 컨트롤 스펙](../superpowers/specs/2026-11-16-design-system-selection-controls-design.md).

- 패키지는 `checkbox`(`PushlogCheckmark`, `PushlogCheckbox`, `PushlogCheckboxGroup`)와 `radio`(`PushlogRadiomark`, `PushlogRadio`, `PushlogRadioGroup`)다. 마크(`PushlogCheckmark`, `PushlogRadiomark`)는 공개하지만 탭을 처리하지 않는다. #22 리스트 줄이 마크를 조합해 쓰기 때문이다.
- 줄 전체가 탭 영역이고 마크만 줄어든다(SEED). 라벨을 눌러도 상태가 바뀐다. 탭 판정이 축소 층 바깥에 있어서 `edgeTapSurvivesShrink` 규칙이 그대로 지켜진다. 마크 20dp는 축소 기준 길이가 24dp라 22/24로 준다.
- 상태 3개(`ToggleableState`)와 부모의 일부 선택 계산은 호출하는 쪽이 한다. `checked: Boolean` 오버로드는 편의용이다.
- 기본값은 SEED recipe의 `defaultVariants`를 따르되 tone만 `NEUTRAL`이다(SEED 문서 권장, recipe는 brand).
- 체크와 대시는 SEED 체크박스가 쓰는 채운 아이콘(`IconCheckmarkFatFill`, `IconMinusFatFill`)의 경로를 `Canvas`로 그린다. 아이콘 자원과 의존을 늘리지 않으려는 것이다. 그리기 함수는 `CheckmarkIconDrawing.kt`에 둬서 뒤따르는 select-box 체크 마크가 같이 쓴다.
- 라벨이 여러 줄이면 마크는 첫 줄에 붙는다(SEED 위치 보정). 글자 200%에서 보정값이 음수가 되면 0으로 자른다.
- ghost "선택 + 눌림" 마크 배경은 SEED의 `carrot-200` 대신 brand는 `forest200`(라이트·다크 같은 단계), neutral은 `gray200`이다. 아이콘은 그대로 `fg.brand`다. 아이콘과 배경의 대비는 라이트 3.96(`fg.brand` `forest600` 대 `forest200`), 다크 4.36(`fg.brand` `forest700` 대 `forest200`)이다. 다크의 다른 단계는 `forest100` 5.05, `forest300` 3.43, `forest400` 2.56이라 `forest400`은 3:1에 못 미친다. 같은 단계를 쓰는 편이 SEED와 맞아 `forest200`으로 정했다. `CheckmarkContrastTest`가 라이트·다크 모두 3:1 이상을 강제한다.

### `pressable` 부품

체크박스·라디오가 쓰는 부품과, 단위 테스트로만 검증하는 부품(뒤따르는 PR의 대상)이 있다. 모두 `internal`이다.

| 부품 | 하는 일 | 쓰는 곳 |
|---|---|---|
| `pushlogTriStateToggleArea` | 3상태 토글 탭 영역, `Role.Checkbox` | `PushlogCheckbox` |
| `pushlogSelectableArea` | 단일 선택 탭 영역, `Role.RadioButton` | `PushlogRadio` |
| `pushlogToggleableArea` | 켬·끔 탭 영역, 역할을 인자로 받음 | 스위치 줄, 다중 선택 select-box(후속 PR) |
| `pushlogPressIndication` | 탭 없이 눌림 색과 축소만 보임. 축소는 끌 수 있음 | `PushlogCheckmark`, `PushlogRadiomark` |
| `pushlogPressScale` | 눌림 색 없이 축소만 보임 | 스위치 마크(후속 PR) |
| `PushlogPressableSurface`의 `scaleScope` | `SELF`는 표면 전체, `CONTENT`는 안의 내용만 줄임 | select-box 카드, 세그먼트 항목(후속 PR) |
| `PushlogPressableSurface`의 `selectionMode` | `TOGGLE`은 toggleable, `SINGLE`은 selectable(`Role.RadioButton`) | 세그먼트 항목, 단일 선택 select-box(후속 PR) |
| `PushlogPressableSurface`의 `pressedBorder` | 눌린 동안만 테두리를 그림 | 세그먼트 항목(후속 PR) |
| `pushlogToggleablePressable`의 `role` | 토글 역할. 기본값 `Role.Checkbox`로 칩 동작 유지 | 스위치(후속 PR) |

- 탭 영역 modifier는 시각 효과가 없고 `indication = null`이다. 눌림 표시 modifier는 탭을 받지 않는다. 둘이 같은 `interactionSource`를 봐서 줄을 누르면 마크가 눌린다.
- 내용 축소(`CONTENT`)의 배율은 안쪽 내용이 아니라 바깥 표면 크기로 잰다(SEED `scale-feedback.ts`: 비율은 측정한 요소 크기에서 나온다). 안쪽 상자가 표면의 최소 제약을 그대로 받아 표면과 크기가 같아지므로 `PressScaleNode`가 자기 크기로 재도 같은 값이 나온다.

## M3 연결

`toColorScheme()`(`MaterialMapping.kt`)은 M3 컴포넌트가 브랜드 색으로 보이게 하는 임시 다리다. Pushlog 컴포넌트(#19~#22)가 생기면 M3 의존과 함께 줄인다.

- `ColorScheme(...)` 주 생성자를 쓰고 모든 인자를 이름과 함께 채운다. `lightColorScheme()` 팩토리나 `copy`는 빠진 칸이 M3 기본 보라로 남는다.
- SEED에는 M3 Fixed 톤이 없다. Fixed 칸에는 테마별 토큰(`brandWeak`, `neutralWeak` 등)을 넣었다.
- 라이트 `surfaceContainer` 계단은 M3의 단조 순서가 아니라 SEED 의미를 따른다. `High`·`Low`에 `layerFloating`(흰색)을 쓴다.
- `surfaceTint`는 투명이다. 기본값(primary)이면 tonal elevation이 초록으로 물든다.
- M3 매핑 결과는 `LightMaterialColorScheme`, `DarkMaterialColorScheme`, `DefaultMaterialTypography`, `DefaultMaterialShapes`(`internal val`)로 한 번만 만든다. `PushlogTheme`은 고르기만 한다.
- `inversePrimary`는 용도 토큰에 짝이 없어 팔레트 단계를 직접 받는다. 아래 표 참고.
- 글자 15칸은 `toMaterialTypography()`, 모서리 5칸은 `toMaterialShapes()`가 채운다.

| M3 칸 | 토큰 |
|---|---|
| `primary` / `onPrimary` | `bg.brandSolid` / `fg.onBrandSolid` |
| `primaryContainer` / `onPrimaryContainer` | `bg.brandWeak` / `fg.brandContrast` |
| `inversePrimary` | 라이트 `LightPalette.forest500`(`#30C16A`, `inverseSurface` `#2A3038` 위 5.68:1), 다크 `DarkPalette.forest400`(`#245F3B`, `inverseSurface` `#F3F4F5` 위 6.87:1). Snackbar 액션이 브랜드 초록으로 읽히고 4.5를 넘는 단계다. 다크 `forest500`은 4.38이라 쓰지 않았다 |
| `secondary` / `onSecondary` | `bg.neutralSolid` / `fg.onNeutralSolid` |
| `secondaryContainer` / `onSecondaryContainer` | `bg.neutralWeak` / `fg.neutral` |
| `tertiary` / `onTertiary` | `bg.informativeSolid` / `fg.onInformativeSolid` |
| `tertiaryContainer` / `onTertiaryContainer` | `bg.informativeWeak` / `fg.informativeContrast` |
| `background`, `surface` | `bg.layerDefault` |
| `onBackground`, `onSurface` | `fg.neutral` |
| `surfaceVariant` / `onSurfaceVariant` | `bg.neutralWeak` / `fg.neutralMuted` |
| `surfaceTint` | `Color.Transparent` |
| `inverseSurface` / `inverseOnSurface` | `bg.neutralInverted` / `fg.neutralInverted` |
| `error` / `onError` | `bg.criticalSolid` / `fg.onCriticalSolid` |
| `errorContainer` / `onErrorContainer` | `bg.criticalWeak` / `fg.criticalContrast` |
| `outline` / `outlineVariant` | `stroke.neutralWeak` / `stroke.neutralMuted` |
| `scrim` | `bg.overlay` |
| `surfaceBright` / `surfaceDim` | `bg.layerFloating` / `bg.layerBasement` |
| `surfaceContainer` | `bg.neutralMuted` |
| `surfaceContainerHigh` / `Low` | `bg.layerFloating` |
| `surfaceContainerHighest` | `bg.neutralWeak` |
| `surfaceContainerLowest` | `bg.layerDefault` |
| `primaryFixed` / `primaryFixedDim` | `bg.brandWeak` / `bg.brandWeakPressed` |
| `onPrimaryFixed` / `onPrimaryFixedVariant` | `fg.brandContrast` / `fg.brand` |
| `secondaryFixed` / `secondaryFixedDim` | `bg.neutralWeak` / `bg.neutralWeakPressed` |
| `onSecondaryFixed` / `onSecondaryFixedVariant` | `fg.neutral` / `fg.neutralMuted` |
| `tertiaryFixed` / `tertiaryFixedDim` | `bg.informativeWeak` / `bg.informativeWeakPressed` |
| `onTertiaryFixed` / `onTertiaryFixedVariant` | `fg.informativeContrast` / `fg.informative` |

## 그림자

`Modifier.pushlogShadow(token, shape)`는 Compose `dropShadow`로 그린다. `BlurMaskFilter` 기반이라 SEED(CSS blur)보다 약간 퍼져 보이며, 이 차이는 허용했다.

## 글꼴

Pretendard 정적 OTF 400/500/700(orioncactus/pretendard v1.3.9)을 번들한다. Pretendard 한글 글리프는 굵기가 달라도 advance width가 같다. 그래서 굵기 테스트는 라틴 문자로 하고, 한글 굵기는 카탈로그 스크린샷으로 눈으로 확인한다.

```bash
./gradlew :app-preview:recordRoborazziDebug
```

결과는 `app-preview/build/outputs/roborazzi/catalog_*.png`다(`FoundationCatalogScreenshotTest`). 기준 스크린샷은 커밋하지 않는다.

## 프리뷰 스크린샷

`starter.android.screenshot`을 적용한 모듈은 namespace 아래 `@Preview`(private 포함)를 자동으로 캡처한다(Roborazzi Compose Preview 스캔). 결과는 `<module>/build/outputs/roborazzi/com.citytexi.pushlog.<...>.png`이고 UI diff PR 코멘트에서 비교된다. 언제 도는지는 [빌드 문서](build-and-release.md#검증)에 있다.

- 상태는 되도록 프리뷰로 표현한다. 직접 쓴 스크린샷 테스트는 프리뷰로 그릴 수 없는 상태(눌림, 애니메이션 진행 시점, 특수 화면 폭, `LaunchedEffect`로 띄우는 스낵바)에만 쓴다. 예: `SampleScreenScreenshotTest`(에러 스낵바), `FoundationCatalogScreenshotTest`(긴 화면, 412dp 폭).
- 선택 컨트롤의 마크는 눌림을 주입하는 `PushlogCheckmarkScreenshotTest`, `PushlogRadiomarkScreenshotTest`가 직접 찍는다. 크기·레이아웃과 2배 글자는 동작 컴포넌트와 같이 `app-preview` 섹션 프리뷰가 맡는다.
- 동작 컴포넌트(`PushlogActionButton`, `PushlogChip`, `PushlogContextualFloatingButton`)의 직접 쓴 스크린샷 테스트는 `states`(눌림 열 주입, 로딩 호 시계 진행)만 남겼다. 크기·레이아웃과 2배 글자는 `app-preview`에서 섹션마다 둔 `@PushlogPreview`(`PushlogActionButtonPreviews.kt`, 칩·플로팅 버튼은 `*PreviewScreen.kt` 아래쪽)가 Light/Dark/Light font 2x로 찍는다. 앱 화면용 `*PreviewContent`는 찍히지 않는다. 미리보기 캔버스(320x470)가 넘치는 부분을 잘라 내므로, 새 크기나 레이아웃을 추가하면 그 섹션의 `@PushlogPreview`를 하나 더 만든다.
- Robolectric에서 그릴 수 없는 프리뷰(Hilt·ViewModel)나 실행마다 달라지는 프리뷰에는 `@ScreenshotExcluded`(`core:ui`)를 붙인다.
- 프리뷰 이름은 파일명이 된다(공백은 `_`로 바뀐다). 글자·숫자·`_`·`-`·`.`·공백 외의 기호(`%`, 괄호, `+` 등)가 있으면 UI diff 스크립트가 경로 안전 검사(`report.py`의 `PART_RE`)에서 그 이미지를 빼고, 코멘트에 뺀 개수를 경고로 남긴다. `@PushlogPreview`의 200% 변형 이름이 `Light font 2x`인 이유다.
- 기본 tester(`AndroidComposePreviewTester`)를 쓴다. 파라미터 프리뷰를 쓰면 값 이름이 파일명에 들어가므로 이름 있는 값을 준다.

## Konsist 규칙

`DesignTokenRulesTest`가 `feature`와 `app`에서 세 가지를 막는다.

- `MaterialTheme` import
- `material3.MaterialTheme` 전체 이름 사용(import 없이 쓰는 우회)
- `Color(0x…)`, `Color(red = …)` 직접 생성

화면은 `PushlogTheme`의 토큰만 쓴다. 규칙이 막으면 토큰을 추가한다.

## 미리보기 앱

`:app-preview`는 디버그 전용 앱이다. 개발자가 실기기에서 토큰과 컴포넌트를 확인하고, 디자이너가 같은 화면을 검수하는 데 쓴다. release 변형은 꺼져 있다. CI는 APK를 올리지 않으니 직접 설치한다.

```bash
./gradlew :app-preview:installDebug
```

화면 아래쪽 조작 바(`PreviewControlBar`, `PreviewApp`이 모든 화면 아래에 둔다)에서 테마(라이트/다크/시스템)와 글자 크기(1×/1.3×/2×)를 바꾼다. 글자 크기는 시스템 설정과 무관한 절대 배율이다.

### 컴포넌트를 추가할 때

`core:designsystem`의 `component` 패키지(하위 패키지 포함)에 공개 composable을 추가하면 `app-preview`에 화면을 함께 만들어야 한다. 네 단계다. `PushlogActionButton`이 예시다.

1. 키: `component/PushlogActionButtonPreviewKey.kt`에 `NavKey` 구현체를 둔다.
2. 화면: `component/PushlogActionButtonPreviewScreen.kt`에 본문 `PushlogActionButtonPreviewContent`와 entry 함수 `pushlogActionButtonPreviewEntry(onBack)`를 둔다. entry는 `PreviewScaffold` 안에 본문을 그린다.
3. 등록: `navigation/PreviewEntryProvider.kt`의 `previewEntryProvider`에 entry 함수를 호출한다.
4. 목록: `catalog/PreviewCatalog.kt`의 `previewCatalog`에 `PreviewCatalogItem(카테고리, 제목, 키)` 행을 더한다.

이름은 `<컴포넌트 함수 이름>PreviewKey`, `<컴포넌트 함수 이름>PreviewScreen.kt`, `<함수 이름 첫 글자 소문자>PreviewEntry`로 짓는다. 파일 이름과 키 선언은 아래 규칙 1이 검사한다. 본문은 배경을 직접 그리지 않고 `PushlogTheme.*` 토큰만 쓴다.

### 누락 방지 테스트

세 가지가 빌드에서 실패한다.

| 잡는 것 | 테스트 |
|---|---|
| 1. 컴포넌트에 `<함수 이름>PreviewScreen.kt` 또는 `<함수 이름>PreviewKey` 선언이 없음 | Konsist `PreviewCoverageRulesTest`(`konsist`) |
| 2. `previewCatalog`의 키가 `previewEntryProvider`에 등록되지 않음 | `PreviewEntryProviderTest`(`app-preview` 단위 테스트) |
| 3. `NavKey` 구현체가 `previewCatalog`에서 참조되지 않음(고아 키) | Konsist `PreviewCoverageRulesTest` |

규칙 1의 대상은 `component` 패키지와 그 하위 패키지의 공개 최상위 `@Composable` 함수다. 컴포넌트의 보조 부품(슬롯, 하위 부품)은 `internal`로 둬서 화면을 요구받지 않게 한다.
