---
id: design-system-pressable
title: 디자인 시스템 동작 컴포넌트 공통 규칙
category: architecture
status: living
platforms: android
verified: 2026-11-17
related_spec: [docs/superpowers/specs/2026-11-13-design-system-action-components-design.md, docs/superpowers/specs/2026-11-16-design-system-selection-controls-design.md]
related_adr:
related_architecture: [design-system]
related_code: [PushlogPressableSurface, PressScaleScope, PressableSelectionMode]
tags: [architecture, design-system]
---

# 디자인 시스템 동작 컴포넌트 공통 규칙

눌리는 컴포넌트 모두가 따르는 규칙과 `pressable` 부품을 다룬다. 결정 배경은 [동작 컴포넌트 스펙](../../superpowers/specs/2026-11-13-design-system-action-components-design.md), [선택 컨트롤 스펙](../../superpowers/specs/2026-11-16-design-system-selection-controls-design.md). 컴포넌트별 결정은 [components/](README.md) 아래 문서에 있다.

## 공통 규칙

- 컴포넌트는 `component` 아래 종류별 하위 패키지에 둔다. `button`(액션 버튼, 플로팅 버튼, 둘이 함께 쓰는 `PushlogButtonSupport.kt`), `chip`, `loading`, `pressable`(눌림 부품, 모두 `internal`)이다. `component` 바로 아래에 두면 Konsist `ComponentRulesTest`가 막는다.
- 크기·variant에 따라 갈리는 값은 그 enum의 `internal` 멤버로 둔다. 크기는 `PushlogXxxSize.metrics()`가 `XxxMetrics`(같은 패키지, `Pushlog` 접두사 없이 값만 담는 `internal class`, 예: `PushlogChipSize` → `ChipMetrics`)를 돌려주고, 색은 `PushlogXxxVariant.surfaceColor(...)`·`contentColor(...)`처럼 variant가 답한다. 컴포넌트 파일에는 그리는 코드와 거기서만 쓰는 상수만 남긴다. 멤버는 `else`·`==` 없이 `when (this)`로 모든 항목을 적는다. 그래야 새 항목을 추가했을 때 컴파일러가 빠진 곳을 알려 준다.
- `XxxDefaults` object는 호출하는 쪽이 값을 바꿀 수 있게 파라미터를 열 때만 만든다(예: 나중에 `colors = PushlogChipDefaults.colors(...)`처럼 열 때). 컴포넌트 안에서만 쓰는 값은 파일 안 `private` 상수로 둔다. `Defaults`라는 이름은 바꿔도 되는 공개 값으로 읽히고, 공개하면 값 변경이 API 변경이 된다.
- 새 동작 컴포넌트는 표면을 새로 만들지 않고 `PushlogPressableSurface`(눌림 색·축소, 클릭 또는 토글, 테두리, 그림자, 로딩 의미 정보)에 내용을 채운다. 탭 영역과 축소 대상이 다른 컨트롤(체크박스·라디오 줄)은 `pressable`의 탭 영역 modifier와 눌림 표시 modifier를 같은 `interactionSource`로 묶는다. `pressable` 밖에서 `clickable`·`toggleable`·`selectable`·`indication`·`pointerInput`·ripple을 직접 쓰거나 Icon·Text·진행 표시 외의 Material 3 컴포넌트를 쓰면 `ComponentRulesTest`가 막는다. 눌림 모양과 로딩 규칙이 컴포넌트마다 갈라지지 않게 하려는 것이다. 버튼류는 `PushlogButtonSupport.kt`의 로딩 내용·색 도우미를 함께 쓴다. 크기와 접근성 이름은 `modifier`로 넘긴다.
- ripple을 쓰지 않는다. 눌림은 표면 색 교체와 2dp 축소로만 보인다. 축소 기준 길이는 max(높이, 너비÷4, 24dp)다. 글자·아이콘 색은 그대로다.
- 축소는 그림자까지 요소 전체를 줄인다(SEED `scaleScope: self`). 그래서 그림자는 `PushlogPressableSurface`의 `shadow` 인자로 넘겨 축소 층 안에 그린다. modifier에 따로 두면 표면만 줄고 그림자는 그대로 남는다. 탭 판정 영역은 줄이지 않는다. 판정 영역까지 줄면 가장자리를 누른 손가락이 줄어든 표면 밖으로 밀려나 탭이 취소된다(`edgeTapSurvivesShrink`).
- 로딩은 비활성으로 읽히지 않는다. 의미 정보에는 `enabled`가 남고 클릭만 삼킨다. 스크린 리더가 "사용 불가"로 안내하면 곧 다시 쓸 수 있는 버튼이 막힌 것처럼 들린다.
- SEED의 px 값은 같은 크기의 토큰으로 옮긴다(14→`x3Half`, 16→`x4`, 32→`x8` 등). 토큰이 없는 22dp, 44dp, 1dp(칩 테두리)와 progress 애니메이션 값만 파일 안 `private` 상수로 두고 SEED 출처를 주석으로 단다. progress 두께 2dp는 `x0Half`를 읽는다. 눌림 축소량 2dp는 배율 계산 안의 숫자 상수다.

