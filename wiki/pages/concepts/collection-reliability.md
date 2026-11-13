---
tags: [feature, scope]
sources: [src-competitor-play-reviews-2026-11-13, src-pushlog-planning-decisions-2026-11-13]
updated: 2026-11-13
---

# 수집 안정성

알림 수집이 끊기지 않고 모든 앱을 빠짐없이 기록하는 품질. 경쟁 앱 리뷰의 **1위 불만**이다.

## 왜 중요한가

- 노티스토리: "되다 안 되다 함"(50번 끊겼다는 리뷰), 일부 앱만 저장, 카카오톡 업데이트 후 누락.
- 노티세이브: 앱이 작동 안 함, 설정이 자꾸 풀림, 제조사 OS 업데이트 후 오동작.

## 결정과의 연결

수집이 어디서 끊기는지 기기·제조사별로 알아야 고칠 수 있어서 Crashlytics·GA를 붙이기로 했다 — [[privacy-promise]]의 범위를 "알림 내용"으로 좁힌 이유다.

## 연관
- [[notistory]] · [[notisave]]
- [[privacy-promise]]
- [[src-competitor-play-reviews-2026-11-13]] · [[src-pushlog-planning-decisions-2026-11-13]]
