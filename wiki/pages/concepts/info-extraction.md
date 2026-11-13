---
tags: [feature, scope]
sources: [src-competitor-play-reviews-2026-11-13, src-pushlog-planning-decisions-2026-11-13]
updated: 2026-11-13
---

# 정보 추출 (결제·배송·쿠폰·AI 질의)

알림 내용에서 결제 금액, 배송 단계, 쿠폰 만료일 같은 정보를 꺼내 모아 보여 주고, 지원 기기에서는 AI 질의로 답하는 기능. Pushlog에 **넣기로 했다**.

## 결정

- 추출 규칙은 **앱에 내장**한다. Remote Config로 받는 방식은 택하지 않았다.
- 규칙이 바뀌면 과거 기록에도 적용된다.

## 왜

신규 경쟁작 NotiFold가 이미 금융·배송·쿠폰·기기 내 AI를 제공한다. 이 영역의 기능 확장을 먼저 한 쪽이 NotiFold다.

## 연관
- [[notifold]]
- [[tag-classification]]
- [[src-competitor-play-reviews-2026-11-13]] · [[src-pushlog-planning-decisions-2026-11-13]]
