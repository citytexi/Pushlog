# 디자인 시스템 글자 입력 컴포넌트 설계

- 작성일: 2026-11-17
- 상태: 승인됨 (2026-11-17)
- 이슈: #36 (상위 #20, 최상위 #17)
- 범위: `core:designsystem`의 글자 입력 컴포넌트. SEED 이름으로 `field`, `field-label`, `text-input`(한 줄 Text Field Input, 여러 줄 Text Field Textarea), `search-input`, `segmented-text-input`, `input-button`(SEED 문서 이름 Field Button). #35 선택 컨트롤 그룹을 `field`로 감싸는 연결도 포함한다. 값 조절(#37), 시트·선택기 입력(#38)은 범위 밖이다.

## 1. 의도

SEED의 글자 입력 컴포넌트를 정의대로 Compose로 옮긴다. 범위 원칙은 #35와 같이 "deprecated만 빼고 SEED 전부"다(사용자 결정, 2026-11-16). 그래서 Pushlog 화면에 지금 쓸 곳이 없는 `segmented-text-input`도 SEED 동작 전부를 만든다(사용자 결정, 2026-11-17).

설계 선택은 구현 난도보다 확장성, 안정성, 성능을 기준으로 한다(사용자 기준).

- 값 기준: SEED rootage 정의, 기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`(#35와 같다). 파일은 `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/<name>.yaml`. 대상: `field`, `field-label`, `text-input`, `search-input`, `segmented-text-input`, `input-button`.
- 기본값 기준: 같은 커밋의 `packages/qvism-preset/src/recipes/<name>.ts` `defaultVariants`.
- 구성·동작 기준: 같은 커밋의 `docs/registry/react/ui/`(`text-field.tsx`, `field-button.tsx`, `search-input.tsx`, `segmented-text-field.tsx`, `resident-number-field.tsx`, `card-number-field.tsx`, `checkbox.tsx`, `radio-group.tsx`, `select-box.tsx`), `docs/content/react/components/`(`text-field-input.mdx`, `text-field-textarea.mdx`, `search-input.mdx`, `segmented-text-input.mdx`, `field-button.mdx`), segmented 동작은 `packages/react-headless/segmented-text-input/src`.
- 이 문서는 정의 파일 값을 다 옮겨 적지 않는다. 값은 정의 파일이 정본이고, 여기에는 Pushlog에서 정한 것과 정의 파일만으로 알 수 없는 것을 적는다.

## 2. 목표 · 비목표 · 반목표

목표

- 위 여섯 정의의 색·크기·모서리·글자·모션을 라이트/다크로 만든다.
- `field`가 라벨·필수 표시·설명·오류·글자 수를 맡고, 안쪽 입력과 #35 선택 컨트롤 그룹에 오류·비활성·읽기 전용 상태를 한 곳에서 내려준다.
- 공개 `@Composable`마다 `@PushlogPreview`, 미리보기 앱 화면, 라이트/다크 스크린샷을 둔다.
- PR 세 개로 나눈 계획을 함께 쓴다(§8).

비목표

- `medium` 크기(`text-input`, `segmented-text-input`, `input-button`). SEED가 "데스크톱 전용, 모바일에서는 쓰지 않음"이라고 적었다. 버튼에서 `xsmall`을 뺀 것과 같은 판단이다(설계자 판단, 사용자 승인 2026-11-17).
- `responsive` 크기. 웹 화면 너비 전환용이다.
- 숫자 서식(천 단위 구분 등 SEED Number Formatting). 호출하는 쪽이 `OutputTransformation`으로 붙인다. 필요해지면 따로 연다.
- SEED Search Bar(Stackflow App Bar에 검색 입력을 넣는 것). 상단 바는 #22 이후 대상이다.
- 주민등록번호·카드번호 외 segmented 형식. SEED도 이 두 형식만 지원한다(registry 주석: "Only the shipped formats … are supported").
- #35 선택 컨트롤 마크의 오류 모양. SEED 정의에 없다. 오류는 그룹을 감싼 `field`의 문구와 접근성 정보로만 드러난다.
- 아이콘 세트. 필요한 아이콘은 컴포넌트가 직접 그린다(§6.8).

반목표 (돌아가도 실패로 보는 것)

- 한글 조합 중 글자가 되돌아가거나 커서가 튀는 것.
- `field`의 `invalid`와 안쪽 입력의 테두리·문구가 어긋나는 것.
- 오류 문구나 라벨이 스크린 리더에 안 읽히는 것. 오류일 때 설명이 사라져서 스크린 리더도 못 읽는 것(SEED는 설명을 시각적으로만 숨긴다).
- 눌렀을 때 ripple이 보이는 것, `pressable` 밖에서 탭을 처리하는 것(`ComponentRulesTest`가 막는다. 규칙을 약화하지 않는다).
- 가린 칸의 실제 값이 화면, 접근성 트리, 로그, 자동완성 서비스에 드러나는 것.
- Material 3 `TextField`·`OutlinedTextField`를 쓰는 것(`ComponentRulesTest`가 막는다).

## 3. 제약

- `AGENTS.md` 금지 사항, `docs/code-conventions.md` 주석 규칙.
- `docs/architecture/design-system/pressable.md` 공통 규칙과 `.claude/rules/design-system-components.md`:
  - `component.<종류>` 하위 패키지, 파일은 최상위 타입마다 하나.
  - 크기·variant별 값은 enum의 `internal` 멤버(`metrics()`, 색 함수)가 `when (this)`로 돌려준다. variant·size가 없으면 토큰을 컴포넌트 파일에서 바로 읽는다.
  - SEED px 값은 같은 크기 토큰으로, 없으면 파일 안 `private` 상수 + SEED 출처 주석.
  - 그룹·감싸는 쪽이 자식 기본값을 줄 때는 `internal` `CompositionLocal`로 주고 자식 인자가 덮어쓴다.
  - 공개 `@Composable`마다 `<함수>PreviewKey`·`<함수>PreviewScreen.kt`, 섹션별 `@PushlogPreview`.
- `ComponentRulesTest`: 탭 처리는 `pressable`에서만. 허용 Material 3는 `Icon`, `Text`, 진행 표시뿐이다. `BasicTextField`는 foundation이라 허용된다.
- minSdk 26. 문자소(grapheme) 세기는 `android.icu.text.BreakIterator`를 쓸 수 있다.
- 알림 원문 금지 규칙은 이번 범위에 직접 걸리지 않지만, 입력값을 로그·예외 메시지에 넣지 않는다.
- 주민등록번호·카드번호 입력을 쓰는 화면은 수집 목적, 보존 기간, 동의를 자기 스펙에 적는다. 이 문장은 `components/input.md`에도 남긴다.

## 4. 접근 방식

### 4.1 입력 상태 API

| 방식 | 내용 | 결과 |
|---|---|---|
| A. `TextFieldState` 기반 `BasicTextField` | 글자 수 제한은 `InputTransformation`, 가린 칸·구분자 표시는 `OutputTransformation`. 칸마다 상태 객체를 직접 다룬다 | **선택(사용자 승인 2026-11-17)** |
| B. `value: String` + `onValueChange` | ViewModel `String`과 바로 맞는다. 조합 중 값 되돌림으로 커서가 튀는 문제가 알려져 있고, 가리기는 `VisualTransformation` 오프셋 매핑을 손으로 짜야 한다 | 탈락 |
| C. M3 `OutlinedTextField` 감싸기 | ripple·모양·모션이 SEED와 다르고 `ComponentRulesTest`가 막는다 | 탈락 |

A를 고른 이유: 상태를 IME와 같은 쪽에서 들고 있어 조합 중 되돌림이 구조적으로 없다(안정성). 변환을 조각으로 붙였다 뗄 수 있고 segmented 칸 이동·붙여넣기 분배를 입력 이벤트 흉내 없이 짤 수 있다(확장성). 입력마다 `String` 왕복이 없다(성능). 대가는 호출하는 쪽이 `rememberTextFieldState()`를 쓰고 ViewModel과 잇는 코드가 조금 는다는 것이다.

### 4.2 `field` → 안쪽 입력 상태 전달

| 방식 | 내용 | 결과 |
|---|---|---|
| A. `PushlogField`가 `internal` `CompositionLocal`로 `invalid`·`enabled`·`readOnly`를 내려주고, 안쪽 입력은 같은 인자를 받되 기본값을 그 값으로 둔다 | 상태를 한 곳에서 정한다. 새 입력은 읽기만 하면 field 안에 들어간다 | **선택(사용자 승인 2026-11-17)** |
| B. 입력마다 같은 값을 따로 받는다 | field와 입력에 두 번 넘기고 하나만 고치면 어긋난다 | 탈락 |

`CompositionLocal`은 `compositionLocalOf`(값이 바뀌면 읽는 곳만 다시 그림)로 둔다. 기존 규칙 "그룹이 자식 기본값을 줄 때는 `internal` `CompositionLocal`로 주고 자식 인자가 덮어쓴다"와 같은 방식이다.

## 5. 사용자가 정한 것

- 범위: deprecated만 빼고 SEED 전부(2026-11-16). `segmented-text-input`은 SEED 동작 전부(2026-11-17).
- 입력 상태 API는 `TextFieldState`(4.1-A), field 상태 전달은 `CompositionLocal`(4.2-A)(2026-11-17).
- `medium` 크기 제외, PR 세 개로 나누기(2026-11-17, 설계자 제안 승인).
- 작업은 워크트리 대신 메인 레포의 `feat/...` 브랜치에서 한다.
- segmented 값은 가린 칸까지 전부 저장 상태(savedInstanceState)에 남긴다. 회전·프로세스 종료 뒤에도 입력이 유지된다(2026-11-17). 민감한 값이 Bundle에 들어간다는 점을 알고 정했다.

## 6. 설계

### 6.1 패키지와 공개 API

| 패키지 | 공개 | `internal` | SEED |
|---|---|---|---|
| `component.field` | `PushlogField`, `PushlogFieldLabel`, `PushlogFieldLabelWeight` | `LocalPushlogFieldState`, 필수 표시, 오류 아이콘 그리기 | field, field-label |
| `component.textinput` | `PushlogTextInput`, `PushlogTextArea`, `PushlogTextInputVariant` | 장식 상자(테두리·앞뒤 요소), `TextInputMetrics`, 문자소 제한 변환 | text-input |
| `component.searchinput` | `PushlogSearchInput` | | search-input |
| `component.inputbutton` | `PushlogInputButton` | | input-button |
| `component.clearbutton` | 없음 | `ClearButton`, 지우기 아이콘 그리기 | search-input·input-button의 clearButton |
| `component.segmentedtextinput` | `PushlogResidentNumberInput`, `PushlogCardNumberInput`, 상태 클래스 `PushlogResidentNumberState`·`PushlogCardNumberState`(+ `Saver`) | 칸 엔진, 점 그리기 | segmented-text-input |

- 한 줄과 여러 줄을 함수 둘로 나눈다(SEED 문서도 둘로 나눈다). 장식 상자는 공유한다(설계자 판단).
- `text-input`에는 SEED `size` 중 `large`만 남으므로 size enum을 만들지 않는다. 값은 `PushlogTextInputVariant.metrics()`가 돌려준다(설계자 판단).
- segmented는 범용 칸 API를 공개하지 않고 SEED가 내놓은 두 형식만 공개한다. SEED가 다른 형식을 지원하지 않는다고 적었고, 공개 API가 작을수록 동작을 바꿀 때 안전하다(설계자 판단). 칸 엔진은 `internal`이라 형식을 더할 때 공개만 늘리면 된다.
- `component.clearbutton`은 공개 함수가 없어 미리보기 화면이 필요 없다. search-input과 input-button 미리보기가 그린다.

### 6.2 `PushlogField`, `PushlogFieldLabel`

구성(SEED registry `text-field.tsx`와 같다): 머리(라벨 + 필수 표시 + 보조 글자) / 내용 슬롯 / 꼬리(설명 또는 오류 + 글자 수).

- 인자: `label: String?`, `labelWeight`(기본 `MEDIUM`, recipe), `showRequiredIndicator`, `indicatorText: String?`(예: "(선택)"), `description: String?`, `descriptionIcon: Painter?`, `errorMessage: String?`, `characterCount: Int?`, `maxCharacterCount: Int?`, `invalid`, `enabled`, `readOnly`, `modifier`, `content`.
- 머리는 `label`이나 `indicatorText`가 있을 때만, 꼬리는 셋 중 하나라도 있을 때만 그린다.
- 오류 문구는 `invalid && errorMessage != null`일 때만 보인다. 이때 설명은 화면에서 감추지만 접근성 트리에는 남긴다(SEED `VisuallyHidden`). 오류 문구 앞에는 16dp 느낌표 아이콘.
- 글자 수는 `characterCount`와 `maxCharacterCount`가 둘 다 있을 때 `현재/최대`로 보인다. `invalid`면 둘 다 `fg.critical`.
- 필수 표시는 6px 점(`fg.critical`)이다. SEED가 "font scaled"라고 적은 값(점 6, 위 4, 왼쪽 2, 보조 글자 왼쪽 4)은 sp로 두고 dp로 바꿔 글자 크기 설정을 따른다.
- 보조 글자는 크기 `t4`에 줄 높이 `t5`다(SEED가 "intentional; not a typo"라고 적음). `t4Regular`에서 줄 높이만 바꾼 스타일을 파일 안에서 만든다.
- `PushlogFieldLabel`은 라벨 글자만 그린다. 굵기 `REGULAR`(SEED base), `MEDIUM`, `BOLD`. 공개 기본값은 recipe의 `MEDIUM`이다.
- `LocalPushlogFieldState`: `invalid`, `enabled`, `readOnly`, 접근성용 라벨·설명·오류 문구. field 밖 기본값은 `invalid=false, enabled=true, readOnly=false`, 문구 없음.
- 상태를 받는 쪽(text-input·textarea·input-button·segmented 입력, §6.7 그룹)은 받은 뒤 자기 아래로 기본값을 다시 내려준다. 그래서 select-box footer 안의 입력이나 field 안의 field는 바깥 라벨·오류·`enabled`를 물려받지 않는다(설계자 판단).
- 접근성: 라벨·설명·오류 문구는 상태를 받는 노드(입력 또는 그룹) 하나에만 들어간다. `invalid`면 오류 문구를 `error`로 넣는다. 글자 입력은 라벨·필수 표시·보조 글자·설명·글자 수를 이은 문구를 상자 안 그리지 않는 의미 전용 자식의 `text`로 두어 입력 노드에 합친다(M3 라벨과 같은 길). 글자 입력 노드에 `contentDescription`·`stateDescription`을 두지 않는 이유는, Compose가 편집 글자가 있는 노드의 상태 설명을 덮어쓰고 자식이 있는 합침 노드의 `contentDescription`을 내보내지 않으며, `contentDescription`이 있으면 TalkBack 글자·단어 탐색이 꺼지기 때문이다. 편집 글자가 없는 그룹은 이름을 `contentDescription`, 나머지 문구를 상태 설명으로 넣는다. field 머리·꼬리 글자 노드는 접근성 트리에서 숨긴다. 그래서 같은 글이 두 번 읽히지 않는다(설계자 판단). 오류일 때 화면에서 감춘 설명도 이 노드에 남아 있어 읽힌다. 정확한 읽기 순서는 빌더가 TalkBack 트리로 확인해 `docs`에 남긴다.

### 6.3 `PushlogTextInput`, `PushlogTextArea`

- 인자: `state: TextFieldState`, `variant`(기본 `OUTLINE`, recipe), `placeholder: String?`, `prefixIcon: Painter?`, `prefixText: String?`, `suffixText: String?`, `suffixIcon: Painter?`, `maxGraphemeCount: Int?`, `invalid`·`enabled`·`readOnly`(기본값은 field 상태), `keyboardOptions`, `onKeyboardAction`, `inputTransformation`, `outputTransformation`, `interactionSource`, `modifier`.
- `maxGraphemeCount`는 문자소 기준 제한 `InputTransformation`으로 걸고 호출하는 쪽 `inputTransformation` 뒤에 잇는다. 이모지·한글 결합 문자를 한 글자로 센다(SEED `useTextFieldWithGraphemes`).
- 글자 수 표시: field가 글자를 직접 세지 않는다. 공개 확장 프로퍼티 `TextFieldState.graphemeCount`(`@Composable` 아님)를 두고, 호출하는 쪽이 `PushlogField(characterCount = state.graphemeCount, maxCharacterCount = 40)`처럼 넘긴다. field가 상태 객체를 모르게 해서 #35 그룹이나 버튼도 같은 field를 쓴다(설계자 판단). `@Composable`로 만들면 `PreviewCoverageRulesTest`가 미리보기 화면을 요구한다. 프로퍼티는 `state.text`를 읽으므로 텍스트가 바뀔 때만 읽는 곳이 다시 그려지고, 세기는 O(n)이라 캐시를 두지 않는다.
- 테두리: 기본 1px `stroke.neutralWeak`는 늘 그린다. 그 위에 2px 강조선 하나를 겹친다. 포커스(`neutralContrast`)와 오류(`criticalSolid`)는 각자 0→1 정도를 100ms `easing`으로 전환하고, 강조선 투명도는 둘 중 큰 값이다. 색은 오류 정도가 투명도에서 차지하는 비율만큼 포커스 색에서 오류 색으로 옮겨 가므로, 색이 섞이는 때는 포커스가 켜진 채 오류가 생기거나 걷히는 동안뿐이다. 포커스 선 위에 오류 선을 겹쳐 둔 것과 같은 결과를 한 선으로 그려 둥근 모서리에 아래 선이 비치지 않게 했다. 두께는 애니메이션하지 않는다. 투명도는 그리기 단계에서 읽는다. `underline`은 아래쪽 선만 그린다.
- 포커스는 `interactionSource.collectIsFocusedAsState()`로 안다. 탭 처리가 아니라 `ComponentRulesTest`에 걸리지 않는다. 장식 상자 전체가 `BasicTextField`의 decorator 안에 있어 안쪽 여백을 눌러도 포커스가 간다.
- 읽기 전용: `outline`은 배경 `bg.disabled`, `underline`은 값·placeholder를 `fg.neutralMuted`로. 비활성: `outline` 배경 `bg.disabled`, 글자·아이콘 `fg.disabled`.
- `PushlogTextArea`: 최소 높이 94, 위아래 여백 14(`x3Half`). 내용에 따라 늘어난다(SEED autoresize). 최대 높이는 호출하는 쪽이 `modifier`나 `lineLimits`로 정한다.

### 6.4 `PushlogSearchInput`

- 인자: `state`, `onSubmit: (String) -> Unit`, `placeholder`, `contentDescription`(기본 "검색"), `clearButtonLabel`(기본 "지우기"), `clearFocusOnSubmit`(기본 `true`), `enabled`, `modifier`.
- 높이 44, `r2Half`, 배경 `bg.neutralWeak`, 글자 `t6`. 테두리와 포커스 표시는 없다(정의 파일).
- field 상태는 받지 않는다. SEED search-input 정의에 오류·읽기 전용 상태가 없고 라벨 없이 쓰는 컴포넌트다. 비활성은 자기 `enabled` 인자로만 받는다(설계자 판단).
- 키보드 동작은 `ImeAction.Search`다. 검색을 누르면 `onSubmit(현재 값)` 뒤 `clearFocusOnSubmit`이면 포커스를 풀어 키보드를 내린다. 값이 비어 있어도 `onSubmit("")`을 부른다. 빈 검색을 막을지는 호출하는 쪽이 정한다(SEED `SearchInput.tsx`도 값 확인 없이 부른다). 한글 조합 확정은 IME가 따로 처리하므로 제출로 오지 않는다. 자동 테스트로 넣을 수 없어 수동 확인한다(§7).
- 지우기 버튼은 값이 있을 때만 보인다. 누르면 값을 비운다. 포커스가 있었으면 지운 뒤에도 유지하고, 없었으면 새로 주지 않는다. 버튼이 사라지며 접근성 포커스를 잃으면 입력으로 옮긴다(SEED 문서).
- `role="search"`에 해당하는 Compose 의미 정보는 없다. 이름 "검색"으로 대신한다.

### 6.5 `PushlogInputButton`

피커·시트를 여는 버튼이다. 글자 입력이 아니다.

- 인자: `value: String?`, `placeholder: String?`, `onClick`, `onClear: (() -> Unit)?`(주면 값이 있을 때 지우기 버튼을 보임), `prefixIcon`, `prefixText`, `suffixText`, `suffixIcon`, `contentDescription: String?`, `invalid`·`enabled`·`readOnly`(기본값은 field 상태), `modifier`.
- 표면은 `PushlogPressableSurface`(`scaleScope = CONTENT`)다. 눌림 배경 `bg.transparentPressed`, 색 전환 `colorTransition`.
- 높이 52(`x13`), `r3`, 여백 `x4`, 간격 `x2Half`, 글자 `t5`. 지우기 버튼 22px.
- 테두리는 6.3과 같다. 기본 1px 위에 오류 2px가 100ms로 겹친다. 포커스 테두리는 없다.
- 지우기 버튼은 바깥 표면 안에 넣지 않고 형제로 겹친다. 지우기를 눌러도 바깥이 눌리거나 줄지 않아야 한다. 지우기 버튼은 자기만 줄어든다(SEED `clearButton.scaleScope: self`). 비활성·읽기 전용이면 보이지 않는다.
- 읽기 전용이면 배경 `bg.disabled`이고 눌리지 않는다. 값 색은 그대로 `fg.neutral`이다.
- 접근성: 역할 Button. 이름은 field 라벨 + 값(없으면 placeholder)이다. `contentDescription`을 주면 field 라벨 대신 그 값이 이름의 앞부분이 된다(인자가 이긴다). field 밖에서 쓰면 `contentDescription`을 줘야 한다. 값이 있으면 앞뒤 글자를 보이는 순서대로 값에 붙인다. placeholder일 때는 붙이지 않는다. 값 글자와 placeholder 글자는 따로 읽히지 않는다(SEED 문서).

### 6.6 segmented: `PushlogResidentNumberInput`, `PushlogCardNumberInput`

- 공개 형식은 SEED registry 그대로다.
  - 주민등록번호: `digits` = `SIX`(생년월일 6 + 점 7개 고정), `SEVEN`(기본, 생년월일 6 + 뒷자리 첫 1 + 점 6개), `THIRTEEN`(생년월일 6 + 뒷자리 7 가림). 구분자 " - ".
  - 카드번호: 4자리 칸 넷, 구분자 " - ". 뒤에서 `maskedDigits`(`FOUR`, 기본 `EIGHT`)만큼 가린다. 끝에 `suffix` 슬롯(카드사 로고).
  - 칸별 접근성 이름과 placeholder 문구도 registry 그대로다.
- 상태: 형식마다 공개 상태 홀더 클래스(`PushlogResidentNumberState`, `PushlogCardNumberState`)와 그 `companion`의 `Saver`를 둔다. 호출하는 쪽은 `rememberSaveable(saver = PushlogResidentNumberState.Saver) { PushlogResidentNumberState() }`로 만든다. 공개 `remember…` 함수는 두지 않는다. `component` 패키지의 공개 `@Composable`은 미리보기 화면이 있어야 해서(`PreviewCoverageRulesTest`)다(설계자 판단). 값은 칸 순서대로 `List<String>`로 읽는다. 안에서는 칸마다 `TextFieldState`를 갖는다. `Saver`는 가린 칸까지 전부 저장한다(사용자 결정 §5).
- 인자: 상태, 형식 인자(`digits`/`maskedDigits`), `invalid`·`enabled`·`readOnly`(기본값은 field 상태), `modifier`(카드번호는 `suffix`).
- 상태 홀더에 모든 칸을 비우는 `clear()`를 둔다. 저장 상태에서 값이 언제 사라질지(제출 성공, 화면 종료)는 쓰는 화면이 정하고 `clear()`를 부른다.
- 비활성·읽기 전용: 배경 `bg.disabled`, 비활성이면 값·placeholder·점·구분자·고정 글자를 `fg.disabled`로(정의 파일). 둘 다 입력, 붙여넣기, Backspace로 앞 칸 지우기, 칸 자동 이동을 막는다. 읽기 전용은 포커스는 받는다. 오류는 6.3과 같은 2px `criticalSolid` 테두리.
- 동작(SEED 문서 `segmented-text-input.mdx` 전부):
  - 끝에서 입력해 칸을 채우면 다음 칸으로 이동한다. 가운데에 넣어 자리 수를 넘기는 입력은 버린다.
  - 빈 칸에서 Backspace는 앞 칸의 마지막 글자를 지운다. 끝에서 →, 처음에서 ←는 옆 칸으로 간다(하드웨어 키보드).
  - 가린 칸은 글자 대신 자리 수만큼 점을 그린다. 커서는 끝에만 있고 끝에서만 입력·삭제한다. ←·→는 옆 칸으로 간다.
  - 붙여넣으면 구분자를 지우고 다음 칸까지 차례로 채운다. 가린 칸 앞에서 멈춘다.
  - 생년월일 칸은 자동완성으로 들어온 `yyyy-MM-dd` 등을 6자리로 바꾼다. 가린 칸은 붙여넣기·자동완성으로 채우지 않는다.
- 자동완성: `ContentType`은 보이는 칸에만 붙인다. 주민등록번호는 생년월일 칸(생년월일), 카드번호는 첫 칸(카드번호, 들어온 값을 가린 칸 앞까지 나눠 채움). 가린 칸은 자동완성 대상에서 뺀다(`ContentType` 없음, 자동완성 제외 의미 정보). 그래서 자동완성 서비스가 가린 값을 읽거나 저장 제안에 담지 않는다(설계자 판단, 리뷰 반영 2026-11-17). 실제 자동완성 서비스 동작은 기기에서 확인한다(§7).
  - 한글 조합 중에는 다음 칸으로 이동하지 않는다.
  - 마지막 칸이 차면 포커스는 그 칸에 그대로 두고 더 들어오는 입력은 버린다. 완료 콜백은 두지 않는다. 호출하는 쪽이 상태 값을 보고 판단한다(설계자 판단).
- 모양: 크기 `large`만. 최소 높이 52, `r3`, 여백 `x4`, 간격 `x2Half`, 글자 `t5`. 테두리는 6.3과 같고, 칸 중 하나라도 포커스면 포커스 테두리를 그린다. 구분자·점·placeholder 색은 정의 파일대로.
- 가린 값은 접근성 트리에 글자로 나오지 않는다(비밀번호 입력처럼 자리 수만). `toString()`이나 로그로 새지 않게 상태 홀더의 `toString`을 값 없이 둔다. 저장 상태에는 들어간다(§5).
- 키보드는 모든 칸이 `KeyboardType.Number`다. Compose foundation 1.12.0에서 `NumberPassword`를 주면 텍스트 필드가 `ContentType.Password`를 붙이는데(TextFieldDecoratorModifier), 가린 칸을 자동완성에서 뺀다는 위 규칙과 어긋난다. 가리기는 `OutputTransformation` 점과 `password()` 의미 정보로, 자동완성 제외는 `contentDataType = ContentDataType.None`으로 한다.
- 칸 엔진은 `internal`이다. 값 분배 규칙(자리 수, 붙여넣기, 날짜 변환, 가린 칸 멈춤)은 엔진 단위 테스트로, 칸 사이 포커스 이동과 키 입력은 Compose UI 테스트로 덮는다(§7).

### 6.7 #35 선택 컨트롤 그룹과 `field`

SEED는 체크박스·라디오·select-box 그룹을 Fieldset(라벨·설명·오류)으로 감싸고, 마크 자체에는 오류 모양을 두지 않는다. 그래서 Pushlog도 `PushlogField`로 그룹을 감싸는 것만 한다.

- `PushlogCheckboxGroup`, `PushlogRadioGroup`, `PushlogSelectBoxGroup`이 `LocalPushlogFieldState`를 읽는다. 그룹 없이 field에 바로 넣은 `PushlogCheckbox`·`PushlogRadio`는 읽지 않는다. 공개 기본값 `enabled = true`를 바꾸지 않으려는 것이다(설계자 판단).
  - `enabled = false`면 안쪽 컨트롤의 `enabled` 기본값이 `false`가 된다. 컨트롤 인자가 덮어쓴다.
  - 그룹 의미 정보에 field 라벨(이름)과 `invalid`일 때 오류 문구(`error`)를 붙인다.
- 마크·줄의 그리기는 바꾸지 않는다. 그래서 #35 스크린샷은 바뀌지 않아야 한다. 빌더는 `verifyRoborazziDebug`로 확인한다.
- 스위치·세그먼트는 SEED에서 field로 감싸지 않으므로 손대지 않는다.

### 6.8 아이콘

- 지우기(`IconXmarkCircleFill`)와 오류 느낌표(`IconExclamationmarkCircleFill`)는 `@karrotmarket/react-monochrome-icon` 1.35.0(Apache-2.0)의 SVG 경로를 `Canvas`/`Path`로 그린다. #35 체크·대시와 같은 방식이다. 출처와 라이선스를 그리기 파일 주석에 적는다.
- 느낌표는 `component.field`, 지우기는 `component.clearbutton` 안 `internal`이다.

### 6.9 값 출처

- 모든 색·간격·모서리·글자·시간은 토큰이 있다. 확인한 토큰: `x0Half`~`x13`, `r2`·`r2Half`·`r3`, `t4`~`t6`, `fg.critical`·`placeholder`·`disabled`·`neutralSubtle`·`neutralMuted`, `stroke.neutralWeak`·`neutralContrast`·`criticalSolid`, `bg.neutralWeak`·`disabled`·`transparent`·`transparentPressed`, `colorTransition`.
- 파일 안 `private` 상수로 둘 값: 1px·2px 선, 100ms 테두리 전환, 높이 44(search), 94(textarea), 22px(지우기), 필수 표시의 sp 값 넷.

## 7. 테스트

공개 `@Composable`마다 미리보기 앱 Key·Screen·entry·catalog와 섹션별 `@PushlogPreview`(Light/Dark/Light font 2x). 캔버스 320x470을 넘지 않게 섹션을 나눈다.

직접 쓰는 스크린샷: 프리뷰로 못 그리는 상태만. 포커스 테두리, 오류 테두리 전환 끝, input-button 눌림, 가린 칸 점.

단위 테스트(최소):

- field: 오류일 때만 오류 문구가 보이고 설명은 화면에서 사라지지만 접근성 트리에 남는다. 글자 수는 `invalid`면 critical 색. 머리·꼬리는 내용이 없으면 그리지 않는다.
- field 상태 전달: field `invalid`/`enabled`/`readOnly`가 안쪽 입력에 반영된다. 입력 인자로 덮어쓸 수 있다. #35 그룹의 컨트롤이 field `enabled = false`면 탭해도 바뀌지 않는다.
- 접근성: 입력이 field 라벨을 이름으로, 오류를 `error`로 가진다. input-button은 Button 역할에 라벨 + 값.
- text-input: `maxGraphemeCount`가 문자소 기준으로 막는다(이모지 결합, 한글). 비활성이면 입력되지 않는다. 읽기 전용이면 수정되지 않고 선택·복사는 된다.
- search-input: 값이 있을 때만 지우기 버튼. 지우면 값이 빈다. 포커스 유지 규칙. 검색 키로 `onSubmit`이 값과 함께 한 번 불린다. 빈 값에서도 `onSubmit("")`이 불린다. `clearFocusOnSubmit = true`면 제출 뒤 포커스가 풀리고 `false`면 남는다.
- input-button: 지우기를 눌러도 `onClick`이 불리지 않고, 바깥 표면 배경과 내용 크기가 그대로다(눌림 주입 뒤 픽셀 측정). 비활성·읽기 전용이면 `onClick`이 안 불리고 지우기 버튼이 없다. `contentDescription`이 field 라벨보다 우선한다.
- segmented 엔진(JVM 단위): 자리 수 초과 버림, 붙여넣기 분배와 가린 칸 앞 멈춤, `yyyy-MM-dd` 등 날짜 → 6자리, 마지막 칸이 찬 뒤 입력 버림, `clear()`.
- segmented UI(Robolectric, `performTextInput`·`performKeyInput`): 칸을 채우면 다음 칸으로 포커스, 빈 칸 Backspace가 앞 칸 마지막 글자를 지움, 끝에서 →·처음에서 ← 이동, 가린 칸은 끝에서만 입력·삭제. 비활성·읽기 전용이면 입력·이동이 막힌다. 가린 칸 값이 의미 정보에 없다.
- segmented 상태 `toString()`: `PushlogResidentNumberState`·`PushlogCardNumberState`에 숫자를 넣은 뒤 `toString()`에 그 숫자가 없다.
- field 상태 경계: field로 감싼 select-box 그룹의 footer 안 입력은 바깥 `invalid`·라벨·`enabled`를 물려받지 않는다. field 안의 field도 같다.
- segmented `Saver`: 저장·복원 뒤 모든 칸 값이 같다.
- `ComponentRulesTest`, `PreviewCoverageRulesTest`가 새 패키지를 잡는지 위반을 넣어 한 번 확인한다.

수동 확인(기기, PR 본문에 결과를 적는다). Compose 테스트 API로는 조합 중 글자나 실제 자동완성 서비스를 넣을 수 없어서다.

- 한글 조합: Gboard와 삼성 키보드에서 text-input에 "한글"을 천천히 친다. 조합 중 글자가 되돌아가거나 커서가 튀지 않는다. search-input에서 조합 중 검색 키를 누르면 조합만 확정되고 `onSubmit`은 확정 뒤 한 번만 불린다. segmented 칸에서 조합 중에는 다음 칸으로 넘어가지 않는다.
- 자동완성: Google 자동완성이 켜진 기기에서 생년월일·카드번호 제안이 보이는 칸에만 뜬다. 가린 칸 값은 저장 제안에 들어가지 않는다.
- 접근성 포커스: TalkBack을 켜고 search-input 지우기 버튼을 누르면 접근성 포커스가 입력으로 간다. field 라벨·설명·오류가 한 번씩만 읽힌다.
- 키보드 종류: 가린 칸은 숫자 비밀번호, 나머지는 숫자 키보드가 뜬다.

## 8. PR 나누기

| PR | 브랜치 | 내용 | 선행 |
|---|---|---|---|
| PR1 | `feat/design-system-text-input` | 이 스펙과 계획 세 개, field·field-label, text-input·textarea, `graphemeCount`, #35 그룹 연결(§6.7), 느낌표 아이콘, 문서 `components/input.md`(화면 검토 책임 문장 포함), `open-questions.md` 상태 보관 위치 미결 | develop |
| PR2 | `feat/design-system-search-input-button` | search-input, input-button, `component.clearbutton` | PR1 머지 |
| PR3 | `feat/design-system-segmented-text-input` | segmented 두 형식과 칸 엔진 | PR1 머지 |

- PR2와 PR3은 서로 의존하지 않는다. 둘 다 PR1이 머지된 develop에서 딴다. 스택 PR은 쓰지 않는다(#35와 같다).
- 각 PR은 `Part of #36`, 마지막으로 머지되는 PR이 `Closes #36`. 머지 뒤 이슈는 직접 닫는다(git-flow).
- 계획 파일: `docs/superpowers/plans/2026-11-17-text-inputs-pr1.md`, `-pr2.md`, `-pr3.md`. 각 계획 맨 앞에 시작할 때 읽을 것(이 스펙, 디자인 시스템 문서, rules 파일, 해당 정의·registry URL)을 적는다.
- 디자인 시스템 README 작업 표에 `components/input.md` 줄을 더한다.

