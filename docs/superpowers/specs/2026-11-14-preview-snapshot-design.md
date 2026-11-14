# Preview 자동 스크린샷 캡처 설계

- 작성일: 2026-11-14
- 상태: 승인됨 (2026-11-14)
- 이슈: #31
- 범위: `starter.android.screenshot` 컨벤션 플러그인, 제외 어노테이션, `@PushlogPreview` 200% 변형 이름, `feature:sample` 스크린샷 정리, UI diff CI 연결과 버린 이미지 경고, 문서. `PushlogButton` 관련 파일과 #19(동작 컴포넌트)는 범위 밖이다.

## 1. 의도

`@Preview`(주로 `@PushlogPreview`: 라이트·다크·글자 200%)를 Roborazzi Compose Preview 스캔으로 자동 캡처한다. 프리뷰를 만들기만 하면 UI diff PR 코멘트에 나오게 하는 것이 목적이다. 지금은 직접 쓴 `captureRoboImage` 테스트만 캡처되므로, 프리뷰가 있어도 테스트를 따로 쓰지 않으면 diff에서 빠진다.

직접 쓴 스크린샷 테스트는 프리뷰로 그릴 수 없는 상태(눌림, 애니메이션 진행 시점, 특수 화면 폭, `LaunchedEffect`로 띄우는 스낵바 등)에만 남긴다.

## 2. 목표 · 비목표 · 반목표

목표

- `starter.android.screenshot`을 적용한 모든 모듈(현재 `core:designsystem`, `feature:sample`, `app-preview`, 앞으로 생길 feature)에서 모듈 namespace 아래 프리뷰를 자동 캡처한다. private 프리뷰도 포함한다.
- 캡처에서 뺄 프리뷰를 표시하는 제외 어노테이션.
- 생성 테스트는 실행 그래프에 Roborazzi 태스크가 있을 때만 돈다(§4).
- UI diff CI가 생성 이미지를 base·head로 비교한다.
- UI diff가 안전하지 않은 이름 때문에 버린 이미지가 있으면 PR 코멘트에 개수를 경고로 남긴다(§5.5).
- `feature:sample`의 직접 쓴 스크린샷 중 프리뷰로 그릴 수 있는 상태를 프리뷰로 옮긴다.
- 문서: `docs/architecture/build-and-release.md`, `docs/architecture/design-system.md`.

비목표

- `PushlogButton`, `PushlogButtonScreenshotTest`, 미리보기 앱의 `PushlogButton` 화면. #19가 이미 지우고 `PushlogActionButton`으로 바꿨다. 이 PR에서 손대면 #19 rebase 때 충돌만 난다. 그동안 버튼 이미지가 diff에 두 번 나오는 것은 받아들인다.
- `FoundationCatalogScreenshotTest` 정리. 긴 화면(`w412dp-h6000dp`)과 412dp 폭이 필요한 특수 폭 사례라 그대로 둔다. 같은 화면의 프리뷰 캡처(기본 높이에서 잘린 모습)와 겹치는 것은 받아들인다.
- 전용 tester, 파라미터 프리뷰 이름 검사, 프리뷰 group 필터(TEAMYG의 `UiSnapshotPreviewTester`). Pushlog에는 Lottie도 파라미터 프리뷰도 없다. 필요해지면 그때 추가한다.
- 기준 스크린샷 커밋. 지금처럼 커밋하지 않는다.

반목표

- `testStarter`와 CI `test` 워크플로가 프리뷰 수에 비례해 느려지는 것.
- `verifyStarter`가 실패하거나 일반 단위 테스트를 건너뛰는 것. TEAMYG의 "Roborazzi 태스크와 일반 테스트를 한 실행에 섞으면 `GradleException`" 규칙은 `verifyStarter`(`testDebugUnitTest`와 `recordRoborazziDebug`를 같이 건다)와 충돌하므로 가져오지 않는다.
- 생성 이미지가 UI diff 파이프라인 밖(다른 디렉터리, 하위 디렉터리)에 떨어져 비교에서 조용히 빠지는 것.

## 3. 현재 상태

