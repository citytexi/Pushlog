# docs 진입 허브

`docs/`는 이 앱의 **구현 문서**다. 무엇을 어떻게 만들었고 왜 그렇게 했는지를 남긴다.
제품 스펙·기획은 [`wiki/`](../wiki/CLAUDE.md)가 적는다 — 의존 방향은 구현 → 위키 단방향이다.

## 작업별로 먼저 읽을 문서

| 작업 | 문서 |
|---|---|
| 모듈을 추가하거나 의존을 바꾼다 | [architecture/module-structure.md](architecture/module-structure.md) |
| 화면(ViewModel·Route·Screen)을 만든다 | [architecture/state-management.md](architecture/state-management.md), [architecture/adding-feature.md](architecture/adding-feature.md) |
| Room·DataStore·SecureStore를 다룬다 | [architecture/data-layer.md](architecture/data-layer.md) |
| 빌드·검증·버전·배포 | [architecture/build-and-release.md](architecture/build-and-release.md) |
| Kotlin 주석·KDoc을 쓴다 | [code-conventions.md](code-conventions.md) |
| 설계·계획 | [superpowers/specs/](superpowers/specs), [superpowers/plans/](superpowers/plans) |
| 결정 이유 | [adr/README.md](adr/README.md) |

## 구성

| 경로 | 내용 |
|---|---|
| `index.md` | 이 문서 |
| `status.md` | 기능별 현재 상태. 덮어쓰기만 한다 |
| `log.md` | 점검·구조 변경의 시간순 기록. append-only |
| `architecture/` | 레이어별 구조 문서 |
| `adr/` | 아키텍처 결정 기록 |
| `superpowers/specs/` | 설계 스펙. 구현 완료분은 `archive/` |
| `superpowers/plans/` | 구현 계획. 구현 완료분은 `archive/` |
| `synthesis/open-questions.md` | 구현 미결의 단일 추적 장소 |
| `code-conventions.md` | Kotlin 주석·KDoc 규약 |
| `script/` | `check_links` `search`. 저장소 루트에서 실행 |
