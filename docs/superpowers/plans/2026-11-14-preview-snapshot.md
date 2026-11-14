# Preview 자동 스크린샷 캡처 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `starter.android.screenshot` 모듈의 `@Preview`를 Roborazzi 프리뷰 스캔으로 자동 캡처해 `verifyStarter`와 UI diff CI가 비교하게 한다.

**Architecture:** 컨벤션 플러그인이 `generateComposePreviewRobolectricTests`를 켜고, 실행 그래프에 Roborazzi 기록·비교 태스크가 없으면 생성 테스트 클래스를 `Test` 필터로 뺀다. CI 스크립트는 그대로 생성 PNG를 비교하고, `report.py`는 안전하지 않은 이름 때문에 버린 캡처 수를 코멘트에 경고로 남긴다.

**Tech Stack:** Gradle convention plugin(Kotlin DSL), Roborazzi 1.77.0, ComposablePreviewScanner 0.9.3, Robolectric 4.17, Python 3 unittest.

**Spec:** `docs/superpowers/specs/2026-11-14-preview-snapshot-design.md`

## Global Constraints

- `AGENTS.md`의 규칙과 [`docs/code-conventions.md`](../../code-conventions.md)를 따른다. `@Suppress`, detekt baseline, ktlint 비활성화, `ModuleRules`·Konsist 약화는 금지다.
- 문서·주석 설명은 한국어, 식별자는 영어. 커밋은 `<type>: 한국어 설명`이고 끝에 `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`를 붙인다.
- 기준 스크린샷(PNG)은 커밋하지 않는다.
- `PushlogButton.kt`, `PushlogButtonScreenshotTest.kt`, `app-preview`의 `PushlogButton` 화면, `FoundationCatalogScreenshotTest.kt`는 건드리지 않는다.
- 버전: `roborazzi = "1.77.0"`(기존), `composablePreviewScanner = "0.9.3"`(새 키), Robolectric `sdk = [targetSdk]`(카탈로그 `targetSdk = "36"`).
- 생성 테스트 클래스 접두사: `com.github.takahirom.roborazzi.RoborazziPreviewParameterizedTests`.
- 제외 어노테이션 FQCN: `com.citytexi.pushlog.core.ui.preview.ScreenshotExcluded`.
- `@PushlogPreview` 세 번째 변형 이름: `"Light font 2x"`.
- Java launcher, `--add-opens`는 추가하지 않는다. 생성 테스트가 그것 없이 실패하면 멈추고 보고한다.
- 완료 전 `./gradlew verifyStarter` 통과.

## Review Focus

1. configuration cache 재사용: 같은 명령을 두 번 돌려 두 번째가 캐시를 재사용해도 생성 테스트 포함 여부가 같아야 한다. Task 1 Step 7에서 확인한다.
2. 생성 테스트만 있는 모듈 또는 생성 테스트가 빠진 뒤 테스트가 없는 모듈: `testStarter`에서 "no matching tests"로 실패하면 안 된다. Task 1 Step 6에서 확인한다.
3. application 모듈 `app-preview`: library와 똑같이 프리뷰 PNG가 생겨야 한다. Task 1 Step 8에서 확인한다.
4. 새 프리뷰 이름에 `%`, `(`, `+`가 들어가는 경우: 이미지는 빠지지만 코멘트에 경고가 남아야 한다. Task 3 테스트가 확인한다.
5. 같은 캡처가 base 목록과 head 결과에 모두 안전하지 않은 이름으로 있는 경우: 경고 수가 두 번 세지면 안 된다. Task 3 테스트가 확인한다.

---

### Task 1: 컨벤션 플러그인 프리뷰 스캔·실행 판정, 제외 어노테이션, 프리뷰 이름

**Files:**
- Modify: `gradle/libs.versions.toml` (versions: `composablePreviewScanner`, libraries: `roborazzi-compose-preview-scanner-support`, `composable-preview-scanner-android`)
- Create: `build-logic/convention/src/main/kotlin/RoborazziRun.kt`
- Test: `build-logic/convention/src/test/kotlin/RoborazziRunTest.kt`
- Modify: `build-logic/convention/src/main/kotlin/AndroidScreenshotConventionPlugin.kt`
- Create: `core/ui/src/main/kotlin/com/citytexi/pushlog/core/ui/preview/ScreenshotExcluded.kt`
- Modify: `core/ui/src/main/kotlin/com/citytexi/pushlog/core/ui/preview/PushlogPreview.kt`

