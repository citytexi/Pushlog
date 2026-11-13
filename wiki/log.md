# 작업 로그

append-only다. 앞 항목을 고치지 않는다.

## [2026-11-13] setup | 위키 체계 도입

- 단일 도메인 평탄 구조로 위키 골격을 만들었다. 데이터 계약은 `wiki/conventions.md`,
  운영 절차는 `wiki/CLAUDE.md`
- 스크립트 6개: `route` `lint` `check_status` `ingest_cache` `wikilib` `graph_signals`

## [2026-11-13] ingest | Pushlog 초기 기획 결정 · 경쟁 앱 Play 리뷰 조사

- 배치 모드. 원본 2건: `wiki/raw/pushlog-planning-decisions-2026-11-13.md`(오너 승인 하에 대화 결정을 정리), `wiki/raw/research/competitor-play-reviews-2026-11-13.md`(Play 스토어 조사)
- 생성: sources 2, entities 3(`notisave` `notistory` `notifold`), concepts 9
- 판본 관계·기존 위키와의 모순 없음(첫 원본). 미결 2건 등록(MVP 범위, BM 재도입 시 약속 문구)
