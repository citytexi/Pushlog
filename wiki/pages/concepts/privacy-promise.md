---
tags: [feature, scope]
sources: [src-competitor-play-reviews-2026-11-13, src-pushlog-planning-decisions-2026-11-13]
updated: 2026-11-13
---

# 프라이버시 약속

Pushlog가 사용자에게 하는 약속은 **"알림 내용은 폰 밖으로 나가지 않는다"**이다.

## 결정

- 크래시 보고(Crashlytics)와 행동 분석(GA) 때문에 `INTERNET` 권한은 쓴다. 그래서 "인터넷 권한 없음"이 아니라 "알림 내용"으로 약속 범위를 좁혔다.
- 크래시 보고는 기본, 행동 분석은 동의한 경우에만.

## 왜

- NotiFold가 기기 내 저장·무계정을 전면에 내세운다.
- 노티세이브에는 "내 알림을 DB로 전송하나"는 의심 리뷰가 있다.

## 미결

- 수익 모델을 다시 넣을 때 이 약속 문구를 어떻게 바꿀지 → [[open-questions]]

## 연관
- [[notifold]] · [[notisave]]
- [[business-model]] · [[collection-reliability]]
- [[src-competitor-play-reviews-2026-11-13]] · [[src-pushlog-planning-decisions-2026-11-13]]
