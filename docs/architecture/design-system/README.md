---
id: design-system
title: 디자인 시스템
category: architecture
status: living
platforms: android
verified: 2026-11-17
related_spec: [docs/superpowers/specs/2026-11-13-design-system-foundation-design.md, docs/superpowers/specs/2026-11-13-design-system-action-components-design.md, docs/superpowers/specs/2026-11-16-design-system-selection-controls-design.md, docs/superpowers/specs/2026-11-17-design-system-text-inputs-design.md]
related_adr:
related_architecture: [module-structure]
related_code: [PushlogTheme, PushlogPreviewTheme]
tags: [architecture, design-system]
---

# 디자인 시스템

`core:designsystem`의 토큰, `PushlogTheme`, 컴포넌트, 그리고 `app-preview`를 다룬다. 값 자체는 코드가 정답이다. 이 디렉터리에는 코드로 복원할 수 없는 선택 이유만 적는다.

작업에 맞는 문서 하나만 먼저 읽는다.

| 작업 | 문서 |
|---|---|
| 색·간격·글자·그림자·모션 토큰을 고치거나 M3 연결을 바꾼다 | [foundation.md](foundation.md) |
| 눌리는 컴포넌트를 새로 만든다(패키지, 메트릭, 표면, 축소, 로딩) | [pressable.md](pressable.md) |
| 버튼·칩·플로팅 버튼을 고친다 | [components/action.md](components/action.md) |
| 체크박스·라디오·스위치·세그먼트·select-box를 고친다 | [components/selection.md](components/selection.md) |
| field·text-input·search-input·input-button·segmented 입력을 고친다 | [components/input.md](components/input.md) |
| 미리보기 화면·프리뷰 스크린샷을 추가한다 | [preview.md](preview.md) |

새 컴포넌트 묶음은 `components/` 아래에 파일을 하나 더 만들고 위 표에 한 줄을 더한다. 공통 규칙은 `pressable.md`에 둔다.
