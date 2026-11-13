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
- 팔레트(`LightPalette`, `DarkPalette`, 단계별 원색)는 `internal`이다. 화면이 원색을 직접 집으면 다크 전환과 용도 분리가 깨지므로 용도별 색만 공개한다.

## 이름 규칙

- SEED 이름을 camelCase로 옮긴다. `bg.brandSolid`, `fg.onBrandSolid`.
- SEED의 `_5`는 `Half`로 쓴다(`x0_5` → `x0Half`). ktlint property-naming이 밑줄을 막는다.

## SEED 기준

기준 커밋은 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`(기본 브랜치 `dev`)이다. 값이 이것과 어긋나는지는 `SeedMappingTest`가 일부 잡는다. SEED·Pretendard 라이선스 원문은 `core/designsystem/licenses/`에 있다(APK에는 들어가지 않는다. [미결](../synthesis/open-questions.md) 참고).

## 색

### forest 팔레트

- SEED에는 브랜드 팔레트가 없어서 Pushlog 고유색 `forest`를 만들었다. 색상(hue)은 약 144°로 고정하고 밝기만 단계별로 바꿨다. 라이트 `forest-600`은 `#20874A`다.
- 단계 값은 스펙 §5.2의 대비 조건이 성립하도록 정했다. 기준 대비는 라이트 4.54 / 6.29 / 4.54 / 5.87, 다크 4.83 / 7.57 / 6.07 / 5.05다. `BrandContrastTest`가 이 조건을 강제한다.

### 다크 예외 두 개

SEED 매핑과 다른 곳은 이 둘뿐이다.

| 토큰 | SEED 다크 | Pushlog 다크 |
|---|---|---|
| `bg.brandSolid` | 한 단계 위 | `forest500` |
| `bg.brandSolidPressed` | 한 단계 더 밝음 | `forest400` |

이유: 초록 하나로 "버튼 위 흰 글씨"와 "어두운 배경 위 글씨"를 둘 다 만족시킬 수 없다. SEED 다크는 눌림을 한 단계 밝게 하는데, 그대로 두면 흰 글씨 대비가 4.5 밑으로 떨어진다.

## M3 연결

`toColorScheme()`(`MaterialMapping.kt`)은 M3 컴포넌트가 브랜드 색으로 보이게 하는 임시 다리다. Pushlog 컴포넌트(#19~#22)가 생기면 M3 의존과 함께 줄인다.

- `ColorScheme(...)` 주 생성자를 쓰고 모든 인자를 이름과 함께 채운다. `lightColorScheme()` 팩토리나 `copy`는 빠진 칸이 M3 기본 보라로 남는다.
- SEED에는 M3 Fixed 톤이 없다. Fixed 칸에는 테마별 토큰(`brandWeak`, `neutralWeak` 등)을 넣었다.
- 라이트 `surfaceContainer` 계단은 M3의 단조 순서가 아니라 SEED 의미를 따른다. `High`·`Low`에 `layerFloating`(흰색)을 쓴다.
- `surfaceTint`는 투명이다. 기본값(primary)이면 tonal elevation이 초록으로 물든다.
- 글자 15칸은 `toMaterialTypography()`, 모서리 5칸은 `toMaterialShapes()`가 채운다.

| M3 칸 | 토큰 |
|---|---|
| `primary` / `onPrimary` | `bg.brandSolid` / `fg.onBrandSolid` |
| `primaryContainer` / `onPrimaryContainer` | `bg.brandWeak` / `fg.brandContrast` |
| `inversePrimary` | `bg.brandWeak` |
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

## 글꼴

Pretendard 정적 OTF 400/500/700(orioncactus/pretendard v1.3.9)을 번들한다. Pretendard 한글 글리프는 굵기가 달라도 advance width가 같다. 그래서 굵기 테스트는 라틴 문자로 하고, 한글 굵기는 카탈로그 스크린샷으로 눈으로 확인한다.

```bash
./gradlew :core:designsystem:recordRoborazziDebug
```

결과는 `core/designsystem/build/outputs/roborazzi/catalog_*.png`다(`FoundationCatalogScreenshotTest`). 기준 스크린샷은 커밋하지 않는다.

## Konsist 규칙

`DesignTokenRulesTest`가 `feature`와 `app`에서 두 가지를 막는다.

- `MaterialTheme` import
- `Color(0x…)`, `Color(red = …)` 직접 생성

화면은 `PushlogTheme`의 토큰만 쓴다. 규칙이 막으면 토큰을 추가한다.