**Interfaces:**
- Produces: `internal fun isRoborazziRunTask(taskName: String): Boolean` (`RoborazziRun.kt`, 최상위). `ScreenshotExcluded` 어노테이션.

- [ ] **Step 1: 실패하는 테스트 작성** (`RoborazziRunTest`, JUnit4, `ModuleRulesTest`와 같은 스타일)

```kotlin
@Test fun recordCompareVerifyTasksAreRoborazziRuns() {
    listOf("recordRoborazziDebug", "compareRoborazziDebug", "verifyRoborazziDebug", "verifyAndRecordRoborazziDebug")
        .forEach { assertTrue(it, isRoborazziRunTask(it)) }
}
@Test fun otherTasksAreNot() {
    listOf("testDebugUnitTest", "clearRoborazziDebug", "verifyStarter", "testStarter", "recordRoborazzi", "assembleDebug")
        .forEach { assertFalse(it, isRoborazziRunTask(it)) }
}
```

- [ ] **Step 2: 실패 확인**

Run: `./gradlew -p build-logic :convention:test --tests RoborazziRunTest`
Expected: 컴파일 실패(`isRoborazziRunTask` 없음).

- [ ] **Step 3: `isRoborazziRunTask` 구현**

정규식 `^(record|compare|verify|verifyAndRecord)Roborazzi[A-Z]\w*$`. KDoc: 이름이 아니라 실행 그래프의 태스크 이름에 쓰는 이유(`verifyStarter`가 `recordRoborazziDebug`를 끌어온다).

- [ ] **Step 4: 통과 확인**

Run: `./gradlew -p build-logic :convention:test`
Expected: PASS (`ModuleRulesTest` 포함).

- [ ] **Step 5: 플러그인·카탈로그·`core:ui` 변경**

- 카탈로그: `composablePreviewScanner = "0.9.3"`(주석: Roborazzi 1.77.0과 맞춘 버전, 0.10.0은 `NoSuchMethodError`), `roborazzi-compose-preview-scanner-support`(`io.github.takahirom.roborazzi`, `version.ref = "roborazzi"`), `composable-preview-scanner-android`(`io.github.sergio-sastre.ComposablePreviewScanner:android`).
- 플러그인 `dependencies`에 `testImplementation`으로 직접 추가: 위 두 개와 `libs.junit4`. 주석: Roborazzi 플러그인이 직접 선언만 검사한다.
- `configure<RoborazziExtension> { generateComposePreviewRobolectricTests { ... } }`: `enable = true`, `packages = provider { listOfNotNull(<Library|Application>Extension.namespace) }`, `includePrivatePreviews = true`, `annotationFilter = AnnotationFilter.Exclude("com.citytexi.pushlog.core.ui.preview.ScreenshotExcluded")`, `robolectricConfig = mapOf("sdk" to "[${libs.versions.targetSdk.get()}]")`. tester는 지정하지 않는다(기본 `AndroidComposePreviewTester`). library·application 양쪽의 namespace를 읽는다.
- 실행 판정: `gradle.taskGraph.whenReady { graph -> ... }`에서 `graph.allTasks.none { isRoborazziRunTask(it.name) }`이면 이 모듈의 모든 `Test` 태스크 필터에 `excludeTestsMatching("com.github.takahirom.roborazzi.RoborazziPreviewParameterizedTests*")`, `isFailOnNoMatchingTests = false`. Roborazzi 실행이면 아무것도 빼지 않는다(일반 단위 테스트도 돈다). 주석에 이유(§5.2: `verifyStarter`가 섞어 부르므로 TEAMYG식 명령줄 판정·섞기 금지는 쓰지 않음)를 남긴다. 이 방식이 configuration cache 문제를 내면 같은 동작을 CC 안전한 다른 방법으로 바꾼다. 결과는 Step 6·7이 판정한다.
- 플러그인 KDoc의 "Modules without screenshot tests ..." 문단을 프리뷰 캡처 포함 내용으로 고친다.
- `ScreenshotExcluded.kt`: `@Target(AnnotationTarget.FUNCTION)`, `@Retention(AnnotationRetention.BINARY)` 이상, KDoc 언어·형식은 `docs/code-conventions.md`를 따른다. 용도: Robolectric에서 그릴 수 없는 프리뷰(Hilt·ViewModel), 매번 달라지는 프리뷰.
- `PushlogPreview.kt`: `name = "Light font 200%"` → `name = "Light font 2x"`.

