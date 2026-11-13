# 디자인 시스템 미리보기 앱 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** debug 전용 앱 모듈 `:app-preview`를 추가해 디자인 시스템 foundation과 컴포넌트를 실기기에서 라이트/다크·글자 크기별로 확인하게 하고, 미리보기 누락을 빌드에서 막는다.

**Architecture:** 본 앱과 같은 Navigation 3 구조(화면마다 NavKey 파일 + entry 확장 함수). 모든 entry는 일반 함수 `previewEntryProvider`에 모여 호스트와 테스트가 함께 쓴다. 조작 바(테마·글자 크기)는 `PreviewApp`에서 `NavDisplay` 바깥에 하나만 둔다. 카탈로그 코드와 스크린샷 테스트는 `core:designsystem` 테스트에서 app-preview로 옮겨 한 벌만 남긴다.

**Tech Stack:** Kotlin, Compose(BOM 2026.08.00), Navigation 3, kotlinx-serialization, Robolectric + Roborazzi, Konsist, detekt, ktlint.

**Spec:** [`docs/superpowers/specs/2026-11-13-app-preview-design.md`](../specs/2026-11-13-app-preview-design.md). 이슈 #29.

## Global Constraints

- 모듈 경로 `:app-preview`, 디렉터리 `app-preview/`, 패키지·namespace·applicationId `com.citytexi.pushlog.preview`.
- release 변형 없음(`androidComponents.beforeVariants`로 끔). Hilt 없음.
- 의존 허용: `:core:designsystem`, `:core:ui`, `:core:navigation`, `:core:common`만(test 구성 제외).
- 키는 `@Serializable data object … : NavKey`. 모듈에 `alias(libs.plugins.kotlin.serialization)`.
- 이름 규칙(스펙 §5.2 표): `<Name>PreviewKey.kt`, `<Name>PreviewScreen.kt`(본문 `<Name>PreviewContent` + entry 함수 `<name>PreviewEntry`), 홈 `PreviewHomeKey`/`PreviewHomeScreen.kt`/`previewHomeEntry`. 컴포넌트는 `<Name>` = 컴포넌트 함수 이름(`PushlogButton`, `LoadingContent`).
- `fun previewEntryProvider(onOpen: (NavKey) -> Unit, onBack: () -> Unit): (NavKey) -> NavEntry<NavKey>`는 composable이 아니다.
- 글자 크기 배율은 절대값 1f / 1.3f / 2f, 이름 있는 상수. 화면 코드는 `PushlogTheme.*` 토큰만(Konsist `DesignTokenRulesTest`), 파일당 최상위 타입 하나(`FileStructureRulesTest`). main 소스 수치는 이름 있는 상수(detekt `MagicNumber`).
- 기존 규칙(ModuleRules, Konsist)을 약화하지 않는다. `@Suppress`·baseline·lint 비활성 금지. 기준 스크린샷 커밋 금지.
- 코드 규약 [`docs/code-conventions.md`](../../code-conventions.md). 커밋 `type: 한국어 설명` + 빈 줄 + `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- 작업 위치: `/Users/jeonheehoon/orca/workspaces/Pushlog/app-preview`(브랜치 `feat/app-preview`). 다른 체크아웃은 건드리지 않는다.

## Review Focus

1. 프로세스 종료 후 복원 — 키 serializer가 생성돼 백스택이 복원돼야 한다. Task 2 테스트가 모든 키의 `serializer()` 호출을 확인한다.
2. 조작 바 전환이 상세 화면에도 즉시 적용돼야 한다. Task 2에서 Robolectric 테스트로 다크 전환 뒤 상세 화면의 배경이 `bg.layerDefault` 다크 값인지 확인한다.
3. 다크 전환 뒤 시스템 바 아이콘이 배경과 대비돼야 한다. Task 2 Step에서 실기기·에뮬레이터로 눈으로 확인한다(자동 테스트 어려움).
4. 글자 크기 2×에서 `PreviewScaffold` 제목·조작 바가 잘리지 않아야 한다. Task 3의 fontscale2 캡처는 본문만 그리므로, Task 2에 조작 바·Scaffold의 2× 캡처를 하나 둔다.
5. 홈에서 뒤로 가기는 앱을 닫아야 한다(백스택이 비면 `NavDisplay`의 onBack이 finish). Task 2 구현에서 처리한다.

---

### Task 1: 모듈 골격과 의존 규칙

**Files:**
- Create: `app-preview/build.gradle.kts`, `app-preview/src/main/AndroidManifest.xml`, `app-preview/src/main/res/values/{strings,themes}.xml`, `app-preview/src/main/res/values-night/themes.xml`, 아이콘 리소스(adaptive icon), `app-preview/src/main/kotlin/com/citytexi/pushlog/preview/PreviewActivity.kt`
- Modify: `settings.gradle.kts`, `build-logic/convention/src/main/kotlin/ModuleRules.kt`(+KDoc), `ModuleRulesTest`(build-logic 테스트)

**Interfaces:**
- Produces: `:app-preview` 모듈, `PreviewActivity`(LAUNCHER, `setContent { }`는 Task 2가 채운다 — 이 Task에서는 빈 `PushlogTheme { }` 정도)

- [ ] **Step 1: 실패하는 테스트** — `ModuleRulesTest`에 `:app-preview` 허용(`:core:designsystem`, `:core:ui`, `:core:navigation`, `:core:common`)·금지(`:feature:sample`, `:data`, `:domain`, `:core:network`, `:core:database`, `:core:datastore`) 사례.
- [ ] **Step 2: 실패 확인** — `./gradlew :convention:test`(이름은 build-logic에서 확인) FAIL.
- [ ] **Step 3: 구현** — `ModuleRules.isAllowed`에 `from == ":app-preview" -> to in UI_CORE_ALLOWED`. 모듈 build 파일: plugins `starter.android.application`, `starter.android.compose`, `starter.android.screenshot`, `alias(libs.plugins.kotlin.serialization)`; `namespace`/`applicationId`; release 변형 끄기; 의존(스펙 §5.1). 테마는 `android:Theme.Material.Light.NoActionBar` 기반, 창 배경 라이트 `#FFFFFF` / 다크 `#16171B`. 라벨 "Pushlog Preview". `settings.gradle.kts`에 include.
- [ ] **Step 4: 확인** — `./gradlew :convention:test` PASS, `./gradlew :app-preview:assembleDebug` 성공, `./gradlew :app-preview:tasks --all | grep -c assembleRelease` → 0.
- [ ] **Step 5: 커밋** — `feat: 미리보기 앱 모듈 골격 추가`

