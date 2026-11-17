---
id: design-system-selection
title: 디자인 시스템 선택 컨트롤
category: architecture
status: living
platforms: android
verified: 2026-11-17
related_spec: [docs/superpowers/specs/2026-11-16-design-system-selection-controls-design.md]
related_adr:
related_architecture: [design-system]
related_code: [PushlogCheckmark, PushlogCheckbox, PushlogCheckboxGroup, PushlogRadiomark, PushlogRadio, PushlogRadioGroup, PushlogSwitchmark, PushlogSwitch, PushlogSegmentedControl, PushlogSegmentedControlOption, SelectionControlRow, PushlogSelectBox, PushlogSelectBoxGroup]
tags: [architecture, design-system]
---

# 디자인 시스템 선택 컨트롤

체크박스, 라디오, 스위치, 세그먼트 컨트롤, select-box와 그 그룹을 다룬다. 모두 [`pressable` 부품](../pressable.md#pressable-부품) 위에 얹었다. 결정 배경은 [선택 컨트롤 스펙](../../../superpowers/specs/2026-11-16-design-system-selection-controls-design.md).

- 패키지는 `checkbox`(`PushlogCheckmark`, `PushlogCheckbox`, `PushlogCheckboxGroup`), `radio`(`PushlogRadiomark`, `PushlogRadio`, `PushlogRadioGroup`), `switches`(`PushlogSwitchmark`, `PushlogSwitch`), `segmentedcontrol`(`PushlogSegmentedControl`; 항목과 indicator는 `internal`), `selectbox`(`PushlogSelectBox`, `PushlogSelectBoxGroup`; 체크 마크와 footer는 `internal`)이다. `switches`인 것은 `switch`가 Kotlin 예약어라서다. 마크(`PushlogCheckmark`, `PushlogRadiomark`, `PushlogSwitchmark`)는 공개하지만 탭을 처리하지 않는다. #22 리스트 줄이 마크를 조합해 쓰기 때문이다.
- 체크박스·라디오·스위치 줄은 `component.selection`의 `internal SelectionControlRow`를 함께 쓴다. 마크 위쪽 여백과 라벨 위치 보정이 컨트롤마다 갈라져 한 곳만 고쳐지는 일을 막으려는 것이다. 탭은 이 줄이 처리하지 않고 호출하는 쪽이 `tapArea`로 넘긴다.
- `pressable` 밖의 컴포넌트가 `Modifier.toggleable`을 직접 붙이지 못하는 규칙(`ComponentRulesTest`)은 디자인 시스템 안의 컴포넌트에만 적용된다. 마크 KDoc이 단독 사용자에게 `Modifier.toggleable`을 붙이라고 안내하는 것은 디자인 시스템 밖에서 마크를 조합하는 쪽(#22 등)을 가리킨다.
- 줄 전체가 탭 영역이고 마크만 줄어든다(SEED). 라벨을 눌러도 상태가 바뀐다. 탭 판정이 축소 층 바깥에 있어서 `edgeTapSurvivesShrink` 규칙이 그대로 지켜진다. 마크 20dp는 축소 기준 길이가 24dp라 22/24로 준다.
- 상태 3개(`ToggleableState`)와 부모의 일부 선택 계산은 호출하는 쪽이 한다. `checked: Boolean` 오버로드는 편의용이다.
- 기본값은 SEED recipe의 `defaultVariants`를 따르되 tone만 `NEUTRAL`이다(SEED 문서 권장, recipe는 brand).
- 체크와 대시는 SEED 체크박스가 쓰는 채운 아이콘(`IconCheckmarkFatFill`, `IconMinusFatFill`)의 경로를 `Canvas`로 그린다. 아이콘 자원과 의존을 늘리지 않으려는 것이다. 그리기 함수는 `CheckmarkIconDrawing.kt`에 따로 둬서 select-box 체크 마크도 같이 쓸 수 있게 했다.
- 마크의 위쪽 여백은 `(rowMinHeight - markSize) / 2`로 고정이고, 라벨의 위쪽 여백은 `(rowMinHeight - 첫 줄 높이) / 2`다(SEED 위치 보정). 기본 글자 크기에서는 마크와 첫 줄의 가운데가 맞고 라벨이 여러 줄이어도 마크는 첫 줄 옆에 남는다. 글자가 커져 첫 줄이 `rowMinHeight`보다 높아지면 라벨 여백은 0으로 잘리고 마크는 첫 줄 가운데보다 조금 위에 놓인다.
- 스위치 마크는 축소만 하고 눌림 색이 없다(SEED). 트랙·thumb 색 전환에 20ms 지연이 있어서 `PressColorNode`로 못 하고 마크가 직접 애니메이션한다. 애니메이션 배율 0은 Compose `MotionDurationScale`에 맡긴다. SEED는 reduced-motion에서 전환을 정하지 않았다.
- 스위치 크기 `SMALL`/`MEDIUM`/`LARGE`는 마크 높이 16/24/32dp다. `SMALL`도 줄 높이는 24dp이고 기본값은 `LARGE`다. SEED recipe는 brand 켜짐 thumb에 `fg.onBrandSolid` 대신 `static-white`를 쓰는데 Pushlog에서는 같은 값이라 yaml을 따랐다.
- 스위치 `label`은 필수 `String`이고 `showLabel = false`여도 줄의 접근성 이름(`contentDescription`)으로 남는다. `String?`로 두면 이름 없는 스위치가 컴파일되므로 막았다. 비활성 라벨의 alpha 0.58은 그리기 단계 람다로 읽어 재구성을 피한다. thumb 이동은 `Modifier.offset {}` 람다로 해서 RTL에서 방향이 뒤집히고(`supportsRtl`) 이동 중에는 다시 배치만 한다.
- 세그먼트 항목은 슬롯이 아니라 라벨 목록(`PushlogSegmentedControlOption`)으로 받는다. 같은 크기·글자 색 규칙을 지키려는 것이고, 배지는 #21 뒤 필드로 더한다. 항목은 2~4개이며 `require`로 막는다.
- 세그먼트 칸은 자연 너비 = max(가장 넓은 항목, 86dp) × 항목 수를 제약 안으로 자른 전체 너비를 나눈 같은 너비다. 86dp를 항목 modifier에 걸면 좁은 폭에서 넘치므로 전체 너비에만 반영한다. indicator의 x와 너비는 애니메이션되는 칸 번호로 이웃 칸 경계 사이를 보간하고 배치 단계에서만 읽는다. 칸 너비가 1px씩 다를 수 있어 칸 너비 × 번호로는 경계가 어긋난다. RTL은 `placeRelative`가 뒤집는다. 이미 선택된 항목을 다시 눌러도 콜백은 부르지 않는다.
- 세그먼트 컨테이너는 clip하지 않는다(SEED 루트도 넘침을 자르지 않는다). 글자 2배에서 컨트롤이 커지면 indicator가 둥근 컨테이너 모서리 밖으로 나올 수 있다. clip하면 가장자리 indicator와 항목이 잘려서 이쪽을 택했다.
- 선택된 세그먼트 항목의 눌림·비활성은 SEED처럼 항목 자신이 `gray100`(눌림)·`bg.disabled`(비활성) 배경과 1dp 테두리로 indicator를 덮어 칠한다. indicator 색은 늘 `gray00`이다. 선택 해제 상태의 항목 색은 눌림 색과 같은 색조의 alpha 0이다. `Color.Transparent`(검정 alpha 0)에서 번지면 전환 중간에 어두운 회색이 지나간다.
- 세그먼트처럼 variant·size가 없는 컴포넌트는 토큰을 컴포넌트 파일에서 바로 읽는다. 메트릭 클래스를 따로 만들지 않는다.
- ghost "선택 + 눌림" 마크 배경은 SEED의 `carrot-200` 대신 brand는 `forest200`(라이트·다크 같은 단계), neutral은 `gray200`이다. 아이콘은 그대로 `fg.brand`다. 아이콘과 배경의 대비는 라이트 3.96(`fg.brand` `forest600` 대 `forest200`), 다크 4.36(`fg.brand` `forest700` 대 `forest200`)이다. 다크의 다른 단계는 `forest100` 5.05, `forest300` 3.43, `forest400` 2.56이라 `forest400`은 3:1에 못 미친다. 같은 단계를 쓰는 편이 SEED와 맞아 `forest200`으로 정했다. `CheckmarkContrastTest`가 라이트·다크 모두 3:1 이상을 강제한다.
- select-box 체크 마크는 `PushlogCheckmark`가 아니라 SEED `select-box-checkmark`를 옮긴 `internal SelectBoxCheckmark`다. 크기·색 규칙이 달라서 공개 API를 늘리지 않으려고 체크 그리기 함수만 같이 쓴다. `SelectionControlRow`도 쓰지 않는다. 카드 전체가 탭이고 마크 위치 보정 규칙이 줄과 달라서다.
- `type`(`SINGLE`/`MULTIPLE`)과 `showControl`로 SEED 컨트롤 세 가지(Checkmark, Radiomark, None)를 나타낸다. 마크를 숨겨도 접근성 역할은 `type`을 따른다.
- select-box는 눌림 색과 함께 내용을 줄인다(`PressScaleScope.CONTENT`, 배율은 카드 크기 기준). 마크는 `scaleOnPress = false`로 색만 바꾼다. SEED도 `--seed-radiomark-feedback-scale: 1`로 이중 축소를 막는다.
- 선택 테두리 2dp는 표면 바깥 modifier가 `border`와 같은 모양·위치로 그려 기본 1dp를 덮는다(SEED `::after`). 페이드 100ms는 `duration.d2` 토큰이다. 색은 그리기 단계에서만 읽는다. `Modifier.border`는 색을 컴포지션에서 넘겨야 해서 페이드 동안 카드가 프레임마다 다시 구성된다.
- `suffix`는 마크 바로 앞이다. SEED React는 `suffix` 자리에 마크를 넣지만 Pushlog는 마크를 `type`으로 그리므로 둘을 나란히 둔다.
- 단일 선택에서 이미 선택된 박스를 다시 눌러도 `onClick`이 불린다. 세그먼트는 부르지 않는다. 세그먼트는 `onSelectedIndexChange`라 같은 값 재전달이 의미 없지만, select-box는 `onClick` 하나라 호출하는 쪽이 판단한다.
- footer는 `PushlogSelectBoxFooterVisibility` 세 가지로 보인다. 열 때 높이 400ms(상수 `FOOTER_SLOW_DURATION_MILLIS`, 같은 값의 토큰이 없다)·투명도 `duration.d6`(300ms), 닫을 때 반대다. 다 닫히면 컴포지션에서 빼서 스크린 리더가 숨은 내용을 읽지 않는다. `suffix`와 `footer`에는 intrinsic을 지원하지 않는 레이아웃(`LazyColumn`, `BoxWithConstraints`, `SubcomposeLayout`)을 넣지 않는다. 여러 열 그룹이 intrinsic 높이로 줄을 맞춘다.
- 그룹은 상태가 없고 1~3열이다. 열이 2 이상이면 박스 기본 layout이 세로이고(`LocalSelectBoxLayout`) 박스 인자가 덮어쓴다. 여러 열의 줄 높이는 `maxIntrinsicHeight`로 먼저 정하고 그 고정 높이로 잰다. Compose는 자식을 한 번만 잴 수 있어서다. 한 열은 줄 높이를 맞추지 않는다(SEED `data-columns != 1`). 줄 높이는 높이 제한 없이 정해지므로 박스 내용에 세로 `weight`를 쓰지 않는다.
- 그룹 칸 너비는 세그먼트처럼 나머지 픽셀을 칸마다 나눠 마지막 칸이 오른쪽 끝에 닿는다. 칸끼리 1px 다를 수 있다.
- 그룹은 intrinsic 측정을 직접 계산한다(너비는 열 수 × 가장 넓은 자식 + 간격, 높이는 측정과 같은 줄 계산). 기본 구현은 너비 제한 없이 측정을 불러 `IntrinsicSize` 부모나 `DropdownMenu` 안에서 실패한다. 측정 자체는 여전히 너비 제한이 있어야 한다.
- 열이 2 이상일 때만 박스가 줄 높이를 채운다(`internal LocalSelectBoxFillsRow`를 그룹이 켠다). 단독 박스가 채우면 높이가 정해진 부모를 따라 늘어나서 끈다. `CompositionLocal`은 셀 안 모든 것에 닿으므로 박스는 그룹의 직계 자식이어야 한다. 형제가 있는 래퍼로 감싸면 박스가 눌린다.
