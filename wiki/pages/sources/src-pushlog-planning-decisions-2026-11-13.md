---
tags: [planning, scope, decision]
sources: [pushlog-planning-decisions-2026-11-13.md]
updated: 2026-11-13
status: current
---

# Pushlog 초기 기획 결정 (2026-11-13)

## 요약

서버 없는 Android 푸시 알림 정리·저장·기록·관리 앱 Pushlog의 전체 기능 지도를 정한 결정 메모다. 차별점은 **설치 이후 영구 기록**과 **사용자 커스텀 분류** 두 가지이고, 단톡방 묶기는 기본 기능으로 둔다. 수익 모델은 프라이버시 메시지와의 충돌로 이번 기획에서 뺐다. MVP 범위는 아직 자르지 않았다.

## 핵심 주장

- 차별점 1: 설치 이후 모든 알림 기록을 남기고 검색·복사·원래 앱 이동을 제공한다 → [[notification-history]]
- 차별점 2: 사용자가 분류 기준을 커스텀한다. 기본 분류(예: 결제)에 앱(예: 네이버페이)을 더할 수 있다. 방식은 태그 → [[tag-classification]]
- 단톡방 이름 단위 묶기는 차별점이 아니라 기본 기능 → [[conversation-grouping]]
- 알림창 정리는 설정에서 "기록만" / "알림창 정리까지"를 고르는 선택 모드 → [[tray-cleanup]]
- 결제·배송·쿠폰·AI 정보 추출을 넣고, 추출 규칙은 앱에 내장한다 → [[info-extraction]]
- 규칙을 바꾸면 과거 기록에도 적용된다 → [[tag-classification]], [[info-extraction]]
- 삭제는 가리기 → 30일 안 복구 → 30일 뒤 완전 삭제 → [[record-deletion]]
- `INTERNET` 권한을 쓰고(Crashlytics·GA), 약속은 "알림 내용은 폰 밖으로 나가지 않는다"로 좁힌다 → [[privacy-promise]], [[collection-reliability]]
- 광고 + 광고 제거 판매 구상은 이번 기획에서 제외 → [[business-model]]
- 경쟁작은 [[notisave]], [[notistory]], [[notifold]]

## 미결

- MVP 범위 → [[open-questions]]
- BM을 다시 넣을 때 프라이버시 약속 문구 → [[open-questions]]

## 연관
- [[src-competitor-play-reviews-2026-11-13]]
