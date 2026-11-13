# 디자인 시스템 미리보기 앱 설계

- 작성일: 2026-11-13
- 상태: 승인됨 (2026-11-13)
- 이슈: #29 (상위 #17)
- 범위: 새 앱 모듈 `:app-preview`, foundation·컴포넌트 미리보기 화면, 누락 방지 테스트, 모듈 규칙·CI·문서. 새 디자인 시스템 컴포넌트는 범위 밖이다(#19~#22).

## 1. 의도

디자인 시스템을 실기기에서 바로 확인하는 앱을 만든다. 쓰는 사람은 두 부류다.

- 개발자: 컴포넌트를 만들며 실기기에서 라이트/다크, 글자 크기별 모양을 확인한다.
- 디자이너·팀원: 검수한다. 배포·아티팩트 업로드는 하지 않고, 개발자가 `./gradlew :app-preview:installDebug`로 기기에 설치해 준다.

참고는 TEAMYG-Android의 `app-preview`(컴포넌트 갤러리)다. 같은 구조를 쓰되, 그쪽에서 생긴 갤러리 누락(컴포넌트를 만들고 갤러리에 등록하지 않음)을 테스트로 막는다.

## 2. 목표 · 비목표 · 반목표

목표

- `:app-preview` 앱 모듈. debug 빌드만 만든다. 앱 아이디 `com.citytexi.pushlog.preview`로 본 앱과 함께 설치된다.
- 홈: 카테고리별 목록. 항목을 누르면 상세 화면.
- foundation 화면: 색(`bg`/`fg`/`stroke`), 글자(45개 스타일), 간격·모서리·그림자, M3 컴포넌트(테마 연결 확인).
- 컴포넌트 화면: 현재 디자인 시스템 컴포넌트 `PushlogButton`, `LoadingContent`의 변형·상태.
- 조작 바: 테마 라이트/다크/시스템, 글자 크기 1×/1.3×/2×. 모든 화면에 적용된다.
- 누락 방지 테스트(§5.5)와 등록 절차 문서(§5.8).

비목표

- release 빌드, 서명, 배포(Play, Firebase App Distribution), CI APK 업로드.
- 검색·필터, 컴포넌트 속성을 바꾸는 조작 패널. 변형·상태는 화면에 나열한다.
- Hilt. 미리보기 화면은 DI가 필요 없다.
- 본 앱(`:app`) 코드 변경.

반목표

- 컴포넌트를 만들었는데 미리보기 앱에 없는 상태가 빌드를 통과하는 것.
- 미리보기 앱이 feature·data·domain이나 데이터 계층 core(`network`, `database`, `datastore`)에 의존해 실제 데이터 흐름이 섞이는 것.
- 카탈로그 그리는 코드가 테스트와 미리보기 앱에 두 벌 생기는 것.

## 3. 제약

- `ModuleRules`: `:app-preview`는 UI core(`:core:designsystem`, `:core:ui`, `:core:navigation`)와 `:core:common`만 의존한다(§5.6).
- 패키지 `com.citytexi.pushlog.preview`. Konsist는 이 패키지를 `app` 계층으로 본다(`ArchitectureScope.layer()`). 그래서 `DesignTokenRulesTest`(MaterialTheme import·`Color(0x…)` 금지)와 `FileStructureRulesTest`(파일당 최상위 타입 하나)가 그대로 적용된다.
- 내비게이션은 본 앱과 같은 Navigation 3 방식이다: 화면마다 NavKey 파일, `EntryProviderScope<NavKey>`의 entry 확장 함수, 호스트 등록. NavKey는 `@Serializable`이다(`rememberNavBackStack`).
- test에서 main으로 옮기는 코드에는 detekt `MagicNumber`가 적용된다(test 경로만 제외). `@Suppress`·baseline 금지이므로 수치는 이름 있는 상수로 둔다.
- `verifyStarter`는 작업 이름으로 모아 돌리므로, release 변형을 끈 모듈은 `assembleRelease`가 없어 건너뛴다.

## 4. 접근 방식

| 방식 | 내용 | 대가 | 결정 |
|---|---|---|---|
| A. 목록 하나로 구동 | `PreviewEntry(category, title, content)` 목록 하나가 홈과 상세를 만든다. 키는 홈·상세 둘 | 미리보기가 화면 이동·상태 복원이 필요해지면 그 항목만 예외 처리해야 하고, 그 기준을 지키는 것이 또 하나의 규칙이 된다 | 탈락 |
| **B. 화면마다 NavKey (TEAMYG·본 앱 방식)** | 화면마다 키 파일 + 화면 + entry 등록 + 홈 항목 | 추가할 때 네 곳을 고친다. 누락은 테스트로 막는다 | **선택** |
| C. 한 화면에 모두 나열 | 내비게이션 없음 | 컴포넌트가 수십 개가 되면 쓰기 어렵다 | 탈락 |

사용자는 B를 골랐다. 모든 화면이 같은 모양이라 고를 판단이 없고, 본 앱의 내비게이션 구조와 같아 따로 배울 것이 없다. B의 약점인 누락은 §5.5 테스트로 빌드에서 막는다.

## 5. 설계

### 5.1 모듈

- `app-preview/build.gradle.kts` plugins: `starter.android.application`, `starter.android.compose`, `starter.android.screenshot`(§5.4 스크린샷 테스트), `alias(libs.plugins.kotlin.serialization)`(키 serializer 생성. 없으면 `rememberNavBackStack`이 시작하자마자 "Serializer … not found"로 죽는다). Hilt 없음.
- `namespace`/`applicationId` = `com.citytexi.pushlog.preview`. 버전은 본 앱 카탈로그 값을 그대로 쓴다(배포 안 함).
- release 변형을 끈다: `androidComponents { beforeVariants(selector().withBuildType("release")) { it.enable = false } }`. (빌드 타입을 지우는 것보다 공통 convention plugin과 덜 부딪힌다.)
- 의존: `:core:designsystem`, `:core:ui`(`PushlogPreview`), `:core:navigation`(nav3 `api`), `libs.kotlinx.serialization.json`, androidx activity-compose, core-ktx.
- `settings.gradle.kts`에 `include(":app-preview")`.
- 리소스: 본 앱 테마(`Theme.Pushlog.Splash`)는 `:app` 리소스라 쓸 수 없다. app-preview에 자체 테마(`values/themes.xml`, `values-night/themes.xml`, 창 배경 = `bg.layerDefault` 값)를 둔다.
- 매니페스트: LAUNCHER 액티비티 하나, 라벨 "Pushlog Preview". 아이콘은 본 앱 자리표시자 아이콘과 구분되게 단색 배경의 간단한 adaptive icon.

### 5.2 이름과 화면 구성

```
PreviewActivity → PreviewApp(설정 상태 + 조작 바) → NavDisplay(previewEntryProvider)
```

| 화면 | 키 | 화면 파일 | entry 함수 | 카테고리 |
|---|---|---|---|---|
| 홈 | `PreviewHomeKey` | `PreviewHomeScreen.kt` | `previewHomeEntry` | – |
| 색 | `ColorPreviewKey` | `ColorPreviewScreen.kt` | `colorPreviewEntry` | FOUNDATION |
| 글자 | `TypographyPreviewKey` | `TypographyPreviewScreen.kt` | `typographyPreviewEntry` | FOUNDATION |
| 간격·모서리·그림자 | `ShapePreviewKey` | `ShapePreviewScreen.kt` | `shapePreviewEntry` | FOUNDATION |
| M3 컴포넌트 | `MaterialPreviewKey` | `MaterialPreviewScreen.kt` | `materialPreviewEntry` | FOUNDATION |
| `PushlogButton` | `PushlogButtonPreviewKey` | `PushlogButtonPreviewScreen.kt` | `pushlogButtonPreviewEntry` | COMPONENT |
| `LoadingContent` | `LoadingContentPreviewKey` | `LoadingContentPreviewScreen.kt` | `loadingContentPreviewEntry` | COMPONENT |

- 컴포넌트 화면 이름 규칙: `<컴포넌트 함수 이름>PreviewKey` / `<컴포넌트 함수 이름>PreviewScreen.kt` / `<함수 이름 첫 글자 소문자>PreviewEntry`. §5.5 테스트 1이 파일 이름으로 맞춘다.
- 카테고리: `FOUNDATION`, `COMPONENT`(#19~#22에서 동작·입력·피드백·목록 등으로 나눈다).
- 홈은 `PreviewCatalog`(카테고리, 제목, 키)의 목록만 그린다.
- 각 화면 파일은 본문 composable(`<Name>PreviewContent`)과 entry 함수를 둔다. 상세 화면은 공통 틀 `PreviewScaffold`(뒤로 가기 + 제목 + 스크롤 본문) 안에 본문을 그린다. 스크린샷 테스트는 본문만 그린다(§5.4).
- `fun previewEntryProvider(onOpen: (NavKey) -> Unit, onBack: () -> Unit): (NavKey) -> NavEntry<NavKey>`: composable이 아닌 일반 함수로 모든 entry를 등록한다. 호스트(`NavDisplay`)와 §5.5 테스트 2가 같은 함수를 쓴다.

### 5.3 조작 바와 설정

- 조작 바는 `PreviewApp`에서 `NavDisplay` 바깥 한 곳에 둔다(화면마다 상태를 넘기지 않는다). 홈·상세 모두에서 보인다.
- 테마: 라이트 / 다크 / 시스템. `PushlogTheme(darkTheme = …)`로 `NavDisplay`를 감싼다.
- 글자 크기: 1× / 1.3× / 2×, **절대값**이다(`fontScale = 배율`, 시스템 글자 크기 설정과 무관). `LocalDensity`를 `Density(density, fontScale = 배율)`로 덮는다. 한계: `LocalConfiguration.fontScale`은 바뀌지 않는다. 배율은 이름 있는 상수로 둔다.
- 상태는 `PreviewApp`의 `rememberSaveable`.
- 시스템 바 아이콘 색: 선택한 테마가 바뀔 때마다 `enableEdgeToEdge(SystemBarStyle.auto(TRANSPARENT, TRANSPARENT) { isDark }, …)`를 다시 호출한다(`DisposableEffect(isDark)` 등). `SystemBarStyle.auto`의 판정 람다는 호출할 때 한 번만 읽히기 때문이다.

### 5.4 foundation 화면과 카탈로그 스크린샷

- 지금 `core/designsystem/src/test/.../catalog/FoundationCatalogScreenshotTest.kt` 안에 private으로 있는 카탈로그 composable(색 견본, 글자 목록, 간격·모서리·그림자, M3 묶음)을 foundation 화면의 본문(`<Name>PreviewContent`)으로 옮긴다.
- 글자 본문은 그릴 스타일 목록을 인자로 받는다(전체 45개 / regular만).
- 스크린샷 테스트는 `app-preview/src/test`로 옮겨 본문만 그린다. 지금처럼 412dp 고정 폭 틀(`CatalogRoot`)과 큰 화면 설정(`@Config` 세로 6000dp)은 테스트 쪽에만 둔다. 캡처 9개는 그대로다: color/typography/shape/material × 라이트·다크(typography는 45개) + `catalog_typography_fontscale2_light`(regular만, `fontScale = 2f`).
- `core:designsystem`의 카탈로그 테스트 파일은 지운다. `core:designsystem`에는 `PushlogButtonScreenshotTest`가 남으므로 screenshot 플러그인은 그대로 둔다.
- 본문은 `PushlogTheme.*` 토큰만 쓴다(Konsist가 강제). 수치(배율, 견본 크기 등)는 이름 있는 상수로 둔다(§3 detekt).

### 5.5 누락 방지 테스트

세 가지가 빌드에서 실패해야 한다.

1. **컴포넌트 누락** (Konsist, `konsist` 모듈): `core:designsystem`의 `com.citytexi.pushlog.core.designsystem.component` 패키지(하위 패키지 제외)에 있는 공개 최상위 `@Composable` 함수마다, `app-preview`에 `<함수 이름>PreviewScreen.kt`가 있어야 한다. 컴포넌트의 보조 부품(슬롯, 하위 부품)은 `internal`로 두거나 하위 패키지(`component.<name>`)에 둬서 대상에서 빠지게 한다.
2. **키 연결 누락** (JVM 단위 테스트, `app-preview`): `PreviewCatalog`의 모든 키와 `PreviewHomeKey`를 `previewEntryProvider(onOpen = {}, onBack = {})`에 넣어 entry가 나와야 한다. 없는 키는 예외가 난다. content는 호출하지 않으므로 Robolectric이 필요 없다.
3. **목록 누락·고아 키** (Konsist): `app-preview`에서 `NavKey`를 구현한 모든 타입은 `PreviewHomeKey`를 빼고 `PreviewCatalog` 파일에서 참조돼야 한다.

### 5.6 모듈 규칙

- `ModuleRules.isAllowed`: `from == ":app-preview" -> to in UI_CORE_ALLOWED`(designsystem, ui, navigation, common). test 구성은 원래 검사하지 않는다.
- `ModuleRules` KDoc의 모듈 목록 갱신.
- `ModuleRulesTest`: 허용(designsystem·ui·navigation·common), 금지(feature·data·domain·network·database·datastore) 사례 추가.

### 5.7 CI

- `.github/workflows/ui-diff.yml`의 `on.pull_request.paths`에 `app-preview/**` 추가.
- 카탈로그를 옮기는 이번 PR에서는 UI diff가 같은 캡처를 "9개 삭제 + 9개 추가"로 보인다(모듈 상대 경로 기준). PR 본문에 알린다.
- `.github/workflows/build-debug.yml`: APK 업로드 단계를 지운다(본 앱 포함). `./gradlew assembleDebug`는 그대로 두며, 이것이 app-preview debug 빌드도 함께 검사한다.

### 5.8 문서

- `docs/architecture/design-system.md`에 "미리보기 앱" 절: 목적, 설치(`./gradlew :app-preview:installDebug`), 컴포넌트 추가 시 등록 4단계(키 파일, 화면 파일과 entry 함수, `previewEntryProvider` 등록, `PreviewCatalog` 항목), §5.2 이름 규칙, §5.5 테스트가 무엇을 잡는지, 보조 부품은 internal·하위 패키지. 카탈로그 스크린샷 실행 명령과 경로를 `app-preview` 기준으로 고친다.
- AGENTS.md 라우팅 표의 디자인 시스템 행에서 이 절을 가리킨다.
- `docs/architecture/module-structure.md`: 모듈 표와 의존 표에 `:app-preview`.
- `docs/architecture/build-and-release.md`: `verifyStarter`의 `assembleRelease` 설명에 "app-preview는 debug 전용이라 release가 없다" 추가.

## 6. builder에게 맡기는 것

- `PreviewScaffold`·홈·조작 바의 생김새(토큰만 사용).
- 각 컴포넌트 화면에 나열할 변형·상태 묶음.
- 앱 아이콘 모양.
- `PreviewCatalog`의 자료 형태(목록·카테고리 묶음), §5.5 조건을 만족하면 된다.
