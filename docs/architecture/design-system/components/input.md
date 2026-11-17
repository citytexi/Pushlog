---
id: design-system-input
title: 디자인 시스템 글자 입력
category: architecture
status: living
platforms: android
verified: 2026-11-17
related_spec: [docs/superpowers/specs/2026-11-17-design-system-text-inputs-design.md]
related_adr:
related_architecture: [design-system]
related_code: [PushlogField, PushlogFieldLabel, PushlogTextInput, PushlogTextArea, LocalPushlogFieldState, GraphemeLimitTransformation]
tags: [architecture, design-system]
---

# 디자인 시스템 글자 입력

`field`(라벨·설명·오류 틀), `PushlogTextInput`, `PushlogTextArea`를 다룬다. 결정 배경은 [글자 입력 스펙](../../../superpowers/specs/2026-11-17-design-system-text-inputs-design.md). 입력 상태를 화면 어디에 둘지는 [미결 항목](../../../synthesis/open-questions.md)에 있다.

- 패키지는 `field`(`PushlogField`, `PushlogFieldLabel`)와 `textinput`(`PushlogTextInput`, `PushlogTextArea`)이다. 한 줄·여러 줄 입력은 `internal PushlogTextInputBase`를 같이 쓴다.
- field 상태(`invalid`, `enabled`, `readOnly`, 접근성용 라벨·설명·오류 문구)는 `internal LocalPushlogFieldState`(`compositionLocalOf`)로 내려간다. 입력마다 같은 값을 두 번 넘기다 한쪽만 고치는 일을 막으려는 것이다. 받는 쪽(text-input, textarea, #35 그룹 세 개)은 읽은 뒤 `ResetPushlogFieldState`로 자기 아래를 기본값으로 되돌린다. 위에서 끊지 않고 받는 쪽에서 끊는 이유는, field가 어떤 자손이 상태를 받는지 알 수 없고 받는 쪽만 "내가 소비했다"를 알기 때문이다. 이렇게 해서 select-box footer 안의 입력이나 field 안의 field가 바깥 라벨·오류·`enabled`를 물려받지 않는다.
- 그룹은 `enabled`를 `LocalSelectionGroupEnabled`(`component.selection`)로 안쪽 컨트롤에 준다. field 안에 바로 둔 `PushlogCheckbox`·`PushlogRadio`는 field 상태를 읽지 않는다. 공개 기본값 `enabled = true`를 바꾸지 않으려는 것이다. 마크 그리기는 바뀌지 않는다.
- 접근성 노드는 입력(또는 그룹) 하나로 모은다. field 머리·꼬리는 `clearAndSetSemantics {}`로 숨기고, 거기 있던 라벨·필수 표시·보조 글자·설명·글자 수는 받는 쪽이 읽는다. 오류는 어느 쪽이든 받는 노드에 `error`로 붙는다. 글자 입력은 이 문구를 상자 뒤에 깐 의미 전용 자식(`PushlogFieldSpokenText`)의 `text`로 두고, `BasicTextField`가 자손 의미를 합쳐 M3 라벨처럼 입력의 자식으로 읽힌다. 입력 노드에 `contentDescription`·`stateDescription`을 쓰지 않는다. Compose 1.12는 편집 글자가 있는 노드의 상태 설명을 빈 입력 안내로 덮고, 자식이 있는 합침 노드의 `contentDescription`은 노드 정보에 넣지 않으며, `contentDescription`이 있으면 글자·단어 이동을 끈다. 의미 전용 자식은 크기가 있어야 하고(크기 0이면 트리에서 빠진다) 다른 자식에 다 덮이면 안 되므로 상자 크기로 맨 아래에 깐다. 그룹은 편집 글자가 없어 `pushlogFieldSemantics`가 이름을 `contentDescription`, 나머지를 상태 설명으로 넣는다. 머리·꼬리와 입력이 각자 읽히면 같은 글이 두 번 읽히고, 입력은 자기 라벨을 몰라 이름 없는 노드가 된다. 오류일 때 화면에서 감춘 설명도 이 노드에 남는다.
- 테두리는 기본 1dp를 늘 그리고 그 위에 2dp 강조 테두리(포커스·오류)를 겹쳐 투명도만 100ms로 바꾼다(`Modifier.textInputStroke`). 두께나 색을 애니메이션하면 안쪽 여백이 움직이거나 중간 색이 지나간다. 투명도는 그리기 단계에서 읽어 페이드 동안 재구성하지 않는다.
- 글자 수 `TextFieldState.graphemeCount`는 `@Composable`이 아닌 확장 프로퍼티다. 공개 `@Composable`을 늘리면 `PreviewCoverageRulesTest`가 미리보기 화면을 요구하고, 글자 수는 상태를 읽을 뿐 그릴 것이 없다. field가 상태 객체를 모르고 호출하는 쪽이 `characterCount`로 넘긴다.
- `GraphemeLimitTransformation`은 편집 전에 있던 글자를 지우지 않는다. 글자 수가 늘지 않는 편집(지우기, 같은 길이 바꾸기)은 한도를 넘은 값에서도 통과시키고, 늘리는 편집은 새로 들어온 부분만 남은 자리만큼 잘라 낸다. 남은 자리가 없으면 넣은 부분이 모두 빠진다. 한도보다 긴 값을 받은 뒤에도 Backspace로 줄일 수 있어야 한다. SEED는 뒤를 잘라내지만, 그러면 붙여넣기나 한글 조합 중에 사용자가 이미 친 글자가 사라진다.
- `medium`·`responsive` 크기는 만들지 않았다. SEED가 데스크톱 전용이라고 적었다.
- 주민등록번호·카드번호 입력을 쓰는 화면은 수집 목적, 보존 기간, 동의를 자기 스펙에 적는다.
