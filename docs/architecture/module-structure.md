---
id: module-structure
title: 모듈 구성·의존 규칙·네이밍
category: architecture
status: living
platforms: android
verified: 2026-11-13
related_spec:
related_adr:
related_architecture:
related_code: [ModuleRules, PushlogDatabase]
tags: [architecture, modules]
---

# 모듈 구성·의존 규칙·네이밍

## 모듈 구성

| 모듈 | 종류 | 역할 |
|---|---|---|
| `:app` | Android | `MainActivity`, Hilt Application, 앱 전체 nav 조립(`PushlogNavHost`) |
| `:feature:sample` | Android | 예시 화면. Route, Screen, ViewModel, UiState/Intent/SideEffect. 새 feature의 복사 원본 |
| `:domain` | JVM | 모델, `*Repository` 인터페이스, `*UseCase`. Android 의존 금지 |
| `:data` | Android | `*RepositoryImpl`, Hilt 바인딩(`DataModule`) |
| `:core:designsystem` | Android | 테마, 공용 컴포넌트 |
| `:core:ui` | Android | feature 공용 UI 유틸, MVI 컨테이너(`core.ui.mvi`) |
| `:core:navigation` | Android | route/key 정의. nav 라이브러리를 `api`로 노출 |
| `:core:network` | Android | Retrofit, OkHttp, `SampleApi`, `BuildConfig.BASE_URL` |
| `:core:database` | Android | Room. 스키마를 바꿀 때는 `PushlogDatabase` KDoc(버전, AutoMigration)과 `PushlogDatabaseMigrationTest`를 따른다 |
| `:core:datastore` | Android | DataStore 설정, `SecureStore` |
| `:core:common` | JVM | 공용 타입과 함수(`runSuspendCatching`, `HttpStatusException` 등), dispatcher qualifier와 Hilt 바인딩(`DispatcherModule`, JVM용 `hilt-core`) |
| `:core:testing:jvm` | JVM | JVM 테스트 도구 |
| `:core:testing:android` | Android | 계측/Robolectric 테스트 도구 |
| `:konsist` | JVM | 아키텍처 규칙 테스트(소스 경로만 읽음) |

JVM 모듈은 `domain`, `core:common`, `core:testing:jvm`, `konsist`이고 나머지는 Android다.

## 의존 규칙

Gradle `build-logic`의 `ModuleRules`가 모든 모듈의 프로젝트 의존을 검사하고 어기면 빌드가 실패한다. `implementation`/`api`뿐 아니라 직접 만든 구성과 `extendsFrom`으로 이어진 구성, `afterEvaluate`에서 추가한 의존도 검사한다. test 구성(`test`, `androidTest`, `testFixtures` 등 이름에 `test`/`Test` 단어가 있는 구성)은 검사하지 않는다. 대신 test 구성이 아닌 곳에서 `testFixtures(project(...))`를 요청하면(`implementation(testFixtures(project(":domain")))` 등) 모듈 표와 관계없이 실패한다.

| 모듈 | 의존해도 되는 모듈 |
|---|---|
| `feature:*` | `domain`, `core:designsystem`, `core:ui`, `core:navigation`, `core:common` |
| `domain` | `core:common` |
| `data` | `domain`, `core:network`, `core:database`, `core:datastore`, `core:common` |
| `core:designsystem`, `core:ui`, `core:navigation` | 이 셋과 `core:common`만(UI core는 `network`, `database`, `datastore`에 의존하지 못함) |
| 그 밖의 `core:*` | 다른 `core:*`만. `core:testing:*`은 test/androidTest 구성에서만 쓰고, 자신은 아무 `core:*`에나 의존할 수 있음 |
| `app` | 전부 |
| `konsist` | 없음 |

`feature:*` → `core:common`은 설계 문서의 목록에는 없지만 허용한다. `domain`이 `core:common`을 `api`로 노출하고, ViewModel이 dispatcher qualifier를 주입받을 수 있어야 하기 때문이다.

Konsist(`konsist/src/test`)가 import도 검사한다.

- `feature`는 `com.citytexi.pushlog.data`, `core.network`, `core.database`, `core.datastore` 패키지를 import하지 않는다(와일드카드 포함).
- `domain`은 `android`, `androidx`를 import하지 않는다.
- production 소스의 kt 파일은 최상위 `class`/`interface`/`object`/`enum`/`annotation class`를 최대 1개만 선언한다. 파일 이름은 타입 이름으로 짓는다. 중첩 선언과 최상위 함수·값은 세지 않고, 예외는 없다.
- ViewModel의 `MutableMviContainer` 프로퍼티는 `private`이어야 한다([state-management.md](state-management.md) 참고).

데이터는 항상 **Repository → UseCase → ViewModel** 순서로 흐른다. Konsist가 강제한다.

- Repository(`domain`의 인터페이스, `data`의 구현)는 실패하면 예외를 던진다. `Result`를 반환하지 않는다.
- UseCase의 `suspend operator fun invoke`는 반환 타입을 `Result<T>`로 명시하고 `runSuspendCatching { }`(`core:common`)으로 감싼다. Repository를 그대로 부르기만 하는 경우에도 UseCase를 만든다.
- ViewModel은 UseCase만 주입받는다. Repository를 주입하지 않는다. 결과는 `onSuccess`/`onFailure`로 처리한다.
- `runSuspendCatching`은 `CancellationException`을 다시 던진다(stdlib `runCatching`은 취소를 실패로 바꿔 버린다). 블록에 suspend 호출이 없으면 stdlib `runCatching`을 쓴다.

## 네이밍 규칙

Konsist가 강제하는 규칙이다. 어기면 `testStarter`가 실패한다.

| 대상 | 위치 |
|---|---|
| `*ViewModel` | `feature` 또는 `app`(`MainViewModel`) |
| `*Repository` 인터페이스 | `domain` |
| `*RepositoryImpl` | `data`. `domain`의 Repository를 구현해야 함 |
| `*UseCase` | `domain`. `operator fun invoke`를 가져야 함 |

관례(검사하지 않음): nav 파일 이름은 `PushlogRoutes`/`PushlogNavKeys`처럼 접두사를 쓰고, feature의 화면 composable은 `*Route`, `*Screen`으로 짓는다.