---

### Task 2: 내비게이션, 홈, 조작 바

**Files:**
- Create: `preview/PreviewApp.kt`, `preview/navigation/previewEntryProvider` 파일(`PreviewEntryProvider.kt`), `preview/navigation/PreviewHomeKey.kt`, `preview/home/PreviewHomeScreen.kt`, `preview/catalog/PreviewCatalog.kt`(+ `PreviewCategory`, 항목 타입은 파일당 하나), `preview/scaffold/PreviewScaffold.kt`, `preview/settings/`(테마 모드·글자 배율 타입, 조작 바 composable)
- Test: `app-preview/src/test/.../navigation/PreviewEntryProviderTest.kt`, `.../PreviewAppTest.kt`

**Interfaces:**
- Produces:
  - `fun previewEntryProvider(onOpen: (NavKey) -> Unit, onBack: () -> Unit): (NavKey) -> NavEntry<NavKey>` — 내부는 `entryProvider { previewHomeEntry(onOpen); … }`
  - `val previewCatalog: List<PreviewCatalogItem>`; `PreviewCatalogItem(category: PreviewCategory, title: String, key: NavKey)`; `enum PreviewCategory { FOUNDATION, COMPONENT }`
  - `@Composable fun PreviewScaffold(title: String, onBack: () -> Unit, content: @Composable () -> Unit)`
  - `@Composable fun PreviewApp()` — 설정 상태(`rememberSaveable`), 조작 바, `PushlogTheme(darkTheme)` + `LocalDensity` 덮기, `NavDisplay(rememberNavBackStack(PreviewHomeKey), entryProvider = previewEntryProvider(...))`
  - Task 3·4는 화면을 만들고 `previewEntryProvider`와 `previewCatalog`에 한 줄씩 더한다.