- [ ] **Step 6: `testStarter`에서 생성 테스트가 빠지는지 확인**

Run: `./gradlew testStarter && grep -rl RoborazziPreviewParameterizedTests */build/test-results/testDebugUnitTest */*/build/test-results/testDebugUnitTest || echo NONE`
Expected: BUILD SUCCESSFUL, 마지막 줄 `NONE`.

- [ ] **Step 7: configuration cache 재사용에서도 같은지 확인**

Run: 같은 명령을 한 번 더 실행.
Expected: 출력에 `Reusing configuration cache.`, BUILD SUCCESSFUL, `NONE`.

- [ ] **Step 8: 기록 실행에서 세 모듈 PNG와 이름 안전성 확인**

Run:
```bash
./gradlew recordRoborazziDebug
./gradlew recordRoborazziDebug   # CC 재사용 확인
python3 - <<'EOF'
import sys; sys.path.insert(0, ".github/scripts/ui_diff")
from pathlib import Path, PurePosixPath
from report import is_safe_path
for m in ("core/designsystem", "feature/sample", "app-preview"):
    pngs = sorted(Path(m, "build/outputs/roborazzi").glob("*.png"))
    gen = [p for p in pngs if p.name.startswith("com.citytexi.pushlog.")]
    bad = [p.name for p in gen if not is_safe_path(PurePosixPath(p.as_posix()))]
    print(m, len(gen), "unsafe:", bad)
    assert gen and not bad and any("Light_font_2x" in p.name for p in gen)
EOF
ls feature/sample/build/test-results/roborazzi/debug/results | head -3
```
Expected: 두 번째 실행에 `Reusing configuration cache.`. 세 모듈 모두 생성 PNG 개수 > 0, `unsafe: []`. 생성 PNG가 `build/outputs/roborazzi/` 바로 아래에 있다. results 디렉터리에 JSON이 있다. 생성 파일명 접두사가 `com.citytexi.pushlog.`이 아니면 실제 이름을 보고 이 확인 스크립트의 접두사 조건만 고친다. Task 4 문서의 결과 경로도 실제 이름에 맞춘다.

- [ ] **Step 9: 커밋**

```bash
git add gradle/libs.versions.toml build-logic/convention/src core/ui/src
git commit -m "build: 프리뷰를 Roborazzi 스크린샷으로 자동 캡처"
```

### Task 2: `feature:sample` 직접 쓴 스크린샷을 프리뷰로 옮김

**Files:**
- Modify: `feature/sample/src/main/kotlin/com/citytexi/pushlog/feature/sample/SampleScreen.kt:104-114`
- Modify: `feature/sample/src/test/kotlin/com/citytexi/pushlog/feature/sample/SampleScreenScreenshotTest.kt`

**Interfaces:**
- Consumes: Task 1의 프리뷰 자동 캡처.

- [ ] **Step 1: 프리뷰 넷으로 교체**

기존 `SampleScreenPreview`를 지우고 private 함수 넷을 둔다. 모두 `@PushlogPreview`, `PushlogTheme { SampleScreen(state, remember { SnackbarHostState() }, onIntent = {}) }`.
- `SampleScreenEmptyPreview`: `SampleUiState()`
- `SampleScreenItemsPreview`: `SampleUiState(items = listOf(SampleItem(1, "First"), SampleItem(2, "Second"), SampleItem(3, "Third")))`
- `SampleScreenRefreshingPreview`: `SampleUiState(isRefreshing = true)`
- `SampleScreenRefreshingWithItemsPreview`: `SampleUiState(items = listOf(SampleItem(1, "First"), SampleItem(2, "Second")), isRefreshing = true)`

중복을 줄이는 private 헬퍼(`SampleScreenPreviewContent(state)`)는 구현자 판단.

- [ ] **Step 2: 테스트에서 옮긴 네 개 삭제**

