---
id: design-system-action
title: 디자인 시스템 동작 컴포넌트
category: architecture
status: living
platforms: android
verified: 2026-11-17
related_spec: [docs/superpowers/specs/2026-11-13-design-system-action-components-design.md]
related_adr:
related_architecture: [design-system]
related_code: [PushlogActionButton, PushlogChip, PushlogContextualFloatingButton]
tags: [architecture, design-system]
---

# 디자인 시스템 동작 컴포넌트

`PushlogActionButton`, `PushlogChip`, `PushlogContextualFloatingButton`을 다룬다. 결정 배경은 [동작 컴포넌트 스펙](../../../superpowers/specs/2026-11-13-design-system-action-components-design.md). 눌림·표면·로딩 같은 공통 규칙은 [pressable.md](../pressable.md)에 있다.

- 칩 높이는 최솟값이다. SEED는 `height`를 고정하지만 Pushlog는 최솟값(`heightIn(min)`)으로 둬서 글자가 잘리지 않고, 글자가 필요로 하면 칩이 커진다. 글자 크기 200%에서도 칩이 SEED 높이(36dp) 그대로인 것은 Compose가 큰 글자를 비선형으로 확대해 t4 한 줄이 36dp 안에 들어서다. 더 큰 배율에서는 커진다(`growsWithFontScale`이 fontScale 4로 확인한다).
- 아이콘만 버튼은 같은 이름의 오버로드다. 한 이름에 미리보기 화면이 하나라서(`PreviewCoverageRulesTest`) 두 오버로드를 한 화면에 함께 보여 준다.
- `PreviewSampleIcons`(`core:ui`)는 미리보기·테스트 전용이다. 제품 아이콘 세트가 아니다.