- [ ] **Step 1: 실패하는 테스트** — `PreviewEntryProviderTest`(JVM): `(previewCatalog.map { it.key } + PreviewHomeKey)` 각각에 대해 `previewEntryProvider({}, {})(key)`가 예외 없이 entry를 돌려준다; 각 키 타입의 `serializer()`가 존재한다(Review Focus #1). `PreviewAppTest`(Robolectric): 조작 바에서 다크를 고르면 홈 배경이 `DarkPushlogColors.bg.layerDefault`; 글자 2× 캡처 `preview_app_fontscale2_light`(Review Focus #4).
- [ ] **Step 2: 실패 확인** — `./gradlew :app-preview:testDebugUnitTest` FAIL(컴파일).
- [ ] **Step 3: 구현** — 위 인터페이스. 이 Task에서는 `previewCatalog`가 비어 있어도 된다. 시스템 바: 테마가 바뀔 때 `enableEdgeToEdge(SystemBarStyle.auto(TRANSPARENT, TRANSPARENT) { isDark }, …)` 재호출. 배율 상수 1f/1.3f/2f. 백스택이 홈 하나일 때 뒤로 가기는 액티비티 종료(Review Focus #5). `PreviewActivity`가 `PreviewApp()`을 띄운다.
- [ ] **Step 4: 확인** — 테스트 PASS. 에뮬레이터/실기기에 `./gradlew :app-preview:installDebug` 후 다크 전환 시 시스템 바 아이콘 대비를 눈으로 확인(Review Focus #3, 보고서에 기록). 기기가 없으면 그 사실을 보고한다.
- [ ] **Step 5: 커밋** — `feat: 미리보기 앱 내비게이션과 조작 바 추가`

---

### Task 3: foundation 화면과 카탈로그 스크린샷 이전

**Files:**
- Create: `preview/foundation/{Color,Typography,Shape,Material}PreviewKey.kt`, `…PreviewScreen.kt`(각각 본문 `<Name>PreviewContent` + entry 함수), 공용 견본 composable이 필요하면 파일당 하나
- Create: `app-preview/src/test/.../catalog/FoundationCatalogScreenshotTest.kt`
- Delete: `core/designsystem/src/test/kotlin/com/citytexi/pushlog/core/designsystem/catalog/FoundationCatalogScreenshotTest.kt`
- Modify: `PreviewEntryProvider.kt`, `PreviewCatalog.kt`

**Interfaces:**
- Consumes: Task 2의 `previewEntryProvider`, `previewCatalog`, `PreviewScaffold`
- Produces: `ColorPreviewContent()`, `TypographyPreviewContent(styles: List<Pair<String, TextStyle>>)`, `ShapePreviewContent()`, `MaterialPreviewContent()`

- [ ] **Step 1:** 옛 테스트 파일의 카탈로그 composable을 본문으로 옮긴다(동작·모양 동일). 412dp 폭 틀과 `@Config(qualifiers = "w412dp-h6000dp")`는 새 테스트 파일에만 둔다. main의 수치는 이름 있는 상수로.
- [ ] **Step 2:** 새 스크린샷 테스트: 캡처 9개 이름 그대로(`catalog_color_{light,dark}`, `catalog_typography_{light,dark}`(45개), `catalog_typography_fontscale2_light`(regular만, `fontScale = 2f`), `catalog_shape_{light,dark}`, `catalog_material_{light,dark}`). 옛 파일 삭제.
- [ ] **Step 3:** 4개 화면을 `previewEntryProvider`와 `previewCatalog`(FOUNDATION)에 등록. Task 2의 `PreviewEntryProviderTest`가 새 키까지 통과.
- [ ] **Step 4: 확인** — `./gradlew :app-preview:recordRoborazziDebug :app-preview:testDebugUnitTest :core:designsystem:testDebugUnitTest` 성공, `app-preview/build/outputs/roborazzi/catalog_*.png` 9개.
- [ ] **Step 5: 커밋** — `refactor: 카탈로그를 미리보기 앱 foundation 화면으로 옮김`

---

### Task 4: 누락 방지 Konsist 규칙과 컴포넌트 화면

**Files:**
- Create: `konsist/src/test/kotlin/com/citytexi/pushlog/konsist/PreviewCoverageRulesTest.kt`
- Create: `preview/component/PushlogButtonPreviewKey.kt`, `PushlogButtonPreviewScreen.kt`, `LoadingContentPreviewKey.kt`, `LoadingContentPreviewScreen.kt`
- Modify: `PreviewEntryProvider.kt`, `PreviewCatalog.kt`

**Interfaces:**
- Consumes: `ArchitectureScope.kt`(`allProductionFiles()`, `isInPackage`), Task 2·3 등록 지점

- [ ] **Step 1: 실패하는 테스트** — `PreviewCoverageRulesTest`(LayerImportRulesTest 스타일, 대상 비어 있지 않은지 먼저 확인):
  - ``every public design system component has a preview screen``: 패키지가 정확히 `com.citytexi.pushlog.core.designsystem.component`인 production 파일의 공개 최상위 `@Composable` 함수 이름마다 `app-preview` production 파일 중 `<이름>PreviewScreen.kt`가 있다.
  - ``every preview key is listed in the catalog``: `app-preview` production 파일에서 부모 타입이 `NavKey`인 선언 중 `PreviewHomeKey`를 뺀 이름이 모두 `PreviewCatalog.kt` 본문에 등장한다.
- [ ] **Step 2: 실패 확인** — `./gradlew :konsist:test --tests '*PreviewCoverageRulesTest*'` FAIL, 첫 테스트가 `PushlogButton`, `LoadingContent`를 보고.
- [ ] **Step 3: 구현** — 두 컴포넌트 화면(변형·상태 나열: 버튼 enabled/disabled·짧은/긴 문구, LoadingContent 첫 로딩/새로고침/완료). entry·catalog(COMPONENT) 등록.
- [ ] **Step 4: 확인** — `./gradlew :konsist:test :app-preview:testDebugUnitTest` PASS. 고아 키 검사가 실제로 잡는지 한 번 확인: catalog에서 항목 하나를 잠시 지워 FAIL을 본 뒤 되돌린다(커밋하지 않음, 보고서에 기록).
- [ ] **Step 5: 커밋** — `test: 미리보기 누락을 막는 Konsist 규칙 추가`, `feat: PushlogButton·LoadingContent 미리보기 화면 추가`

---

### Task 5: CI와 문서

**Files:**
- Modify: `.github/workflows/ui-diff.yml`(paths에 `'app-preview/**'`), `.github/workflows/build-debug.yml`(Upload debug APK 단계 삭제, `assembleDebug` 유지)
- Modify: `docs/architecture/design-system.md`, `docs/architecture/module-structure.md`, `docs/architecture/build-and-release.md`, `AGENTS.md`

- [ ] **Step 1:** 워크플로 두 곳 수정. `build-debug.yml`에서 업로드 단계만 지우고 나머지(변경 감지, assemble)는 그대로.
- [ ] **Step 2:** 문서(스펙 §5.8): `design-system.md` "미리보기 앱" 절(목적, `./gradlew :app-preview:installDebug`, 등록 4단계, 이름 규칙, 누락 방지 테스트 3개가 잡는 것, 보조 부품은 internal·하위 패키지), 카탈로그 스크린샷 명령·경로를 `:app-preview` 기준으로; `module-structure.md` 모듈 표·의존 표·Konsist 규칙 목록; `build-and-release.md`에 app-preview는 release가 없다는 예외; AGENTS.md 라우팅 표 디자인 시스템 행에서 미리보기 앱 절을 가리킨다. 스펙 상태 줄은 이미 "승인됨".
- [ ] **Step 3:** `python3 docs/script/check_links.py docs` 0건.
- [ ] **Step 4: 커밋** — `ci: 미리보기 앱을 UI diff 대상에 넣고 APK 업로드를 뺌`, `docs: 미리보기 앱 문서 추가`

---

### Task 6: 최종 검증과 PR

- [ ] **Step 1:** `./gradlew ktlintFormat` 후 `./gradlew verifyStarter` 성공.
- [ ] **Step 2:** `git-flow` skill Work flow 4단계: push, `gh pr create --base develop`, 제목 `feat: add design system preview app module`, 본문에 `Closes #29`와 "카탈로그 이동으로 UI diff가 9개 삭제 + 9개 추가로 보임" 안내. CI 확인. merge는 사용자 확인 후.
