---
id: design-system-input
title: 디자인 시스템 글자 입력
category: architecture
status: living
platforms: android
verified: 2026-11-19
related_spec: [docs/superpowers/specs/2026-11-17-design-system-text-inputs-design.md]
related_adr:
related_architecture: [design-system]
related_code: [PushlogField, PushlogFieldLabel, PushlogTextInput, PushlogTextArea, PushlogSearchInput, PushlogInputButton, ClearButton, LocalPushlogFieldState, GraphemeLimitTransformation, PushlogResidentNumberInput, PushlogCardNumberInput, PushlogResidentNumberState, PushlogCardNumberState]
tags: [architecture, design-system]
---

# 디자인 시스템 글자 입력

`field`(라벨·설명·오류 틀), `PushlogTextInput`, `PushlogTextArea`, `PushlogSearchInput`, `PushlogInputButton`, `PushlogResidentNumberInput`, `PushlogCardNumberInput`을 다룬다. 결정 배경은 [글자 입력 스펙](../../../superpowers/specs/2026-11-17-design-system-text-inputs-design.md). 입력 상태를 화면 어디에 둘지는 [미결 항목](../../../synthesis/open-questions.md)에 있다.

- 패키지는 `field`(`PushlogField`, `PushlogFieldLabel`), `textinput`(`PushlogTextInput`, `PushlogTextArea`), `searchinput`(`PushlogSearchInput`), `inputbutton`(`PushlogInputButton`), `clearbutton`(둘이 같이 쓰는 `internal ClearButton`), `segmentedtextinput`(`PushlogResidentNumberInput`, `PushlogCardNumberInput`과 각 상태 홀더)이다. 한 줄·여러 줄 입력은 `internal PushlogTextInputBase`를 같이 쓴다.
- field 상태(`invalid`, `enabled`, `readOnly`, 접근성용 라벨·설명·오류 문구)는 `internal LocalPushlogFieldState`(`compositionLocalOf`)로 내려간다. 입력마다 같은 값을 두 번 넘기다 한쪽만 고치는 일을 막으려는 것이다. 받는 쪽(text-input, textarea, #35 그룹 세 개)은 읽은 뒤 `ResetPushlogFieldState`로 자기 아래를 기본값으로 되돌린다. 위에서 끊지 않고 받는 쪽에서 끊는 이유는, field가 어떤 자손이 상태를 받는지 알 수 없고 받는 쪽만 "내가 소비했다"를 알기 때문이다. 이렇게 해서 select-box footer 안의 글자 입력이나 field 안의 field가 바깥 라벨·오류·`enabled`를 물려받지 않는다. 다만 footer에 바로 둔 `PushlogCheckbox`·`PushlogRadio`는 field 상태가 아니라 그룹이 준 `LocalSelectionGroupEnabled`를 읽으므로 그룹 `enabled`를 물려받는다([미결](../../../synthesis/open-questions.md)).
- 그룹은 `enabled`를 `LocalSelectionGroupEnabled`(`component.selection`)로 안쪽 컨트롤에 준다. field 안에 바로 둔 `PushlogCheckbox`·`PushlogRadio`는 field 상태를 읽지 않는다. 공개 기본값 `enabled = true`를 바꾸지 않으려는 것이다. 마크 그리기는 바뀌지 않는다.
- 접근성 노드는 입력(또는 그룹) 하나로 모은다. field 머리·꼬리는 `clearAndSetSemantics {}`로 숨기고, 거기 있던 라벨·필수 표시·보조 글자·설명·글자 수는 받는 쪽이 읽는다. 오류는 어느 쪽이든 받는 노드에 `error`로 붙는다. 글자 입력은 이 문구를 상자 뒤에 깐 의미 전용 자식(`PushlogFieldSpokenText`)의 `text`로 두고, `BasicTextField`가 자손 의미를 합쳐 M3 라벨처럼 입력의 자식으로 읽힌다. 입력 노드에 `contentDescription`·`stateDescription`을 쓰지 않는다. Compose 1.12는 편집 글자가 있는 노드의 상태 설명을 빈 입력 안내로 덮고, 자식이 있는 합침 노드의 `contentDescription`은 노드 정보에 넣지 않으며, `contentDescription`이 있으면 글자·단어 이동을 끈다. 의미 전용 자식은 크기가 있어야 하고(크기 0이면 트리에서 빠진다) 다른 자식에 다 덮이면 안 되므로 상자 크기로 맨 아래에 깐다. 그룹은 편집 글자가 없어 `pushlogFieldSemantics`가 이름을 `contentDescription`, 나머지를 상태 설명으로 넣는다. 머리·꼬리와 입력이 각자 읽히면 같은 글이 두 번 읽히고, 입력은 자기 라벨을 몰라 이름 없는 노드가 된다. 오류일 때 화면에서 감춘 설명도 이 노드에 남는다.
- 실기기 확인(Samsung SM-A356N, `app-preview`, `uiautomator dump`): 입력마다 편집 값 뒤에 합쳐진 글자 자식 하나가 붙고, 그 글은 `<label>, <필수>, <indicatorText>, <description>, <n/max자>`(있는 조각만)이다. 오류는 입력 노드의 error 표시로만 나간다. field 머리·꼬리는 따로 노드가 되지 않는다. 편집 노드에 `contentDescription`이 없어 글자·단어 이동이 된다. TalkBack이 실제로 읽는 순서는 귀로 확인해 기록하지 않았다.
- 테두리는 기본 1dp를 늘 그리고 그 위에 2dp 강조선 하나를 겹친다(`Modifier.textInputStroke`). 포커스와 오류는 각자 0→1 정도를 100ms로 전환하고, 강조선 투명도는 둘 중 큰 값, 색은 오류가 차지하는 비율만큼 포커스 색에서 오류 색으로 옮긴다. 색을 바꿔 끼우면 포커스 중 오류가 생기거나 걷힐 때 색이 튀고, 포커스 선과 오류 선을 따로 겹치면 둥근 모서리의 안티에일리어싱 틈으로 아래 선이 비친다. 두께를 애니메이션하면 안쪽 여백이 움직인다. 투명도는 그리기 단계에서 읽어 페이드 동안 재구성하지 않는다.
- 글자 수 `TextFieldState.graphemeCount`는 `@Composable`이 아닌 확장 프로퍼티다. 공개 `@Composable`을 늘리면 `PreviewCoverageRulesTest`가 미리보기 화면을 요구하고, 글자 수는 상태를 읽을 뿐 그릴 것이 없다. field가 상태 객체를 모르고 호출하는 쪽이 `characterCount`로 넘긴다.
- `GraphemeLimitTransformation`은 편집 전에 있던 글자를 지우지 않는다. 글자 수가 늘지 않는 편집(지우기, 같은 길이 바꾸기)은 한도를 넘은 값에서도 통과시키고, 늘리는 편집은 새로 들어온 부분만 남은 자리만큼 잘라 낸다. 원본이 이미 한도를 넘었으면 남은 자리는 원본 글자 수 기준이라, 선택을 바꿔 쓸 때 지운 만큼은 넣고 원본보다 길어지지는 않는다. 한도보다 긴 값을 받은 뒤에도 Backspace로 줄일 수 있어야 한다. SEED는 뒤를 잘라내지만, 그러면 붙여넣기나 한글 조합 중에 사용자가 이미 친 글자가 사라진다.
- `PushlogSearchInput`은 field 상태를 받지 않는다. 검색창은 `PushlogField`의 라벨·오류·필수 틀 없이 단독으로 놓이고, field 안에 놓아도 바깥 `invalid`·`readOnly`가 검색어에 붙을 이유가 없다. 그래서 `ResetPushlogFieldState`로 시작하고 비활성은 `enabled` 인자로만 받는다. 이름(`contentDescription` 인자, 기본 "검색")도 위 접근성 규칙대로 입력 노드가 아니라 `PushlogFieldSpokenText` 자식으로 합친다.
- 검색 키는 값이 비어 있어도 `onSubmit("")`을 부른다. SEED가 그렇게 동작하고, 화면은 빈 값 제출을 "검색 조건 해제"로 쓸 수 있어야 하기 때문이다. 막는 일은 호출하는 쪽이 정한다.
- `PushlogInputButton`은 두 층을 겹친다. 아래층은 탭·눌림 색을 맡는 표면, 위층은 내용만 담은 `Row`로 탭 modifier가 없다. 한 층으로 하면 지우기 버튼 탭이 바깥 표면의 눌림까지 일으키고, 표면 전체가 줄어들어 SEED의 `scaleScope: content`(표면은 그대로, 내용만 줄어듦)도 맞출 수 없다. 두 층이면 지우기 버튼이 자기 탭을 먼저 받고, 빈 곳 터치만 아래 표면으로 간다.
- `PushlogInputButton`의 테두리는 두 층 사이의 형제 레이어에 그린다. `PushlogPressableSurface`가 호출하는 쪽 modifier보다 뒤에 배경을 그리므로, 표면 modifier에 테두리를 넣으면 `bg.disabled`나 눌림 색에 덮인다. 이 레이어에는 탭·의미 정보가 없어 터치는 아래로 간다.
- `PushlogInputButton`의 접근성 이름은 값이 있으면 앞뒤 글자를 보이는 순서(`prefixText 값 suffixText`)로 값에 붙인다. 앞뒤 글자는 화면에서 값의 단위·통화라 빼면 "1,000"만 읽혀 뜻이 달라진다. placeholder일 때는 붙이지 않는다. 힌트 옆 단위는 소음이고, 값이 없는데 "₩ 금액 원"으로 읽히면 값이 있는 것처럼 들린다.
- 지우기 버튼(`ClearButton`)은 눌리면 자기만 줄어든다(SEED `clearButton`의 `scaleScope: self`). 입력 전체나 바깥 표면을 줄이면 지우기를 누를 때마다 입력 상자가 같이 흔들린다. input-button의 지우기 탭 영역도 search-input처럼 44dp로 넓히되, 아이콘 위치와 이웃 간격이 그대로이도록 넓힌 만큼 양옆 간격 위로 겹친다.
- `medium`·`responsive` 크기는 만들지 않았다. SEED가 데스크톱 전용이라고 적었다.
- segmented 입력은 주민등록번호·카드번호 두 형식만 공개하고 칸 엔진(`SegmentedTextInput` 등)은 `internal`로 둔다. 칸 수·자리 수·가림 위치가 형식마다 정해진 값이라 일반 칸 입력으로 열면 가린 칸 규칙을 호출하는 쪽이 어길 수 있고, 한 번 공개한 엔진 API는 바꾸기 어렵다.
- 칸은 `BasicSecureTextField`가 아니라 `BasicTextField`에 `OutputTransformation`(점)과 `password()` 의미 정보를 써서 가린다. `BasicSecureTextField`는 `ContentType.Password`를 붙여 자동완성·저장 제안 대상이 된다.
- 가린 칸 키보드는 `KeyboardType.NumberPassword`(자동 수정 끔), 보이는 칸은 `Number`다(사용자 결정 2026-11-19). `NumberPassword`는 키보드에 민감한 값임을 알려 입력을 학습·제안하지 않게 한다. Compose foundation 1.12.0은 `NumberPassword`에 `ContentType.Password`를 붙이지만, 가린 칸 바깥 의미 정보의 `contentDataType = ContentDataType.None`이 텍스트 필드의 값을 덮고 `AndroidAutofillManager`는 None인 노드를 자동완성 대상에서 뺀다. 한 노드 안에서는 바깥 modifier의 의미 정보가 이기므로, 텍스트 필드가 변환 전 숫자를 넣는 `inputText`도 같은 자리에서 점으로 덮는다.
- 가린 칸은 자동완성에서 뺀다. `contentDataType = ContentDataType.None`을 준다. 자동완성 종류(`ContentType`)는 따로 붙이지 않지만 `NumberPassword`가 붙이는 `ContentType.Password` 힌트는 남는다. 제외는 `contentDataType = None`만으로 한다. 가린 칸에 자동완성 종류가 NONE이 아닌 값이 들어오면 `require`로 막는다. 자동완성 서비스가 가린 값을 읽거나 저장 제안에 담지 않게 하려는 것이다. 보이는 칸(생년월일, 카드번호 첫 칸)만 자동완성을 받는다.
- 저장 상태(savedInstanceState)에는 가린 값까지 남긴다. 회전·프로세스 종료 뒤에도 입력이 유지되는 대신 민감한 값이 Bundle에 들어간다는 것을 알고 사용자가 정했다([스펙 §5](../../../superpowers/specs/2026-11-17-design-system-text-inputs-design.md)). 상태 홀더 `toString`은 값을 내지 않는다.
- 칸 줄은 `LayoutDirection.Ltr`로 고정한다. 숫자 칸의 순서가 RTL 로케일에서 뒤집히면 주민등록번호·카드번호의 자리 순서가 달라 보인다.
- `clear()`는 상태 홀더에 있고 부르는 책임은 쓰는 화면에 있다. 입력이 사라질 때 지울지는 화면의 수집 목적에 달려 있어 컴포넌트가 알 수 없다.
- 칸 전체를 바꾸는 편집(자동완성, 접근성 `SetText`)은 앞뒤 공통부분 비교가 아니라 `TextFieldBuffer.changes`로 알아본다. 비교로 하면 새 값이 현재 칸 내용으로 시작할 때 이어 붙인 입력으로 읽혀 값이 깨진다.
- 빈 칸의 접근성 이름은 합쳐진 의미 전용 자식에서 "라벨, placeholder"로 읽히고, placeholder `Text`는 의미 정보에서 지운다. placeholder가 의미 전용 자식을 완전히 덮으면 자식이 트리에서 빠지기 때문이다(실기기에서 확인).
- field 라벨·오류는 칸 줄이 아니라 칸에 붙인다. TalkBack 포커스는 편집 노드인 칸이 받아서, 줄에만 두면 칸으로 바로 간 사용자는 오류를 듣지 못한다. 라벨은 첫 칸에 한 번, 오류는 칸마다 둔다.
- 상자 치수·글자·글자색은 `PushlogTextInputVariant.OUTLINE`의 한 줄 값에서 읽는다. 같은 값을 따로 적으면 OUTLINE을 고칠 때 segmented만 남는다.
- 자리 수를 넘친 입력을 다음 칸으로 넘기는 일은 `Channel`로 한다. 입력 변환 도중에 오는 값을 놓치지 않고 차례로 처리하기 때문이다. 상태 값 방식을 쓰지 않은 사정은 [미결 항목](../../../synthesis/open-questions.md)의 "segmented 넘친 입력"에 있다.
- 접미사가 있는 카드번호는 폭이 대략 330dp 아래에서 빠듯하다. SEED 값(칸 크기·간격·여백)에서 그대로 나온다.
- 실기기 확인(Samsung SM-A356N, 삼성 패스 자동완성, 삼성 키보드, `app-preview`): 가린 칸은 "••••"로 나오고 password=true다. `NumberPassword`인 가린 칸에는 숫자 PIN 키패드(숫자와 영문 힌트, 마지막 칸은 "완료")가, 보이는 칸에는 숫자 키패드가 뜬다. 가린 칸은 자동완성 구조에 여전히 `ContentType.Password` 힌트를 싣는다. 카드번호 첫 칸에 "4111111111111111"을 넣으면 칸이 4111 / 1111 / •••• / ••••로 나오고, 화면을 나가도 삼성 패스 저장 제안이 뜨지 않았다(`dumpsys autofill`의 showsSaveUi=false). 빈 둘째 칸에서 소프트 키보드 Backspace는 첫 칸의 마지막 숫자를 지우고 포커스를 그리로 옮긴다. 입력하면 칸이 자동으로 넘어간다. 빈 칸은 "카드번호 1-4번째 자리, 0000"으로 읽힌다. 기기가 삼성 패스를 써서 구글 자동완성은 확인하지 못했다. 숫자 키패드에 언어 전환이 없어 한글 조합 중 칸 이동은 재현하지 못했다.
- 주민등록번호·카드번호 입력을 쓰는 화면은 수집 목적, 보존 기간, 동의를 자기 스펙에 적는다.
