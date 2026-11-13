---
id: build-and-release
title: 빌드·검증·버전·release 서명
category: architecture
status: living
platforms: android
verified: 2026-11-14
related_spec:
related_adr:
related_architecture:
related_code: [AndroidConfig, configureAndroidApplication]
tags: [architecture, build]
---

# 빌드·검증·버전·release 서명

## 검증

변경을 끝내기 전에 반드시 실행한다.

```bash
./gradlew verifyStarter
```

task는 두 개다. 모듈에 없는 task는 건너뛴다.

- `testStarter`: 작업 중 자주 돌리는 빠른 검사. CI의 `test` workflow가 이것을 돌린다.
  - Android: `testDebugUnitTest`, `assembleDebugAndroidTest`(컴파일만)
  - JVM: `test` (`:konsist` 포함, 아키텍처 규칙)
  - `build-logic`: `:convention:test` (`ModuleRulesTest`)
  - 전체: `detekt`, `ktlintCheck`
- `verifyStarter`: 끝내기 전의 완료 게이트. `testStarter` 전부에 더해 Android 모듈의 `assembleDebug`, `assembleRelease`, `recordRoborazziDebug`를 실행한다. `:app-preview`는 debug 전용이라 release 변형이 없어 `assembleRelease`에서 빠진다.

`recordRoborazziDebug`는 직접 쓴 스크린샷 테스트와 함께 `@Preview` 자동 캡처(Roborazzi가 만드는 생성 테스트)도 돌린다. 생성 테스트는 실행 그래프에 Roborazzi 기록·비교 태스크(`record`/`compare`/`verify`/`verifyAndRecord` + `Roborazzi<Variant>`)가 있을 때만 돈다. 그래서 `verifyStarter`와 CI `ui-diff`에서는 돌고 `testStarter`와 CI `test`에서는 빠진다.

- 이유: 프리뷰 수에 비례해 느려지는 렌더를 빠른 검사에서 빼고, 렌더 오류는 완료 게이트에서 잡는다.
- 명령줄 태스크 이름이 아니라 실행 그래프로 판정한다. `verifyStarter`는 `recordRoborazziDebug`를 이름 없이 끌어오기 때문이다.
- Roborazzi 실행에서도 일반 단위 테스트는 함께 돈다. `verifyStarter`가 둘을 한 실행에 부르므로 어느 쪽도 뺄 수 없다. 그래서 UI diff CI에서 단위 테스트가 실패하면 그 모듈은 렌더 실패로 보고된다.

포맷 오류는 `./gradlew ktlintFormat`으로 고친다. 기준 스크린샷은 커밋하지 않는다.

Gradle configuration cache가 켜져 있다(`gradle.properties`). 빌드 스크립트에서 설정 시점에 파일이나 환경변수를 읽으면 캐시 오류가 날 수 있다.

## 앱 버전

`versionCode`와 `versionName`은 `gradle/libs.versions.toml`의 `[versions]`에 있고 `AndroidConfig.kt#configureAndroidApplication`이 읽는다.

- `versionName` 형식은 `yyyy.MM.ddvNN`이다. 배포 날짜 + 같은 날 몇 번째 빌드인지(01부터). 예: `2026.11.13v01`.
- 배포할 때마다 `versionCode`를 1 올린다.
- 릴리스는 `release/<versionName>` 브랜치를 `develop`에서 따서 `main`으로 PR을 올리고 squash merge한다. `<versionName>` 태그는 merge 뒤 `main`의 squash 커밋에 단다. release 브랜치 끝 커밋은 squash merge로 `main`에 들어가지 않으므로 거기에 단 태그는 `main` 이력 밖을 가리킨다.
- squash 커밋은 `develop`과 이력을 공유하지 않는다. 릴리스마다 `main`을 `develop`에 merge commit으로 합쳐야 다음 릴리스 PR이 충돌하지 않고, 올린 버전도 `develop`에 들어간다.
- 명령 순서는 `.claude/skills/git-flow/SKILL.md`의 Release flow를 따른다.

## release 서명

`release`는 아래 4개 값이 모두 있으면 해당 키스토어로 서명한다. 하나도 없으면 debug 키로 서명하고, `assembleRelease`/`bundleRelease`를 실행할 때 경고를 출력한다(그 빌드는 업로드하지 않는다). 일부만 있으면 빠진 키 이름을 알려 주며 빌드가 실패한다. 값은 루트 `local.properties`와 환경변수에서 읽고, 둘 다 있으면 `local.properties`가 우선한다.

| 이름 | 의미 |
|---|---|
| `STARTER_KEYSTORE_PATH` | 키스토어 파일 경로(프로젝트 루트 기준) |
| `STARTER_KEYSTORE_PASSWORD` | 키스토어 비밀번호 |
| `STARTER_KEY_ALIAS` | 키 alias |
| `STARTER_KEY_PASSWORD` | 키 비밀번호 |
