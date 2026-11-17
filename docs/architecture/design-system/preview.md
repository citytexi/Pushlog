---
id: design-system-preview
title: 디자인 시스템 미리보기와 스크린샷
category: architecture
status: living
platforms: android
verified: 2026-11-17
related_spec: [docs/superpowers/specs/2026-11-13-app-preview-design.md, docs/superpowers/specs/2026-11-14-preview-snapshot-design.md]
related_adr:
related_architecture: [design-system]
related_code: [PushlogPreviewTheme, PreviewCatalog, PreviewEntryProvider]
tags: [architecture, design-system]
---

# 디자인 시스템 미리보기와 스크린샷

`app-preview` 미리보기 앱과 프리뷰 스크린샷을 다룬다. 미리보기를 `PushlogPreviewTheme`으로 감싸는 이유는 [foundation.md](foundation.md#토큰-묶음과-접근)에 있다.

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

화면 함수가 detekt `TooManyFunctions`(파일당 11개, `private` 포함)를 넘으면 `@PushlogPreview` 섹션을 `<Component>Previews.kt`의 `internal` 함수로 옮긴다(선례: Switch, Checkbox, SelectBox).

### 누락 방지 테스트

세 가지가 빌드에서 실패한다.

| 잡는 것 | 테스트 |
|---|---|
| 1. 컴포넌트에 `<함수 이름>PreviewScreen.kt` 또는 `<함수 이름>PreviewKey` 선언이 없음 | Konsist `PreviewCoverageRulesTest`(`konsist`) |
| 2. `previewCatalog`의 키가 `previewEntryProvider`에 등록되지 않음 | `PreviewEntryProviderTest`(`app-preview` 단위 테스트) |
| 3. `NavKey` 구현체가 `previewCatalog`에서 참조되지 않음(고아 키) | Konsist `PreviewCoverageRulesTest` |

규칙 1의 대상은 `component` 패키지와 그 하위 패키지의 공개 최상위 `@Composable` 함수다. 컴포넌트의 보조 부품(슬롯, 하위 부품)은 `internal`로 둬서 화면을 요구받지 않게 한다.

## 프리뷰 스크린샷

`starter.android.screenshot`을 적용한 모듈은 namespace 아래 `@Preview`(private 포함)를 자동으로 캡처한다(Roborazzi Compose Preview 스캔). 결과는 `<module>/build/outputs/roborazzi/com.citytexi.pushlog.<...>.png`이고 UI diff PR 코멘트에서 비교된다. 언제 도는지는 [빌드 문서](../build-and-release.md#검증)에 있다.

- 상태는 되도록 프리뷰로 표현한다. 직접 쓴 스크린샷 테스트는 프리뷰로 그릴 수 없는 상태(눌림, 애니메이션 진행 시점, 특수 화면 폭, `LaunchedEffect`로 띄우는 스낵바)에만 쓴다. 예: `SampleScreenScreenshotTest`(에러 스낵바), `FoundationCatalogScreenshotTest`(긴 화면, 412dp 폭).
- 선택 컨트롤의 마크는 눌림을 주입하는 `PushlogCheckmarkScreenshotTest`, `PushlogRadiomarkScreenshotTest`, `PushlogSwitchmarkScreenshotTest`가 직접 찍는다. `PushlogSegmentedControlScreenshotTest`는 실제 터치 눌림과 indicator 이동 뒤를 찍는다. `PushlogSelectBoxScreenshotTest`는 눌림을 주입해 카드 색과 내용 축소를 찍는다. 크기·레이아웃과 2배 글자는 동작 컴포넌트와 같이 `app-preview` 섹션 프리뷰가 맡는다.
- 동작 컴포넌트(`PushlogActionButton`, `PushlogChip`, `PushlogContextualFloatingButton`)의 직접 쓴 스크린샷 테스트는 `states`(눌림 열 주입, 로딩 호 시계 진행)만 남겼다. 크기·레이아웃과 2배 글자는 `app-preview`에서 섹션마다 둔 `@PushlogPreview`(`PushlogActionButtonPreviews.kt`, 칩·플로팅 버튼은 `*PreviewScreen.kt` 아래쪽)가 Light/Dark/Light font 2x로 찍는다. 앱 화면용 `*PreviewContent`는 찍히지 않는다. 미리보기 캔버스(320x470)가 넘치는 부분을 잘라 내므로, 새 크기나 레이아웃을 추가하면 그 섹션의 `@PushlogPreview`를 하나 더 만든다.
- Robolectric에서 그릴 수 없는 프리뷰(Hilt·ViewModel)나 실행마다 달라지는 프리뷰에는 `@ScreenshotExcluded`(`core:ui`)를 붙인다.
- 프리뷰 이름은 파일명이 된다(공백은 `_`로 바뀐다). 글자·숫자·`_`·`-`·`.`·공백 외의 기호(`%`, 괄호, `+` 등)가 있으면 UI diff 스크립트가 경로 안전 검사(`report.py`의 `PART_RE`)에서 그 이미지를 빼고, 코멘트에 뺀 개수를 경고로 남긴다. `@PushlogPreview`의 200% 변형 이름이 `Light font 2x`인 이유다.
- 기본 tester(`AndroidComposePreviewTester`)를 쓴다. 파라미터 프리뷰를 쓰면 값 이름이 파일명에 들어가므로 이름 있는 값을 준다.