- `AndroidScreenshotConventionPlugin`(`starter.android.screenshot`): Roborazzi 플러그인 적용, `isIncludeAndroidResources = true`, `robolectric.pixelCopyRenderMode=hardware`, Hilt 모듈에서 `failOnNoDiscoveredTests=false`. library·application 모두 적용된다.
- Robolectric 4.17 / Roborazzi 1.77.0 / targetSdk 36. 테스트는 Gradle JDK로 돈다(로컬·CI 모두 21, toolchain 없음). 지금 SDK 36 렌더에 `--add-opens`나 별도 launcher가 없어도 된다.
- `verifyStarter` = `testStarter` + Android 모듈의 `assembleDebug`, `assembleRelease`, `recordRoborazziDebug`. `testStarter`에는 Roborazzi 태스크가 없다.
- UI diff CI(`.github/workflows/ui-diff.yml`): base·head에서 각각 `recordRoborazziDebug` / `compareRoborazziDebug`를 전체 모듈에 돌린다. `stage.py`·`report.py`는 `<module>/build/outputs/roborazzi/*.png`(하위 디렉터리는 보지 않음)와 `<module>/build/test-results/roborazzi/debug/results/*.json`만 읽는다. 이름에서 `com.citytexi.pushlog.` 접두사를 떼고 28자씩 줄바꿈해 표에 넣는다. 워크플로 `paths`에는 이미 `core/ui/**`, `feature/**`, `app-preview/**`, `build-logic/**`가 있다.
- Roborazzi 1.77.0의 프리뷰 생성은 `CommonExtension`·`Variant`/`HasUnitTest` 기반이라 application 모듈(`app-preview`)도 지원한다(플러그인 바이트코드 확인). 플러그인은 `testImplementation`에 `roborazzi-compose-preview-scanner-support`, `ComposablePreviewScanner:android`, `robolectric`, `junit`이 **직접** 선언됐는지 검사한다. 전이 의존으로는 통과하지 않는다.
- TEAMYG는 Roborazzi 1.77.0과 `composablePreviewScanner` 0.9.3을 쓴다. 0.10.0은 1.77.0에서 `NoSuchMethodError`가 났다.

## 4. 접근

생성 테스트를 언제 돌릴지 세 가지를 검토했다.

| | 방법 | 장점 | 대가 |
|---|---|---|---|
| A | 항상 돈다 | 가장 단순하다. 렌더 오류가 `testStarter`에서 바로 드러난다 | 프리뷰가 늘수록 `testStarter`·CI `test`가 느려진다 |
| **B (선택)** | 실행 그래프에 Roborazzi 태스크가 있을 때만 돈다 | `verifyStarter`·UI diff CI에서는 돌고 `testStarter`·CI `test`에서는 빠진다. 섞어 부르기 금지 규칙이 필요 없다 | `testStarter`만 돌리면 프리뷰 렌더 오류를 못 본다. 완료 게이트(`verifyStarter`)에서는 잡힌다 |
| C | 생성 테스트 전용 Test 태스크 | 가장 깔끔하게 나뉜다 | Roborazzi 태스크가 `testDebugUnitTest`에 묶여 있어 플러그인·CI를 많이 고쳐야 한다 |

사용자가 B를 골랐다. TEAMYG는 명령줄 태스크 이름(`startParameter.taskNames`)으로 판정했다. 그 방식으로는 `verifyStarter`를 Roborazzi 실행으로 알아보지 못하므로, Pushlog는 **task graph**로 판정한다.

## 5. 설계

### 5.1 컨벤션 플러그인 (`AndroidScreenshotConventionPlugin`)

기존 설정은 그대로 두고 아래를 더한다.

- `roborazzi.generateComposePreviewRobolectricTests`
  - `enable = true`(항상). 생성·컴파일은 싸므로 실행만 거른다(§5.2).
  - `packages` = 모듈 `namespace`. 모듈 build 파일이 플러그인 적용 뒤에 namespace를 정하므로 provider로 늦게 읽는다.
  - `includePrivatePreviews = true`. 현재 프리뷰는 모두 private다.
  - tester는 기본값(`AndroidComposePreviewTester`)을 쓴다. 커스텀 tester를 쓰면 `includePrivatePreviews`·`annotationFilter`가 자동 적용되지 않으므로 기본 tester를 유지한다.
  - `annotationFilter` = 제외 어노테이션(§5.3)을 Exclude.
  - `robolectricConfig`: `sdk = [targetSdk]`(카탈로그 `targetSdk`). Robolectric 4.17은 36까지만 렌더한다.
- `testImplementation`에 직접 추가: `roborazzi-compose-preview-scanner-support`(버전 `roborazzi`), `io.github.sergio-sastre.ComposablePreviewScanner:android`(새 버전 키 `composablePreviewScanner = "0.9.3"`, 1.77.0과 맞춘 이유를 카탈로그 주석에 남긴다), `junit`(카탈로그에 있는 것 사용). `robolectric`·compose `ui-test-junit4`는 이미 있다.
- Java launcher·`--add-opens`는 넣지 않는다. 생성 테스트가 JDK 21에서 그것 없이 실패하면 그때 근거와 함께 추가한다.

