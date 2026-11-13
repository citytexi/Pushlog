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

## [2026-11-13] schema-change | 개념 그래프(graphify) 제거, 키워드 검색으로 대체

- `graph_signals.py`와 그 테스트, `lint.py`의 그래프 신호 4종(`그래프stale` `그래프고립` `희소커뮤니티` `브리지`)을 뺐다
- 관계 탐색은 `[[링크]]`, `route.py` 출처 중복 확장, `docs/script/search.py --scope wiki`로 한다
- 근거: 작은 저장소에서 그래프 도구가 단순 텍스트 검색 대비 찾는 비율을 올리지 못하고 비용과 재생성 부담만 늘었다는 측정 사례를 참고했다

## [2026-11-13] ingest | Pushlog 브랜드 색 결정

- 배치 모드. 원본 1건: `wiki/raw/pushlog-brand-color-decision-2026-11-13.md`(오너 승인 하에 대화 결정을 정리)
- 생성: sources 1, concepts 1(`brand-color`). 갱신: entities 3(경쟁 앱 아이콘 색), overview, index
- 판본 관계·기존 위키와의 모순 없음. 미결 추가·해소 없음
