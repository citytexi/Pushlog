# Architecture

Pushlog의 구조·설계 명세 문서를 모읍니다. (모듈 구조, 상태관리 패턴, 데이터 계층, 빌드·배포 등)

> **참고** — 모든 주장은 **파일명 + 심볼명(클래스/함수/프로퍼티)** 으로 근거를 표시하고, 추정은 **Assumption** 라벨을 붙입니다.
>
> **⚠ 라인번호와 수치(파일 수·진행률·사용 횟수)는 적지 않습니다** — 커밋마다 바뀌어 금방 거짓이 됩니다.
>
> `architecture/`는 "어떻게/어디"(구현 가이드)를, `adr/`는 "왜"(결정·대안)를 다룹니다. 상호 보완입니다.

| 문서 | 내용 |
|------|------|
| [module-structure.md](module-structure.md) | 모듈 목록·의존 규칙(`ModuleRules`, Konsist)·데이터 흐름·네이밍 |
| [state-management.md](state-management.md) | MVI 컨테이너(`UiState`/`UiIntent`/`UiSideEffect`)·화면 작성 규칙·테스트 |
| [adding-feature.md](adding-feature.md) | 새 feature 모듈 추가 절차·nav 등록·문구·테스트 |
| [data-layer.md](data-layer.md) | 데이터 소스 추가(network/database/datastore)·Room 마이그레이션·`SecureStore` |
| [build-and-release.md](build-and-release.md) | `testStarter`/`verifyStarter`·앱 버전 규칙·release 브랜치·서명 |

## Frontmatter (필수)
모든 architecture 문서는 YAML frontmatter를 단다(형식 권위: [`template.md`](template.md)). 필드: `id` · `title` · `category`(=architecture) · `status`(**living / superseded / deprecated**) · `platforms`(=android) · `verified`(코드 대조일) · `related_spec` · `related_adr` · `related_architecture` · `related_code`(심볼명, 라인번호 금지) · `tags`.