### 5.2 실행 판정 (B)

- 실행 그래프에 Roborazzi 기록·비교 태스크(`record`/`compare`/`verify`/`verifyAndRecord` + `Roborazzi<Variant>`)가 하나라도 있으면 "Roborazzi 실행"이다. 모듈이 아니라 전체 그래프 기준이다.
- Roborazzi 실행이 아니면 `Test` 태스크 필터에서 생성 테스트 클래스(`com.github.takahirom.roborazzi.RoborazziPreviewParameterizedTests*`)를 제외한다. `isFailOnNoMatchingTests = false`.
- Roborazzi 실행이면 생성 테스트와 일반 단위 테스트를 **모두** 돌린다. TEAMYG처럼 일반 테스트를 빼면 `verifyStarter`가 단위 테스트를 건너뛴다.
  - 결과: UI diff CI에서 단위 테스트가 실패하면 그 모듈은 렌더 실패(`failed_modules`)로 보고된다. 단위 테스트 실패는 CI `test` 워크플로가 따로 잡으므로 받아들인다.
- configuration cache가 켜져 있다. 판정 방법은 configuration cache와 호환돼야 하고, 캐시 재사용 시에도 같은 결과가 나와야 한다. 캐시 키에 요청 태스크가 들어가므로 그래프 기반 판정은 재사용 시에도 일관된다. 다만 구현 방법(`taskGraph.whenReady` 등)이 CC 경고 없이 도는지 확인한다.

동작 표:

| 실행 | 생성 테스트 | 일반 단위 테스트 |
|---|---|---|
| `./gradlew testStarter`, CI `test` | 안 돎 | 돎 |
| `./gradlew verifyStarter` | 돎(record) | 돎 |
| `./gradlew recordRoborazziDebug`, CI ui-diff | 돎 | 돎 |
| `./gradlew :feature:sample:testDebugUnitTest` | 안 돎 | 돎 |

### 5.3 제외 어노테이션

- 이름·위치: `com.citytexi.pushlog.core.ui.preview.ScreenshotExcluded` (`core:ui`, `PushlogPreview` 옆). 스크린샷이 필요한 모듈은 모두 이미 `core:ui`를 의존한다.
- 대상: 함수. 쓰임: Hilt·ViewModel이 필요해 Robolectric에서 그릴 수 없는 프리뷰, 시간·난수에 따라 매번 달라지는 프리뷰.
- 스캐너가 클래스 파일에서 읽을 수 있는 retention이어야 한다(구현 때 확인).
- 지금 붙일 프리뷰는 없다. KDoc에 용도를 적는다.

### 5.4 `feature:sample` 정리

- 프리뷰로 옮긴다: 빈 목록, 항목, 새로고침(첫 로드), 새로고침(항목 있음). 각각 `@PushlogPreview`. 방법(별도 함수 4개 또는 `PreviewParameterProvider`)은 구현에서 정한다. 파라미터 프리뷰를 쓰면 이름이 파일명에 들어가므로 이름 있는 값을 쓴다.
- `SampleScreenScreenshotTest`에는 에러 스낵바 3종(`Network`, `Server`, `Unknown`)만 남긴다. 클래스 KDoc에 "스낵바는 `LaunchedEffect`로 띄워야 해서 프리뷰로 못 그린다"를 적는다.
- 기존 이미지 이름(`sample_screen_empty` 등)은 사라지고 생성 이름으로 바뀐다. 첫 PR의 UI diff에서는 removed·new로 보인다.

### 5.5 UI diff CI

#### 파일명과 `@PushlogPreview` 이름