## `pressable` 부품

체크박스·라디오·스위치·세그먼트가 이미 쓰는 부품과, 아직 쓰는 컴포넌트 없이 단위 테스트로만 검증하는 부품이 있다. 모두 `internal`이다. 표의 "쓰는 곳"이 컴포넌트 이름이 아니면 의도한 사용처다.

| 부품 | 하는 일 | 쓰는 곳 |
|---|---|---|
| `pushlogTriStateToggleArea` | 3상태 토글 탭 영역, `Role.Checkbox` | `PushlogCheckbox` |
| `pushlogSelectableArea` | 단일 선택 탭 영역, `Role.RadioButton` | `PushlogRadio` |
| `pushlogToggleableArea` | 켬·끔 탭 영역, 역할을 인자로 받음 | `PushlogSwitch`(`Role.Switch`) |
| `pushlogPressIndication` | 탭 없이 눌림 색과 축소만 보임. 축소는 끌 수 있음 | `PushlogCheckmark`, `PushlogRadiomark` |
| `pushlogPressScale` | 눌림 색 없이 축소만 보임 | `PushlogSwitchmark` |
| `PushlogPressableSurface`의 `scaleScope` | `SELF`는 표면 전체, `CONTENT`는 안의 내용만 줄임 | 세그먼트 항목, `PushlogSelectBox`(`CONTENT`) |
| `PushlogPressableSurface`의 `selectionMode` | `TOGGLE`은 toggleable, `SINGLE`은 selectable(`Role.RadioButton`) | 세그먼트 항목(`SINGLE`), `PushlogSelectBox`(`SINGLE`/`TOGGLE`) |
| `PushlogPressableSurface`의 `pressedBorder` | 눌린 동안만 테두리를 그림 | 세그먼트 항목 |
| `pushlogToggleablePressable`의 `role` | 토글 역할. 기본값 `Role.Checkbox`로 칩 동작 유지 | 지금 `role`을 넘기는 곳은 없다 |

- 탭 영역 modifier는 시각 효과가 없고 `indication = null`이다. 눌림 표시 modifier는 탭을 받지 않는다. 둘이 같은 `interactionSource`를 봐서 줄을 누르면 마크가 눌린다.
- 내용 축소(`CONTENT`)의 배율은 안쪽 내용이 아니라 바깥 표면 크기로 잰다(SEED `scale-feedback.ts`: 비율은 측정한 요소 크기에서 나온다). 안쪽 상자가 표면의 최소 제약을 그대로 받아 표면과 크기가 같아지므로 `PressScaleNode`가 자기 크기로 재도 같은 값이 나온다.