## 9. 설계자 판단 (바꿔도 됨)

- `medium`·`responsive` 크기 제외(승인됨).
- 한 줄·여러 줄을 함수 둘로, 장식 상자 공유. size enum 없이 variant가 메트릭을 돌려줌.
- 글자 수는 field가 세지 않고 `graphemeCount` 확장으로 호출하는 쪽이 넘김.
- segmented는 SEED 두 형식만 공개, 칸 엔진은 `internal`.
- input-button 지우기 버튼은 바깥 표면의 형제로 겹침.
- #35 그룹은 field 상태를 읽어 `enabled` 기본값과 접근성 정보만 받고, 마크 모양은 그대로.
- 필수 표시의 "font scaled" 값은 sp로 둠.
- 아이콘은 SEED 아이콘 경로를 Canvas로 그림.
- search-input 기본 접근성 문구 "검색", "지우기".
- field 상태는 받는 쪽에서 끊김. 그룹 없는 체크박스·라디오는 field 상태를 읽지 않음.
- 화면에서 `TextFieldState`와 segmented 상태를 Route가 들지 ViewModel이 들지는 정하지 않는다. 쓰는 화면이 없어서다. `docs/synthesis/open-questions.md`에 미결로 올린다(PR1).
- 공개 `remember…` 함수 없이 상태 클래스 + `Saver`. `graphemeCount`는 `@Composable` 아닌 프로퍼티.
- 라벨·설명·오류는 입력·그룹 노드 하나에만, field 머리·꼬리 글자는 접근성 트리에서 숨김.
