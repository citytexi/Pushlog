---
id: design-system
title: 디자인 시스템 foundation 구조
category: architecture
status: living
platforms: android
verified: 2026-11-13
related_spec: [docs/superpowers/specs/2026-11-13-design-system-foundation-design.md]
related_adr:
related_architecture: [module-structure]
related_code: [PushlogTheme, PushlogColors, toColorScheme]
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

`core:designsystem`의 `component` 패키지에 공개 composable을 추가하면 `app-preview`에 화면을 함께 만들어야 한다. 네 단계다. `PushlogButton`이 예시다.

1. 키: `component/PushlogButtonPreviewKey.kt`에 `NavKey` 구현체를 둔다.
2. 화면: `component/PushlogButtonPreviewScreen.kt`에 본문 `PushlogButtonPreviewContent`와 entry 함수 `pushlogButtonPreviewEntry(onBack)`를 둔다. entry는 `PreviewScaffold` 안에 본문을 그린다.
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

규칙 1의 대상은 `component` 패키지(하위 패키지 제외)의 공개 최상위 `@Composable` 함수다. 컴포넌트의 보조 부품(슬롯, 하위 부품)은 `internal`로 두거나 하위 패키지 `component.<name>`에 둬서 화면을 요구받지 않게 한다.
