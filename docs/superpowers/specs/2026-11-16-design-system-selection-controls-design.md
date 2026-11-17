# 디자인 시스템 선택 컨트롤 설계

- 작성일: 2026-11-16
- 상태: 승인됨 (2026-11-16)
- 이슈: #35 (상위 #20, 최상위 #17)
- 범위: `core:designsystem`의 선택 컨트롤 다섯 종(checkbox, radio, switch, segmented-control, select-box)과 하위 부품, 그리고 이들이 함께 쓰는 `pressable` 부품 확장. 글자 입력(#36), 값 조절(#37), 시트·선택기 입력(#38)은 범위 밖이다.

## 1. 의도

SEED Design의 선택 컨트롤을 정의 그대로 Compose로 옮긴다. 범위 원칙은 "deprecated만 빼고 SEED 전부"다(사용자 결정, 2026-11-16). 그래서 Pushlog 화면에 지금 쓸 곳이 있는지가 아니라 SEED에 있는지로 포함 여부를 정한다.

- 값 기준: SEED rootage 정의, 기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`. 파일은 `https://raw.githubusercontent.com/daangn/seed-design/5f25880e93fe6f6eedba6f3842805cd8f6f352d1/packages/rootage/components/<name>.yaml`로 받는다. 대상: `checkbox`, `checkbox-group`, `checkmark`, `radio`, `radio-group`, `radiomark`, `switch`, `switchmark`, `segmented-control`, `segmented-control-item`, `segmented-control-indicator`, `select-box`, `select-box-group`, `select-box-checkmark`.
- 사용 기준: https://seed-design.io/components/checkbox, /radio, /switch, /segmented-control, /select-box.
- 이 문서는 정의 파일의 값을 다 옮겨 적지 않는다. 값은 정의 파일이 정본이고, 이 문서는 Pushlog에서 정한 것과 정의 파일만으로 알 수 없는 것을 적는다.

## 2. 목표 · 비목표 · 반목표

목표

- 다섯 종과 공개 하위 부품을 SEED 정의의 색·크기·모서리·글자·모션대로 만든다(라이트/다크).
- #19의 `pressable` 부품을 넓혀, 탭 영역과 눌림 축소 대상이 다른 컨트롤도 같은 부품으로 만든다.
- 공개 `@Composable`마다 `@PushlogPreview`, 미리보기 앱 화면, 라이트/다크 스크린샷을 둔다.
- 다음 세션이 계획만 따라 구현할 수 있게, PR 세 개로 나눈 계획을 함께 쓴다(§7).

비목표

- 오류(invalid) 상태, 그룹 라벨·설명·오류 문구, 필수 표시. 이것들은 `field`가 감싸는 일이고 `field`는 #36 대상이다. #36에서 `field`를 만들 때 이 컨트롤들에 오류 상태를 붙인다.
- 세그먼트 항목의 알림 배지. 배지(`notification-badge`)는 #21 대상이다. #21 뒤에 붙인다.
- select-box의 "최대 선택 수에 닿으면 막고 Snackbar로 알림". 막는 것은 호출하는 쪽이 `onClick`에서 하고, Snackbar는 #21이다.
- 아이콘 세트. 체크·대시 그림은 컴포넌트가 직접 그린다(§6.3).
- 햅틱.

반목표 (돌아가도 실패로 보는 것)

- 눌렀을 때 ripple·state layer가 보이는 것.
- `pressable` 밖에서 `toggleable`·`selectable`·`triStateToggleable`을 직접 쓰는 것. `ComponentRulesTest`가 막는다. 규칙을 약화하지 않는다.
- 줄 전체가 탭 영역인 컨트롤에서 줄 전체가 줄어드는 것. SEED는 마크만 줄인다.
- 라벨을 탭해도 상태가 안 바뀌는 것.

## 3. 제약

- `AGENTS.md` 금지 사항, `docs/code-conventions.md` 주석 규칙.
- `docs/architecture/design-system.md` "동작 컴포넌트" 절 규칙과 `.claude/rules/design-system-components.md`:
  - 컴포넌트는 `component.<종류>` 하위 패키지에 둔다.
  - 크기별 값은 `PushlogXxxSize.metrics()`가 `XxxMetrics`로, 색은 `PushlogXxxVariant`(이 문서에서는 tone 등)의 `internal` 멤버가 `when (this)`로 돌려준다.
  - SEED px 값은 같은 크기 토큰으로 옮기고, 없으면 파일 안 `private` 상수에 SEED 출처 주석을 단다.
  - 공개 `@Composable`마다 `<함수>PreviewKey`·`<함수>PreviewScreen.kt`와 섹션별 `@PushlogPreview`.
  - `XxxDefaults`는 만들지 않는다.
- 파일은 최상위 타입마다 하나다(`FileStructureRulesTest`).
- 팔레트 값(`gray-300` 등)은 `pushlogThemedColor(LightPalette.x, DarkPalette.x)`로 읽는다.

## 4. 접근 방식

| 방식 | 내용 | 대가 | 결과 |
|---|---|---|---|
| A. `pressable`에 "탭 영역"과 "눌림 표시"를 나눈 부품을 더한다 | 줄(Row)에 탭 modifier, 마크에 눌림 표시 modifier. 둘이 `MutableInteractionSource` 하나를 같이 쓴다 | `pressable` 공개 부품이 늘어난다 | **선택(설계자 판단)** |
| B. 컨트롤마다 `pressable` 안에 전용 표면을 만든다 | `pressable`에 checkbox 줄, radio 줄을 직접 둔다 | 컴포넌트 코드가 `pressable`로 새어 들어가고 패키지 경계가 무너진다 | 탈락 |
| C. M3 `Checkbox`·`Switch`·`SegmentedButton` 감싸기 | 색만 토큰으로 | ripple, 크기, 모션이 SEED와 다르다. `ComponentRulesTest`가 막는다 | 탈락 |

## 5. 사용자가 정한 것

- 범위: deprecated만 빼고 SEED 전부(2026-11-16).
- #20을 #35~#38로 나누고, 하위 이슈마다 PR을 따로 낸다.
- #35는 순차 PR 세 개로 낸다(§7). PR1을 develop에 머지한 뒤 develop에서 PR2·PR3 브랜치를 딴다. 스택 PR은 쓰지 않는다.
- 스펙과 계획을 먼저 다 쓰고, 다음 세션들은 계획을 따라 구현만 한다.

## 6. 설계

### 6.1 `pressable` 부품 확장 (internal)

지금 있는 것: `Modifier.pushlogPressable`(클릭), `Modifier.pushlogToggleablePressable`(토글, `Role.Checkbox` 고정), `PushlogPressableSurface`(색·축소·탭·테두리·그림자를 한 표면에), `PressScaleNode`, `PressColorNode`.

더할 것:

1. **탭 영역 modifier** (시각 효과 없음): 줄 전체에 붙인다. 셋이 필요하다.
   - 3상태 토글: `triStateToggleable`, `Role.Checkbox`. 체크박스 줄.
   - 단일 선택: `selectable`, `Role.RadioButton`. 라디오 줄, 세그먼트 항목, 단일 선택 select-box.
   - 토글: `toggleable`, 역할을 인자로(`Role.Switch`, `Role.Checkbox`). 스위치 줄, 다중 선택 select-box.
   - 모두 `indication = null`, `enabled`, `interactionSource`를 받는다.
2. **눌림 표시 modifier** (탭 처리 없음): 마크에 붙인다. 같은 `interactionSource`를 보고 `PressScaleNode`(self 축소)와 `PressColorNode`(표면 색)를 건다. 탭 영역이 축소 층 바깥에 있으므로 #19의 "탭 판정은 줄지 않는다" 규칙이 그대로 지켜진다. 축소는 끌 수 있어야 한다. SEED는 select-box와 리스트 줄 안의 마크는 축소하지 않고 색만 바꾼다(감싸는 쪽이 이미 줄이므로). 스위치 마크는 반대로 눌림 색이 없고 축소만 있다.
3. **내용 축소** (`scaleScope: content`): 표면 색·테두리는 그대로 두고 안의 내용만 줄인다. select-box 카드와 세그먼트 항목이 쓴다. 배율은 SEED처럼 **바깥 표면 크기**로 계산한다(SEED `scale-feedback.ts`: "The ratio still comes from the measured element's size"). 지금 `PressScaleNode`는 자기 층 크기로 계산하므로, 바깥 크기를 넘겨받는 방식이 필요하다. `PushlogPressableSurface`에 축소 대상을 고르는 인자를 더하는 방식이 기본안이다.
4. `pushlogToggleablePressable`의 역할을 인자로 받게 한다(기본값 `Role.Checkbox`로 지금 칩 동작 유지).
5. **단일 선택 표면**: `PushlogPressableSurface`에 selectable(`Role.RadioButton`) 모드를 더한다. 세그먼트 항목과 단일 선택 select-box가 쓴다.
6. **눌릴 때만 보이는 테두리**: 세그먼트 항목은 눌릴 때만 1px `stroke.neutralMuted` 테두리가 생긴다. 표면이 눌림 테두리 색을 받게 한다.

이 부품들은 PR1에서 모두 만든다. PR1에서 쓰는 컴포넌트가 없는 것(내용 축소, 단일 선택 표면, 눌림 테두리, 역할 인자)은 `pressable` 단위 테스트로 검증한다. PR2·PR3이 각자 더하면 둘이 같은 파일을 고쳐 독립이 깨지므로 그렇게 하지 않는다.

축소 기준 길이 공식(max(높이, 너비÷4, 24dp))은 그대로 쓴다. 마크 20dp는 기준 24dp라 22/24로 줄어든다. 애니메이션 줄이기 설정, 비활성, 로딩에서 줄이지 않는 규칙도 그대로다.

### 6.2 패키지와 공개 API

| 패키지 | 공개 `@Composable` | SEED |
|---|---|---|
| `component.checkbox` | `PushlogCheckmark`, `PushlogCheckbox`, `PushlogCheckboxGroup` | checkmark, checkbox, checkbox-group |
| `component.radio` | `PushlogRadiomark`, `PushlogRadio`, `PushlogRadioGroup` | radiomark, radio, radio-group |
| `component.switches` | `PushlogSwitchmark`, `PushlogSwitch` | switchmark, switch |
| `component.segmentedcontrol` | `PushlogSegmentedControl` | segmented-control(+item, indicator) |
| `component.selectbox` | `PushlogSelectBox`, `PushlogSelectBoxGroup` | select-box(+group, checkmark) |

- `switch`는 Kotlin 예약어라 패키지 이름을 `switches`로 한다(설계자 판단).
- 마크(`PushlogCheckmark`·`PushlogRadiomark`·`PushlogSwitchmark`)는 공개한다. SEED에도 따로 있고, #22 리스트 줄이 조합해 쓴다(설계자 판단). 마크는 탭을 처리하지 않는다. `interactionSource`를 받아 눌림만 보여 주고, 축소를 끄는 인자를 받는다(§6.1-2). 마크만 단독으로 쓰면 호출하는 쪽이 줄에 탭 영역을 붙인다. feature 모듈에서는 `pressable`이 `internal`이라 `toggleable`을 직접 써야 하므로, KDoc에 `indication = null`을 쓰라고 적는다(Konsist 범위 밖이라 검사로는 못 막는다).
- 그룹(`...Group`)은 간격(SEED group 정의)과 접근성 묶음(`selectableGroup`)을 맡는 얇은 `Column`이다. 공개라서 미리보기 화면이 필요하다.
- select-box의 다중 선택 표시는 `PushlogCheckmark`가 아니라 `select-box-checkmark` 정의(20dp 칸, 15dp 아이콘, 색 `fg.placeholder` → 눌림 `fg.neutralSubtle` → 선택 `fg.neutral`)를 쓰는 `internal` 부품이다. 단일 선택 표시는 `PushlogRadiomark`를 SEED처럼 size `MEDIUM`, tone `NEUTRAL`로 고정해 쓴다. 둘 다 축소를 끈다.
- 세그먼트 항목·indicator는 `internal`이다. 항목은 `PushlogSegmentedControl`의 인자(라벨 목록 또는 항목 슬롯)로 받는다.

### 6.3 체크박스 · 체크 마크

- 줄 = 마크 + 라벨. 줄 전체가 3상태 토글 탭 영역(§6.1-1)이다. 마크만 줄어든다.
- 상태는 `ToggleableState`(On, Off, Indeterminate)로 받는다. 부모의 일부 선택 계산은 호출하는 쪽이 한다(설계자 판단, Compose `TriStateCheckbox` 방식). 편의 오버로드로 `checked: Boolean`도 둔다.
- variant: `SQUARE`, `GHOST`. tone: `NEUTRAL`, `BRAND`. size: `MEDIUM`, `LARGE`. 라벨 굵기: `REGULAR`, `BOLD`.
- 기본값: rootage yaml에는 기본값이 없다. SEED React recipe(`packages/qvism-preset/src/recipes/*.ts`, 같은 기준 커밋)의 `defaultVariants`를 따른다. variant `SQUARE`, size `MEDIUM`, 굵기 `REGULAR`. tone만 예외로 recipe의 brand 대신 SEED 문서가 권하는 `NEUTRAL`을 쓴다(설계자 판단). 이 tone 규칙은 라디오·스위치에도 같다.
- ghost의 "선택 + 눌림"에서 SEED는 마크 배경(root)을 `palette.carrot-200`으로 칠하고 아이콘은 그대로 `fg.brand`다. Pushlog 브랜드는 forest라서 brand는 `pushlogThemedColor(LightPalette.forest200, DarkPalette.<단계>)`, neutral은 `gray-200`을 같은 방식으로 쓴다. 다크 단계는 `fg.brand` 아이콘과의 대비가 3:1 이상인 단계로 빌더가 재서 정하고 `design-system.md`에 근거를 남긴다. foundation이 다크 brand solid를 두 단계 낮춘 것과 같은 이유(브랜드 위 대비)다.
- 체크·대시 그림은 SEED 체크박스가 쓰는 채운 아이콘(`@karrotmarket/react-monochrome-icon` 1.35.0(Apache-2.0)의 `IconCheckmarkFatFill`, `IconMinusFatFill`)의 SVG 경로를 `Canvas`/`Path`로 그린다. 아이콘 자원과 의존을 늘리지 않으려는 것이다. 그리기 함수는 PR1에서 `component.checkbox` 안 `internal` 함수로 두고 PR3 select-box 체크 마크가 같이 쓴다.

### 6.4 라디오 · 라디오 마크

- 체크박스와 같은 줄 구조. 단일 선택 탭 영역(§6.1-1), `Role.RadioButton`.
- 가운데 점 크기는 비활성일 때 커진다(정의 파일 값). 비활성 꺼짐·선택의 `gray-300` 채움·테두리도 정의 파일대로다.
- `PushlogRadioGroup`은 `selectableGroup()`을 건다.

### 6.5 스위치 · 스위치 마크

- 크기는 SEED가 높이(16, 24, 32)로 부른다. enum은 `SMALL`(16), `MEDIUM`(24), `LARGE`(32)로 짓고 각 항목에 높이를 주석으로 단다(설계자 판단, Kotlin 이름은 숫자로 시작할 수 없다). 기본값은 recipe대로 `LARGE`(32).
- 16 크기도 줄 높이 24dp를 지킨다(정의 파일).
- 줄 전체가 토글 탭 영역(`Role.Switch`). 라벨은 선택이다. 라벨 없이 쓰면 접근성 이름을 받아야 한다(아이콘만 버튼과 같은 이유). 라벨 위치는 SEED 문서대로 마크 다음이다. 리스트 끝에 스위치를 두는 배치는 #22 리스트 줄이 마크로 조합한다.
- 모션: thumb 이동·크기(꺼짐 0.8배, 켜짐 1.0배)는 d3 150ms, 색은 d1 50ms에 20ms 지연. 지연이 있어서 색은 `PressColorNode`가 아니라 스위치 마크가 직접 애니메이션한다. 기기의 애니메이션 배율 0은 Compose가 `MotionDurationScale`로 자동 반영하므로 따로 처리하지 않는다. SEED는 reduced-motion에서 눌림 축소만 끄고 스위치 전환은 정하지 않는다. 그래서 전환이 즉시 바뀌는 것은 Compose 기본 동작을 따른 Pushlog 판단이다.
- 비활성: 마크 투명도 0.38, 라벨 0.58(정의 파일).
- 쓰는 법(KDoc 한 줄): 바로 반영되는 설정에만 쓴다. 저장을 눌러야 반영되면 체크박스.

### 6.6 세그먼트 컨트롤

- 항목 2~4개. 2개 미만이나 5개 이상이면 `require`로 막는다(SEED "5개 이상 금지", 설계자 판단).
- 각 항목은 단일 선택 탭 영역, `Role.RadioButton`, 컨테이너에 `selectableGroup()`.
- 항목은 모두 같은 너비다. SEED recipe가 `grid-auto-columns: 1fr`이고 indicator 너비를 `(100% - padding×2) / 항목 수`, 위치를 `index × 100%`로 정한다. 그래서 indicator는 칸 나누기로 계산하고 위치를 재지 않는다. 컨테이너 너비는 `modifier`가 정한다. 제약이 없으면 가장 넓은 항목(최소 86) × 항목 수가 된다. 높이도 같다(`grid-auto-rows: 1fr`). 컨테이너가 좁으면 같은 너비로 나뉘고 라벨이 줄바꿈된다(`max-width: 100%`). Compose에서는 `IntrinsicSize`로 맞춘 행에 `weight(1f)`를 주는 방식이 된다.
- indicator는 선택 칸으로 d4 200ms 이동한다.
- 항목 눌림: 표면 `bg.neutralWeakPressed` + 1px `stroke.neutralMuted`(눌릴 때만, §6.1-6), 내용만 축소(§6.1-3).
- 선택된 항목을 누르면 SEED recipe처럼 그 항목이 자기 배경(`palette.gray-100`)과 indicator와 같은 1dp 테두리를 칠해 indicator를 덮는다. indicator 색은 늘 `palette.gray-00`이다.
- 비활성은 컨트롤 전체(`enabled`)와 항목별 둘 다 받는다. SEED React도 둘 다 받고, 항목은 `항목 disabled || 전체 disabled`다. 비활성 + 선택이면 그 항목이 `bg.disabled` 배경과 1dp 테두리로 indicator를 덮는다.
- 쓰는 법(KDoc): 화면 이동에 쓰지 않는다(Tabs와 구분). 화면당 하나.

### 6.7 select-box

- 카드 전체가 하나의 탭 영역이고, 눌림은 카드 전체 색 + 내용 축소(§6.1-3).
- 내용: 앞 아이콘(선택), 라벨, 설명(선택, 최대 2줄), 뒤쪽 `suffix` 슬롯(선택, SEED에 있음), 컨트롤, 아래쪽 영역(선택).
- 선택 상태는 호출하는 쪽이 갖는다. 각 박스가 `selected`와 `onClick`을 받는다. 그룹은 배치와 `selectableGroup()`만 맡고 상태를 갖지 않는다(설계자 판단, 다른 컨트롤과 같은 방식).
- 선택 방식은 박스의 `type: PushlogSelectBoxType`(`SINGLE`, `MULTIPLE`)이 정한다. `SINGLE`은 라디오 마크와 `Role.RadioButton`(selectable), `MULTIPLE`은 체크 마크와 `Role.Checkbox`(toggleable)다. 마크를 숨기려면 `showControl = false`를 준다. 숨겨도 역할은 `type`을 따른다. SEED의 컨트롤 세 가지(Checkmark, Radiomark, 없음)를 이 두 인자로 표현한다(설계자 판단). 그룹 밖에서 써도 같다.
- 선택되면 2px `stroke.neutralContrast` 테두리가 100ms 페이드로 겹친다.
- 아래쪽 영역(footer) 표시는 SEED처럼 세 가지다: 선택됐을 때(기본), 항상, 선택 안 됐을 때. 열고 닫을 때 높이 400ms/300ms, 투명도 300ms/400ms.
- `layout`(horizontal / vertical)은 SEED처럼 **박스** 인자다. 그룹은 기본값만 준다. 열이 2개 이상이면 그룹 기본이 vertical이고, 박스마다 덮어쓸 수 있다. 그룹 밖 단독 박스도 `layout`을 받는다.
- `PushlogSelectBoxGroup`: 열 최대 3개. 여러 열이면 줄마다 높이를 맞춘다(`grid-auto-rows: 1fr`). 그룹 간격은 정의 파일 값.
- SEED 공식 플랫폼에 Android가 없다. 정의 파일 값을 그대로 쓰되, 터치 영역은 Compose 최소 판정 영역을 따른다.

### 6.8 값 출처

- 토큰이 있으면 토큰, 없으면 파일 안 `private` 상수 + SEED 출처 주석.
- 상수로 둘 것으로 보이는 값: 1px·2px 선, 아이콘 12/14/15/18/22, 스위치 track 52/38/26·thumb 26/20/12·padding 3(32) / 2(24·16), 세그먼트 항목 86×34, 100ms·400ms·20ms 지연, 0.8배, 0.38/0.58. 같은 크기 토큰이 있으면 토큰을 쓴다.
- 없는 토큰은 하나뿐이다: `palette.carrot-200` → §6.3.

### 6.9 미리보기와 테스트

- 공개 `@Composable`마다 미리보기 앱 Key·Screen·entry·catalog와 섹션별 `@PushlogPreview`(Light/Dark/Light font 2x). 캔버스 320x470을 넘지 않게 섹션을 나눈다.
- 직접 쓰는 스크린샷 테스트는 프리뷰로 못 그리는 상태만: 눌림(Press 주입), 애니메이션 중간이 아닌 끝 상태, indicator 이동 뒤.
- 단위 테스트(최소):
  - 라벨을 탭하면 상태가 바뀐다(체크박스·라디오·스위치).
  - 줄을 눌러도 마크만 줄고 라벨은 그대로다(픽셀 측정).
  - `pressable` 단위 테스트(PR1): 내용 축소 배율이 바깥 크기 기준이다, 단일 선택 표면의 역할이 RadioButton이다, 눌림 테두리는 눌릴 때만 보인다, 마크 눌림 표시에서 축소를 끄면 색만 바뀐다.
  - 역할: Checkbox(3상태 포함), RadioButton, Switch가 접근성 트리에 드러난다. 그룹에 `selectableGroup`.
  - 비활성이면 탭해도 바뀌지 않는다(실제 클릭 수 확인).
  - 라벨 없는 스위치는 접근성 이름을 갖는다.
  - 세그먼트 항목이 1개나 5개 이상이면 실패한다. 선택을 바꾸면 indicator가 새 칸으로 간다. 항목 하나만 비활성이면 그 항목만 안 바뀐다.
  - select-box footer 표시 세 가지가 각각 맞게 보이고 사라진다. select-box 안의 마크는 줄지 않는다(내용 축소만).
- `ComponentRulesTest`, `PreviewCoverageRulesTest`가 새 패키지를 잡는지 확인한다(위반을 넣어 실패하는지 한 번 본다).

## 7. PR 나누기

| PR | 브랜치 | 내용 | 선행 |
|---|---|---|---|
| PR1 | `feat/design-system-selection-controls` | §6.1 `pressable` 확장 전부, 체크박스·체크 마크·그룹, 라디오·라디오 마크·그룹, 이 스펙과 계획 세 개 | develop |
| PR2 | `feat/design-system-switch-segmented` | 스위치·스위치 마크, 세그먼트 컨트롤 | PR1 머지 |
| PR3 | `feat/design-system-select-box` | select-box·그룹 | PR1 머지 |

- PR2와 PR3은 서로 의존하지 않는다. 둘 다 PR1이 머지된 develop에서 딴다.
- 각 PR은 `Part of #35`로 연결하고, PR3(또는 마지막으로 머지되는 PR)이 `Closes #35`다. 머지 뒤 이슈는 직접 닫는다(git-flow).
- 계획 파일: `docs/superpowers/plans/2026-11-16-selection-controls-pr1.md`, `-pr2.md`, `-pr3.md`. 각 계획은 그 PR만으로 끝나고, 시작할 때 읽을 것(이 스펙, 디자인 시스템 문서, rules 파일, 해당 정의 파일 URL)을 맨 앞에 적는다.

## 8. 설계자 판단 (바꿔도 됨)

- 접근 방식 A(탭 영역과 눌림 표시 분리).
- 마크 세 개 공개, 그룹 공개.
- tone 공개, 기본값 neutral.
- indeterminate는 호출하는 쪽이 계산(`ToggleableState`).
- 체크·대시는 Canvas로 그림.
- 스위치 크기 이름 SMALL/MEDIUM/LARGE(16/24/32), 패키지 `switches`.
- 세그먼트 5개 이상은 `require`.
- 기본값은 SEED React recipe `defaultVariants`, tone만 neutral.
- select-box는 `type`(SINGLE/MULTIPLE) + `showControl`. 상태는 호출하는 쪽.
- 세그먼트 항목은 같은 너비, indicator는 칸 나누기.
- ghost 체크 "선택 + 눌림" 마크 배경은 `forest200`(brand) / `gray-200`(neutral), 다크 단계는 대비를 재서 정함.
- 세그먼트 비활성은 전체와 항목별 둘 다, 항목 2~4개를 `require`.
- select-box `layout`은 박스 인자, footer 표시 세 가지, `suffix` 슬롯 포함.

## 9. 빌더에게 맡기는 것

- 공개 API의 인자 이름과 순서(필수 → `modifier` → 선택).
- 세그먼트 항목을 라벨 목록으로 받을지 슬롯으로 받을지.
- 내용 축소를 `PushlogPressableSurface` 인자로 할지 별도 modifier로 할지(§6.1-3 기본안은 인자).