- 생성 파일명은 `<선언 클래스>.<함수>_<프리뷰 정보>` 꼴이다. 프리뷰 정보는 기본값이 아닌 항목(이름, `FONT_2_0f`, `NIGHT`, `WITH_BACKGROUND` 등)이 이어 붙은 것이다(ComposablePreviewScanner 0.9.3 `AndroidPreviewScreenshotIdBuilder`). 프리뷰 `name`은 공백만 `_`로 바뀌고 나머지 문자는 그대로 남는다.
- `report.py`의 `PART_RE = [\w.\-]+`는 `%`를 허용하지 않는다. 지금 이름 `Light font 200%`로는 200% 이미지 전부가 `split_golden`/`is_safe_path`에서 조용히 버려진다.
- 결정: `core:ui`의 `PushlogPreview`에서 세 번째 변형 이름을 `"Light font 200%"`에서 `"Light font 2x"`로 바꾼다. 바뀌는 것은 Android Studio 프리뷰 라벨뿐이다. 파일명에 `FONT_2_0f`가 따로 붙으므로 정보는 줄지 않는다. KDoc의 "200% font"는 의미 설명이므로 그대로 두어도 된다.
- 검토했지만 고르지 않은 것: `PART_RE`를 넓히는 방법은 이미지 브랜치 경로와 코멘트 링크를 지키는 장치를 약하게 만든다. `stage.py`에서 이름을 정리하는 방법은 base·head 규칙을 맞추고 정리한 이름끼리 겹치는 경우를 다뤄야 한다. 둘 다 얻는 것에 비해 복잡하다.

#### 버린 이미지 경고

- 앞으로 다른 프리뷰 이름에 `%`, `(`, `+` 같은 문자가 들어가면 같은 방식으로 조용히 빠진다. 이를 드러내기 위해, `report.py`가 안전하지 않은 이름 때문에 버린 캡처가 있으면 PR 코멘트에 개수를 경고로 남긴다.
  - 셀 대상: 결과 JSON이 정상인데 golden 이름이 `split_golden`/`is_safe_path`를 통과하지 못한 경우. base 목록(`read_base_list`)에서 같은 이유로 버린 이름도 포함한다. JSON 손상·크기 초과 같은 다른 이유로 버린 것과는 구분한다.
  - 경고에는 개수만 넣는다. 파일명 자체는 코멘트에 넣지 않는다. 안전하지 않은 문자가 들어간 이름을 마크다운에 넣지 않기 위해서다.
  - 경고 문구, 코멘트 안 위치, report JSON 필드 이름은 구현에서 정한다. `report_to_dict`/`report_from_dict` 왕복과 `test_report.py`에 테스트를 더한다. `stage.py`와 계약(`OUTPUTS`·`RESULTS`·`STATUSES`)이 바뀌면 `test_stage.py`도 같이 고친다.

#### 구현 때 확인할 것

- 생성 이미지가 `build/outputs/roborazzi/` 바로 아래 PNG로 떨어지는지(하위 디렉터리면 `stage.py`가 못 본다).
- 생성 테스트의 비교 결과 JSON이 `build/test-results/roborazzi/debug/results/`에 생기는지.
- 이름을 바꾼 뒤 모든 생성 파일명이 `is_safe_path`를 통과하는지(`Light_font_2x`, 선언 클래스 `...Kt`, 점 구분).
- 어긋나면 Roborazzi 출력 설정을 먼저 맞추고, 그래도 안 되면 스크립트를 고친다.

### 5.6 문서

- `build-and-release.md`: `verifyStarter`가 프리뷰 렌더를 포함하고 `testStarter`는 포함하지 않는 이유, 판정 기준(task graph).
- `design-system.md`: 프리뷰가 자동 캡처된다는 것, 직접 쓴 테스트를 둘 때의 기준, `@ScreenshotExcluded`, 결과 위치. 기존 "글꼴" 절의 `catalog_*.png` 설명은 그대로 둔다.
- 결정 배경이 코드로 복원되지 않는 부분만 적는다(AGENTS.md 문서 규칙).

## 6. 검증

- `./gradlew verifyStarter` 통과.
- `./gradlew testStarter` 실행 시 생성 테스트가 돌지 않음(테스트 리포트에 `RoborazziPreviewParameterizedTests` 없음).
- `python3 -m unittest discover -s .github/scripts/ui_diff -p 'test_*.py'` 통과(경고 테스트 포함).
- `./gradlew recordRoborazziDebug` 뒤 세 모듈의 `build/outputs/roborazzi/`에 프리뷰 PNG가 라이트·다크·200%로 생김. `app-preview` 포함.
- configuration cache 재사용 실행(같은 명령 두 번)에서도 위 결과가 같음.
- 가능하면 PR의 UI diff 코멘트로 생성 이미지가 비교되는지 확인.

## 7. 구현자에게 맡기는 것

- 그래프 판정 구현 방식(§5.2의 CC 조건만 지키면 됨).
- sample 프리뷰를 함수로 나눌지 파라미터로 할지.
- 제외 어노테이션 retention·target 세부.
- 버린 이미지 경고의 문구·위치·JSON 필드 이름.
- `ModuleRules`·Konsist에 걸리는 부분은 규칙을 약화하지 않고 구조로 맞춘다.
