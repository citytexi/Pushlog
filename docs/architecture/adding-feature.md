---
id: adding-feature
title: 새 feature 추가 절차
category: architecture
status: living
platforms: android
verified: 2026-11-13
related_spec:
related_adr:
related_architecture:
related_code: [SampleViewModel, PushlogNavHost, PushlogNavKeys]
tags: [architecture, feature]
---

# 새 feature 추가 절차

1. `feature/sample`을 `feature/<name>`으로 복사한다. `sample`을 `<name>`으로 바꾼다(디렉터리, 패키지 `com.citytexi.pushlog.feature.<name>`, `namespace`, 클래스 이름).
   - `build.gradle.kts`는 플러그인 `starter.android.feature`(library + compose + hilt)와 `starter.android.screenshot`을 쓰고 `namespace = "com.citytexi.pushlog.feature.<name>"`로 둔다.
   - 이름을 바꾸면 `domain`에 없는 `Sample*` 타입을 참조하는 파일이 깨진다: `SampleViewModel`, `SampleUiState`, `SampleScreen`, `SampleViewModelTest`, `SampleScreenScreenshotTest`(`domain.model.SampleItem`, `Observe/Refresh/...SampleUseCase`, `FakeSampleRepository` 등). 규칙: `domain.*Sample*` import를 모두 새 feature의 타입으로 다시 쓰거나 지운다. 데이터가 필요 없으면 해당 파일과 의존(`domain`)을 비운다.
2. `settings.gradle.kts`에 `include(":feature:<name>")`을 추가한다. `app/build.gradle.kts`에도 `implementation(project(":feature:<name>"))`을 추가한다.
3. 데이터가 필요하면 아래 순서로 만든다.
   - `domain`: 모델, `<Name>Repository` 인터페이스(실패 시 예외), `<Action><Name>UseCase`(`operator fun invoke`. suspend면 `Result<T>` 반환, `runSuspendCatching`으로 감쌈)
   - `data`: `<Name>RepositoryImpl`, `DataModule`에 `@Binds` 추가
   - `feature`: `@HiltViewModel` ViewModel은 UseCase만 주입받는다. Repository를 직접 주입하지 않는다.
   - 새 데이터 소스(API, DB, 설정)는 [data-layer.md](data-layer.md)의 "데이터 소스 추가"를 따른다.
   - 오류 표시: `data`는 Retrofit `HttpException`을 `mapHttpErrors { }`로 `HttpStatusException`(`IOException`)으로 바꾼다. ViewModel은 UseCase가 돌려준 `Result`의 실패(`onFailure`) 예외를 `SampleError` 같은 타입으로만 바꾸고(`HttpStatusException`이면 서버, 그 밖의 `IOException`이면 네트워크, 나머지는 알 수 없음. `HttpStatusException`이 `IOException`의 하위 타입이라 먼저 검사한다) 문구는 들고 있지 않는다. 화면 쪽이 문구를 고른다(sample은 `SampleSideEffect.ShowError`를 Route가 스낵바로 보여 준다).
4. nav 등록. **먼저 프로젝트가 쓰는 variant를 확인한다.** `core/navigation/build.gradle.kts`에 `navigation-compose`(`androidx.navigation.compose`)가 있으면 nav2, `navigation3`가 있으면 nav3다.
   - 시작 화면은 nav2는 `PushlogNavHost`의 `startDestination = ...`, nav3는 `rememberNavBackStack(...)`에서 정한다.

   **nav2**
   - `core/navigation/.../PushlogRoutes.kt`에 `@Serializable data object <Name>NavRoute`를 추가한다.
   - `feature/<name>/.../navigation/<Name>Navigation.kt`에 `fun NavGraphBuilder.<name>Screen() { composable<<Name>NavRoute> { <Name>Route() } }`를 만든다.
   - `app/.../navigation/PushlogNavHost.kt`의 `NavHost { }` 블록에서 `<name>Screen()`을 호출한다.

   **nav3**
   - `core/navigation/.../PushlogNavKeys.kt`에 `@Serializable data object <Name>Key : NavKey`를 추가한다.
   - `feature/<name>/.../navigation/<Name>Navigation.kt`에 `fun EntryProviderScope<NavKey>.<name>Entry() { entry<<Name>Key> { <Name>Route() } }`를 만든다.
   - `app/.../navigation/PushlogNavHost.kt`의 `entryProvider { }` 블록에서 `<name>Entry()`를 호출한다.

5. 사용자에게 보이는 문구는 `feature/<name>/src/main/res/values/strings.xml`에 두고(sample은 한국어) `stringResource`로 쓴다. 스크린샷 테스트가 깨지지 않게 문구와 함께 갱신한다.
6. 테스트를 쓴다(ViewModel 단위 테스트, 화면 스크린샷 테스트는 `SampleViewModelTest`, `SampleScreenScreenshotTest` 참고). UseCase는 mock하지 않는다. `domain/src/testFixtures`의 `FakeSampleRepository`처럼 가짜 Repository를 만들고 실제 UseCase에 넣는다. feature는 `testImplementation(testFixtures(project(":domain")))`로 받는다. 작업 중에는 `./gradlew testStarter`, 마무리는 `./gradlew verifyStarter`로 한다.
