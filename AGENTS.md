# AGENTS.md

LLM 도구(Claude Code, Cursor, Codex)와 팀원이 이 프로젝트에서 지킬 규칙이다. 코드를 바꾸기 전에 읽는다.
세부 규칙은 아래 문서들에 있다. 이 파일은 진입점과 꼭 지킬 것만 담는다.

- 앱: Pushlog — 서버 없이 기기 안에서 푸시 알림을 수집·저장·분류·정리하는 Android 앱
- 패키지 루트: `com.citytexi.pushlog`, 클래스 접두사: `Pushlog`
- 스택: Kotlin, Compose, Hilt, Room, DataStore, Navigation 3

## 언어

- 사용자 응답, 이 레포의 문서(`docs/`, `wiki/`, 이슈·PR 본문): **한국어**
- 코드, 식별자, 파일명, PR 제목: **영어**
- 커밋 메시지: `type: 한국어 설명` (예: `docs: 위키 골격 추가`)
- skill 파일(`SKILL.md`)은 영어로 쓴다. 고정 구조(헤딩, 프론트매터 키)는 영어로 두고 **내용**만 한국어로 쓴다.

## 문서 라우팅

작업 전에 해당 문서를 읽는다. 전체 지도는 [`docs/index.md`](docs/index.md)다.

| 작업 | 먼저 읽을 문서 |
|---|---|
| 모듈 추가, 의존 변경, 클래스 이름 | [`docs/architecture/module-structure.md`](docs/architecture/module-structure.md) |
| 화면(ViewModel·Route·Screen) | [`docs/architecture/state-management.md`](docs/architecture/state-management.md), [`docs/architecture/adding-feature.md`](docs/architecture/adding-feature.md) |
| Room, DataStore, `SecureStore` | [`docs/architecture/data-layer.md`](docs/architecture/data-layer.md) |
| 빌드, 검증, 버전, 배포, 서명 | [`docs/architecture/build-and-release.md`](docs/architecture/build-and-release.md) |
| 디자인 시스템 토큰·컴포넌트 | [`docs/architecture/design-system.md`](docs/architecture/design-system.md) |
| Kotlin 주석·KDoc | [`docs/code-conventions.md`](docs/code-conventions.md) |
| 제품 스펙·기획 판단 | [`wiki/CLAUDE.md`](wiki/CLAUDE.md)를 먼저 읽고 `python3 wiki/script/route.py --intent <의도>`로 시작 |
| 앱 전체 설계 | [`docs/superpowers/specs/2026-11-13-pushlog-design.md`](docs/superpowers/specs/2026-11-13-pushlog-design.md) |

구현·리뷰를 서브에이전트에 맡길 때는 `docs/code-conventions.md`를 브리프에 링크한다.

## 문서 (`docs/`, `wiki/`)

- `docs/`는 **구현 문서**다. 무엇을 어떻게 만들었고 왜 그렇게 했는지 남긴다.
- `wiki/`는 **제품 스펙·기획**이다. 무엇을 만들기로/만들지 않기로 했고 그 판단이 언제 왜 바뀌었는지 남긴다. 의존 방향은 `docs/` → `wiki/` 단방향이다.
- 코드로 복원할 수 없는 것만 적는다. 코드에서 바로 나오는 사실, 변경 이력 나열은 적지 않는다.
- 구현 미결은 `docs/synthesis/open-questions.md`, 기획 미결은 `wiki/open-questions.md` 한 곳에서만 추적한다.
- 문서를 옮긴 뒤 `python3 docs/script/check_links.py docs`, 위키를 고친 뒤 `python3 wiki/script/lint.py`를 돌려 0건을 확인한다.

## 검증

변경을 끝내기 전에 반드시 실행한다.

```bash
./gradlew verifyStarter
```

작업 중에는 `./gradlew testStarter`(유닛 테스트, Konsist, `ModuleRulesTest`, detekt, ktlint)를 돌린다. 포맷 오류는 `./gradlew ktlintFormat`으로 고친다. 상세는 [`build-and-release.md`](docs/architecture/build-and-release.md).

## 금지 사항

- `feature`에서 `data`, `core:network`, `core:database`, `core:datastore`를 의존하거나 import하지 않는다. UI가 저장소 구현에 묶이면 교체와 테스트가 어려워진다.
- `domain`에 Android/androidx 타입을 쓰지 않는다. 순수 JVM이어야 단위 테스트가 빠르다.
- `ModuleRules`나 Konsist 테스트를 약화하거나 삭제하지 않는다. 규칙이 막으면 구조를 고친다.
- `@Suppress`, detekt baseline, ktlint 비활성화로 검사를 우회하지 않는다. 이유 없이 린트가 통과한 것처럼 보이게 된다.
- ViewModel에서 Repository 구현이나 `Context`를 직접 쓰지 않는다. UseCase를 거친다.
- 비밀값(토큰, 키스토어 비밀번호, API 키)을 코드, `gradle.properties`, 커밋에 넣지 않는다. 키스토어 값은 `local.properties` 또는 환경변수로만 준다.
- 토큰 같은 민감 값을 평문 DataStore나 SharedPreferences에 저장하지 않는다. `SecureStore`를 쓴다.
- 기준 스크린샷과 `local.properties`, 키스토어 파일을 커밋하지 않는다.
- `verifyStarter`를 건너뛰고 완료를 선언하지 않는다. 일부 모듈만 돌려서는 모듈 간 규칙 위반을 놓친다.
- 알림 원문·발신자·대화방 이름을 로그, 예외 메시지, Crashlytics, Analytics에 넣지 않는다. 사용자 약속은 "알림 내용은 폰 밖으로 나가지 않는다"이다.