`empty`, `items`, `refreshing`, `refreshingWithItems`를 지운다. `networkError`, `serverError`, `unknownError`와 `capture`는 남긴다. 클래스 KDoc을 바꾼다: 에러 스낵바는 `LaunchedEffect`로 띄워야 해서 프리뷰로 그릴 수 없으므로 여기서만 캡처하고, 나머지 상태는 `SampleScreen.kt`의 프리뷰가 자동 캡처된다. `SampleRoute`는 Hilt가 필요해 캡처하지 않는다는 기존 문장은 유지한다. 쓰지 않게 된 import는 지운다.

- [ ] **Step 3: 확인**

Run: `rm -rf feature/sample/build/outputs/roborazzi && ./gradlew :feature:sample:recordRoborazziDebug && ls feature/sample/build/outputs/roborazzi`
Expected: BUILD SUCCESSFUL. `sample_screen_error_{network,server,unknown}.png` 3개와 4개 프리뷰 × 3변형(`Light`, `Dark`, `Light_font_2x`)의 생성 PNG 12개. `sample_screen_empty.png` 같은 옛 이름은 없다.

- [ ] **Step 4: 커밋**

```bash
git add feature/sample/src
git commit -m "test: sample 화면 상태 캡처를 프리뷰로 옮김"
```

### Task 3: UI diff 코멘트에 버린 캡처 경고

**Files:**
- Modify: `.github/scripts/ui_diff/report.py`
- Test: `.github/scripts/ui_diff/test_report.py`

**Interfaces:**
- Produces:
  - `Report.unsafe_names: int = 0` (dataclass 마지막 필드, `images_before_cap` 뒤)
  - `collect_results(root: Path) -> tuple[list[CaptureResult], set[str]]` — 두 번째 값: 결과 JSON은 정상이고 `type`도 유효한데 golden 경로가 `split_golden`을 통과하지 못한 캡처의 `<module_dir>/build/outputs/roborazzi/<파일명>` 집합
  - `read_base_list(path: Path) -> tuple[set[str], set[str]]` — 두 번째 값: 비어 있지 않은데 `split_golden`을 통과하지 못한 줄의 집합
  - `report_to_dict`/`report_from_dict`에 `"unsafe_names"`(음이 아닌 `int`, 없으면 0, 아니면 `ValueError("invalid unsafe_names")`)
  - `cmd_collect`: `unsafe_names = len(head_unsafe | base_unsafe)`. `meta.status != "ok"`이면 0.
  - `classify`, `cap_images`는 `unsafe_names`를 그대로 넘긴다.

- [ ] **Step 1: 실패하는 테스트 작성** (`test_report.py`)

```python
def test_collect_counts_unsafe_names_once(self):
    # head 결과 JSON 2개: 정상 "A"(added), 안전하지 않은 "B_200%"(added).
    # base 목록: 같은 "B_200%" 키와 다른 안전하지 않은 "C(x)" 키, 정상 "A" 키.
    # 손상된 JSON 1개("{not json")는 세지 않는다.
    ...
    entries = json.loads(out["entries"])
    self.assertEqual(entries["unsafe_names"], 2)
    self.assertEqual([e["name"] for e in entries["added"]], ["A"])

def test_render_warns_unsafe_names_without_listing_them(self):
    rep = Report([], [], [], [], unsafe_names=2)
    text = render_comment(rep, Meta(7, HEAD, BASE, "ok"), "o/r", IMAGE_SHA)
    self.assertIn("2개", text)
    self.assertIn("바뀐 화면 없음", text)   # total == 0 이어도 경고가 먼저 나온다
    self.assertNotIn("%", text)

def test_report_dict_round_trip_and_validation(self):
    rep = Report([], [], [], [], unsafe_names=3)
    self.assertEqual(report.report_from_dict(report.report_to_dict(rep)).unsafe_names, 3)
    self.assertEqual(report.report_from_dict({}).unsafe_names, 0)
    for bad in (-1, "3", True, 1.5):
        with self.assertRaises(ValueError):
            report.report_from_dict({"unsafe_names": bad})
```

`test_collect_counts_unsafe_names_once`의 준비 코드는 `test_collect_reads_artifact`와 같은 방식(`run_collect`)으로 쓴다.

- [ ] **Step 2: 실패 확인**

Run: `python3 -m unittest discover -s .github/scripts/ui_diff -p 'test_*.py'`
Expected: 새 테스트 3개 FAIL/ERROR, 기존 테스트 PASS.

- [ ] **Step 3: 구현**

`load_capture`가 "정상 JSON인데 이름이 안전하지 않음"을 None과 구분해 돌려주게 한다(방법은 구현자 판단). 경고 문구(고정): `안전하지 않은 파일 이름(영문·숫자·\`_\`·\`-\`·\`.\` 외 문자) 때문에 비교에서 뺀 이미지: {N}개. 프리뷰 이름을 바꾸세요.` 위치: `failed_modules` 줄 바로 뒤, `total == 0` 조기 반환 앞. `N == 0`이면 아무것도 쓰지 않는다. 기존 호출부(`cmd_collect`, 기존 테스트)를 새 반환형에 맞춘다.

- [ ] **Step 4: 통과 확인**

Run: `python3 -m unittest discover -s .github/scripts/ui_diff -p 'test_*.py'`
Expected: 전부 PASS.

- [ ] **Step 5: 커밋**

```bash
git add .github/scripts/ui_diff/report.py .github/scripts/ui_diff/test_report.py
git commit -m "ci: UI diff 코멘트에 이름 때문에 뺀 이미지 수 경고"
```

### Task 4: 문서와 완료 게이트

**Files:**
- Modify: `docs/architecture/build-and-release.md` (검증 절)
- Modify: `docs/architecture/design-system.md` (글꼴 절 뒤에 "프리뷰 스크린샷" 절 추가)

- [ ] **Step 1: `build-and-release.md`**

`verifyStarter` 설명에 추가: `recordRoborazziDebug`는 직접 쓴 스크린샷과 함께 `@Preview` 자동 캡처(생성 테스트)도 돌린다. 생성 테스트는 실행 그래프에 Roborazzi 기록·비교 태스크가 있을 때만 돌고, 그래서 `testStarter`와 CI `test`에서는 빠진다. 이유: 프리뷰 수에 비례해 느려지는 것을 빠른 검사에서 빼고, 렌더 오류는 완료 게이트에서 잡는다. Roborazzi 실행에서는 일반 단위 테스트도 함께 돈다(`verifyStarter`가 섞어 부르기 때문).

- [ ] **Step 2: `design-system.md`**

새 절에 적을 것(코드로 복원되지 않는 것만):
- `starter.android.screenshot` 모듈의 namespace 아래 프리뷰(private 포함)가 자동 캡처되고 UI diff에 나온다. 결과는 `<module>/build/outputs/roborazzi/com.citytexi.pushlog.*.png`.
- 직접 쓴 스크린샷 테스트는 프리뷰로 그릴 수 없는 상태(눌림, 애니메이션 시점, 특수 폭, `LaunchedEffect`로 띄우는 스낵바)에만 쓴다. 예: `SampleScreenScreenshotTest`, `FoundationCatalogScreenshotTest`.
- Robolectric에서 그릴 수 없는 프리뷰에는 `@ScreenshotExcluded`.
- 프리뷰 이름은 파일명이 된다. 영문·숫자·`_`·`-`·`.`·공백만 쓴다. 다른 문자가 있으면 UI diff에서 빠지고 코멘트에 경고가 남는다. `@PushlogPreview`의 200% 변형 이름이 `Light font 2x`인 이유가 이것이다.
- 위 "컴포넌트 화면" 절의 `@PushlogPreview` 설명 줄("라이트·다크·글자 크기 200%")은 그대로 둔다.

- [ ] **Step 3: 링크 검사와 완료 게이트**

Run: `python3 docs/script/check_links.py docs && ./gradlew verifyStarter`
Expected: 깨진 링크 0건, BUILD SUCCESSFUL. 포맷 오류는 `./gradlew ktlintFormat` 후 다시 실행.

- [ ] **Step 4: 커밋**

```bash
git add docs/architecture/build-and-release.md docs/architecture/design-system.md
git commit -m "docs: 프리뷰 자동 스크린샷 캡처 문서화"
```

## 참고: PR 본문에 적을 것

`ui-diff-comment.yml`은 `workflow_run`이라 `report.py`를 default 브랜치(`main`)에서 실행한다. Task 3의 경고는 다음 릴리스로 `main`에 들어간 뒤부터 코멘트에 보인다. 렌더·비교(`ui-diff.yml`, `stage.py`)는 PR 코드로 돌므로 생성 이미지 비교는 이 PR부터 동작한다.
