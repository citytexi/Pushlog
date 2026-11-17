# 디자인 시스템 동작 컴포넌트 설계

- 작성일: 2026-11-13
- 상태: 승인됨 (2026-11-13)
- 이슈: #19 (상위 #17)
- 범위: `core:designsystem`의 동작 컴포넌트 세 개(`PushlogActionButton`, `PushlogChip`, `PushlogContextualFloatingButton`)와 이들이 함께 쓰는 내부 부품. 기존 `PushlogButton` 교체. 입력(#20), 피드백(#21), 목록·탐색(#22), 패턴(#23)은 범위 밖이다.

## 1. 의도

Pushlog 화면(목록, 검색, 대화방, 태그, 설정)이 쓸 동작 컴포넌트를 SEED Design 정의대로 만든다. foundation(#18) 토큰이 유일한 값 출처다.

- 지금 있는 `PushlogButton`은 M3 `Button`을 감싼 스타터 코드다. 이 작업에서 Pushlog 컴포넌트로 바꾼다.
- 기준은 SEED rootage 정의(`packages/rootage/components/*.yaml`, 기준 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`)와 SEED 문서(https://seed-design.io/components, /foundations)다. 정의는 값을, 문서는 사용 기준과 deprecated 여부를 정한다.

## 2. 목표 · 비목표 · 반목표

목표

- 세 컴포넌트를 SEED 정의의 색·크기·모서리·글자·그림자·눌림 동작대로 만든다(라이트/다크).
- 세 컴포넌트가 같은 눌림 피드백을 쓰도록 내부 공용 부품 하나로 묶는다. #20~#22도 이 부품을 쓴다.
- 컴포넌트마다 `@PushlogPreview`, 미리보기 앱 화면, 라이트/다크 스크린샷 테스트를 둔다.
- `PushlogButton`을 지우고 사용처를 `PushlogActionButton`으로 옮긴다.

비목표

- 아이콘 세트. 컴포넌트는 아이콘 자리만 받는다. 아이콘을 어디서 가져올지는 정하지 않았고 이슈도 만들지 않는다(사용자 결정, 2026-11-13).
- 공개 progress circle 컴포넌트(#21). 이번에는 로딩 표시용 내부 부품만 만든다.
- 햅틱 피드백. SEED Feedback 문서가 다루지만 이번 범위에 넣지 않는다.
- 플로팅 버튼의 나타남·사라짐 애니메이션과 배치 규칙(화면 끝에서 띄우는 간격, Snackbar 위치). 패턴(#23) 몫이다.
- 칩 묶음(Chip Group, Filter Bar). 패턴(#23) 몫이다.
- M3 다리(`toColorScheme()`) 축소. 아직 M3 컴포넌트를 쓰는 화면이 있다.

반목표 (돌아가도 실패로 보는 것)

- 눌렀을 때 글자 위까지 반투명 막이 덮이는 것(M3 ripple·state layer). SEED는 표면 색만 바뀐다.
- 컴포넌트 코드에 foundation 토큰으로 표현할 수 있는 값을 직접 적는 것.
- 아이콘만 있는 버튼이 접근성 이름 없이 만들어질 수 있는 API.

## 3. 제약

- `AGENTS.md` 금지 사항, `docs/code-conventions.md` 주석 규칙.
- 디자인 시스템 문서의 [컴포넌트를 추가할 때](../../architecture/design-system/preview.md#컴포넌트를-추가할-때) 네 단계와 [누락 방지 테스트](../../architecture/design-system/preview.md#누락-방지-테스트). `component` 패키지(하위 패키지 제외)의 공개 최상위 `@Composable`마다 미리보기 앱 화면이 있어야 한다. 그래서 공용 부품은 `internal`로 둔다.
- 팔레트(`LightPalette`, `DarkPalette`)는 `internal`이다. 컴포넌트는 `core:designsystem` 안이라 접근은 되지만 라이트/다크를 따라가야 한다.
- 파일은 타입마다 하나다(`FileStructureRulesTest`).

## 4. 접근 방식

| 방식 | 내용 | 대가 | 결과 |
|---|---|---|---|
| A. Compose foundation 위에 직접 | `Box`/`Row` + `clickable(indication = null)` + 내부 공용 눌림 부품. 색·크기는 토큰에서 | 접근성 role, 최소 터치 영역, disabled 처리를 직접 챙긴다 | **선택** |
| B. M3 `Button`·`FilterChip` 감싸기 | 색만 토큰으로 덮어쓴다 | ripple·state layer가 SEED 눌림 규칙과 충돌. 높이·패딩·최소 크기를 계속 덮어써야 함. M3 다리를 줄이는 방향과 반대 | 탈락 |

사용자는 A를 골랐다. SEED 눌림 규칙을 그대로 지킬 수 있고 #20~#22가 같은 부품을 다시 쓸 수 있기 때문이다.

## 5. 컴포넌트 목록 결정

| SEED | 결정 | 이유 |
|---|---|---|
| `action-button` | `PushlogActionButton` | 모든 화면의 기본 버튼 |
| `chip` | `PushlogChip` | 검색 필터(앱·태그·방·기간), 태그 고르기 |
| `contextual-floating-button` | `PushlogContextualFloatingButton` | 긴 기록 목록의 "맨 위로", "새 알림 N개" (사용자 결정) |
| `fab`, `extended-fab` | 제외 | SEED에서 deprecated, `contextual-floating-button`으로 대체 |
| `action-chip`, `control-chip` | 제외 | SEED에서 deprecated, `chip`으로 대체 |
| `text-button` | 제외 | SEED 문서 없음. `action-button` ghost가 대신 |
| `toggle-button` | 제외 | 디자인 가이드 없음. 칩과 역할이 겹친다 |
| `reaction-button` | 제외 | 당근 전용(좋아요·관심) 성격, Pushlog에 쓸 곳 없음 |

## 6. 설계

### 6.1 공용 눌림 부품 (internal)

SEED Feedback(눌림) 규칙을 한 곳에서 구현한다.

- 색: 눌리면 표면 색만 `-pressed` 토큰으로 바뀐다. 글자·아이콘 색은 그대로다. 전환 시간은 `motion.duration.colorTransition`, easing은 `motion.timingFunction.easing`이다.
- 크기: `scaleScope: self` 요소는 눌리면 2px 줄어든다. 배율은 `(기준 길이 - 2px) / 기준 길이`이고, 기준 길이는 높이, 너비÷4, 24px 중 가장 큰 값이다. 시간은 `motion.duration.pressedScale`, easing은 `motion.timingFunction.pressedScale`이다.
- 줄이지 않는 경우: loading, disabled, 시스템 "애니메이션 삭제"가 켜졌을 때.
- ripple은 쓰지 않는다(`indication = null`). `interactionSource`로 눌림을 읽는다.

### 6.2 `PushlogActionButton`

- variant: `brandSolid`, `neutralSolid`, `neutralWeak`, `criticalSolid`, `ghost`. SEED의 `brandOutline`, `neutralOutline`은 쓸 곳이 없어 뺀다.
- size: `small`(높이 x9), `medium`(x10), `large`(x13). SEED `xsmall`은 뺀다.
- 레이아웃: 글자만, 글자 앞 아이콘, 아이콘만. 뒤 아이콘은 두지 않는다. SEED는 앞뒤 동시 사용만 막고 뒤 아이콘 단독(Chevron 등)은 허용하지만, Pushlog 화면에 쓸 곳이 없다.
- 상태: enabled, pressed, disabled, loading.
- loading: 라벨과 아이콘은 자리를 지킨 채 투명하게 그리고, 그 위 가운데에 progress circle을 그린다. 그래서 너비가 변하지 않는다. 표면은 `-pressed` 색이다(ghost는 `transparentPressed`). 누를 수 없다. 접근성 이름(라벨)은 그대로 두고 상태 설명으로 `designsystem_loading`("불러오는 중")을 붙인다. disabled로 읽히지는 않는다.
- 색·패딩·간격·모서리·글자 크기는 SEED `action-button.yaml`의 해당 variant·size 값을 따른다. 글자 굵기는 bold. ghost 배경은 yaml이 hex(`#ffffff00`)로 적었지만 `bg.transparent`를 쓴다.
- 아이콘만 있는 레이아웃은 같은 이름의 오버로드다. `icon: Painter`와 null이 될 수 없는 `contentDescription: String`을 받는다(반목표 3). 함수 이름이 같아서 미리보기 앱 화면은 컴포넌트당 하나다.
- 너비는 `modifier`로 정한다(`fillMaxWidth()` 또는 내용 크기).

### 6.3 `PushlogChip`

- variant: `solid`, `outlineWeak`. SEED 문서가 다중 선택에 `outlineWeak`를 권장하고, Pushlog 필터는 다중 선택이다. `outlineStrong`은 쓸 곳이 없어 뺀다.
- size: `small`(32px), `medium`(36px), `large`(40px).
- 상태: enabled, pressed, selected, selected+pressed, disabled(불투명도 0.5).
- 레이아웃: 글자, 글자 앞 아이콘, 글자 뒤 아이콘(지우기·펼치기 표시). 뒤 아이콘은 그림일 뿐이고 칩 전체가 하나의 탭 영역이다(SEED `chip.yaml`과 같다). 아이콘만 따로 누르는 지우기 버튼은 SEED 문서가 "사용자가 입력한 값" 칩에만 두라고 하며, Pushlog 필터 칩은 고르는 칩이라 두지 않는다. SEED의 앞 아바타는 Pushlog에 쓸 곳이 없어 뺀다. 아이콘만 있는 칩도 뺀다.
- 선택형으로 쓰면 접근성 role은 체크 상태를 가진 것으로, 선택 상태가 없으면 버튼으로 읽힌다.
- 색·높이·패딩·최소 너비·아이콘 크기는 SEED `chip.yaml`을 따른다. 아이콘 크기는 SEED 문서(16px)와 yaml(small 앞뒤·medium 뒤 14px)이 다른데, yaml이 우선한다.

### 6.4 `PushlogContextualFloatingButton`

- variant: `solid`(`bg.neutralSolid`), `layer`(`bg.layerFloating`).
- 레이아웃: 글자(앞 아이콘 선택)는 최소 높이 36px, 아이콘만은 44px 원이다. SEED 문서가 아이콘만은 layer에서만 쓰도록 해서, 아이콘만 레이아웃은 variant를 받지 않고 항상 layer다. 아이콘만 레이아웃은 같은 이름의 오버로드이고 `contentDescription: String`(null 불가)을 받는다.
- 그림자 `shadow.s3`(`Modifier.pushlogShadow`), 모서리 `radius.full`.
- 상태: enabled, pressed, disabled, loading.
- 글자는 한 줄이다. SEED 문서가 줄바꿈을 막는다. 넘치면 말줄임한다.
- loading은 버튼(§6.2)과 같은 방식이다.
- 나머지 값은 `contextual-floating-button.yaml`을 따른다. 글자는 t4 **medium**(버튼의 bold와 다르다), paddingX `x3Half`, paddingY `x2`, gap `x1`, 앞 아이콘 16px(`x4`), 아이콘만 아이콘 22px.

### 6.5 로딩 표시 (internal)

- SEED `progress-circle.yaml`의 indeterminate 원을 내부 부품으로 만든다. 크기와 두께는 각 컴포넌트 정의를 따른다(버튼 14/16/18px, 플로팅 버튼 16px, 두께 2px).
- 트랙 색은 SEED가 팔레트(`staticWhiteAlpha300`, `gray500`, `gray700`)를 직접 가리킨다. 라이트/다크를 따라가는 내부 경로로 읽는다. 공개 토큰은 늘리지 않는다.
- #21에서 공개 컴포넌트로 올릴 때 이 부품을 옮긴다.

### 6.6 터치 영역

- 그려지는 크기는 SEED 그대로 두고, 누르는 영역만 넓힌다(레이아웃 크기는 바뀌지 않는다).
- 아이콘만 버튼(액션 버튼 아이콘만, 플로팅 버튼 아이콘만)은 40dp 이상. SEED Iconography 기준이다.
- 나머지(글자 버튼, 칩, 글자 플로팅 버튼)는 24dp 이상. SEED Inclusive Design의 최소 기준이다. 지금 크기가 모두 넘으므로 실제로 넓힐 일은 없지만 규칙으로 둔다.

### 6.7 값 출처

- foundation 토큰으로 표현되는 값(색, 간격 `x*`, 모서리, 글자, 그림자, 모션)은 반드시 토큰을 읽는다.
- SEED yaml이 `$dimension` 대신 px로 적은 값도 같은 크기의 토큰이 있으면 토큰을 쓴다. 14px → `x3Half`, 16px → `x4`, 18px → `x4Half`, 32px → `x8`, 36px → `x9`, 40px → `x10`.
- 토큰이 없는 값만 컴포넌트 파일 안의 비공개 상수로 두고 SEED 출처를 주석으로 단다. 22px(아이콘), 44px(아이콘만 플로팅 버튼, 칩 small 최소 너비), 1px(테두리), 2px(progress 두께, 눌림 축소량), progress circle 애니메이션 값(`progress-circle.yaml`의 회전·길이 주기 1.2초와 cubic-bezier 세 개). 새 foundation 토큰은 만들지 않는다.

### 6.8 `PushlogButton` 교체

- `PushlogButton`, `PushlogButtonScreenshotTest`, 미리보기 앱의 `PushlogButtonPreviewKey`·`PushlogButtonPreviewScreen`과 카탈로그·entry 등록을 지운다.
- `feature/sample`의 새로고침 버튼은 `PushlogActionButton`으로 바꾸고, 새로고침 중에는 `enabled = false` 대신 `loading = true`를 쓴다. `sample_screen_refreshing*` 스크린샷이 달라진다.
- `PreviewScaffold` 미리보기의 제목 문자열 `"PushlogButton"`은 다른 이름으로 바꾼다.
- `docs/architecture/design-system.md`의 "컴포넌트를 추가할 때" 예시를 `PushlogActionButton`으로 바꾼다.

### 6.9 미리보기와 테스트

- 각 컴포넌트 파일에 `@PushlogPreview`(라이트·다크·글자 200%)를 둔다. 개발 중 prevue로 렌더링해 SEED 문서 그림과 맞춘다.
- 미리보기 앱: 컴포넌트마다 Key·Screen·entry·catalog 네 단계. 화면은 variant × size × 상태 표를 보여 준다. 눌림은 실기기에서 직접 눌러 본다.
- 스크린샷 테스트(Roborazzi, `core:designsystem`): 컴포넌트마다 라이트/다크로 세 장씩 찍는다.
  - 상태 표: variant × 상태(enabled, pressed, disabled, loading 또는 selected·selected+pressed). pressed는 `MutableInteractionSource`에 `PressInteraction.Press`를 넣어 강제한다. 축소 애니메이션이 끝난 뒤 찍는다.
  - 크기 표: size × 레이아웃(글자, 앞 아이콘, 뒤 아이콘, 아이콘만 중 해당하는 것).
  - 글자 200%: `Density`의 fontScale을 2로 덮어 상태 표를 다시 찍는다. 칩 높이가 늘어나는지(§8), 플로팅 버튼이 말줄임하는지 본다.
- 단위 테스트: 눌림 배율 계산(기준 길이 세 경우), 아이콘만 버튼에 접근성 이름이 붙는지, 칩 선택 상태가 접근성 트리에 드러나는지, loading 버튼이 라벨과 "불러오는 중" 상태를 갖고 클릭되지 않는지, 아이콘만 버튼의 터치 영역이 40dp 이상인지.

## 7. 사용자가 정한 것

- 최종 목록은 `action-button`, `chip`, `contextual-floating-button` 세 개다.
- 아이콘 세트는 계획이 없다. 이슈를 만들지 않는다.
- 만드는 방식은 A(Compose foundation 위에 직접, 공용 눌림 부품)다.
- 칩 뒤 아이콘은 표시만 하고 따로 누르지 않는다. 칩 전체가 탭 하나다.

## 8. 설계자 판단 (바꿔도 됨)

- 버튼 variant 다섯 개, 크기 세 개(`xsmall`, outline 두 종 제외).
- 칩 variant 두 개(`outlineStrong` 제외), 앞 아바타·아이콘만 칩 제외.
- 아이콘만 플로팅 버튼은 layer 고정.
- SEED가 px로 적은 값은 같은 크기 토큰이 있으면 토큰, 없으면 컴포넌트 비공개 상수(§6.7).
- 아이콘만 버튼은 같은 이름 오버로드(미리보기 앱 화면 수를 늘리지 않으려고).
- loading은 라벨을 투명하게 남겨 너비를 고정한다. 클릭 불가지만 disabled로 읽히지 않는다.
- 터치 영역은 아이콘만 40dp, 나머지 24dp 이상(§6.6).
- sample 화면 새로고침은 `loading`으로 바꾼다.
- 아이콘 인자 타입은 `Painter`. 벡터·드로어블 리소스를 모두 받는다.
- 칩 높이: SEED는 고정 `height`지만 최소 높이로 둔다. 글자 200%에서 잘리지 않게 늘어나는 쪽을 택했다. SEED 접근성 문서가 글자 크기 설정을 따르라고 한다. 라벨은 한 줄, 넘치면 말줄임.

## 9. 빌더에게 맡기는 것

- 공개 API의 인자 이름과 순서(Compose API 가이드 순서: 필수 → `modifier` → 선택).
- variant·size를 표현하는 타입(enum 등).
- "애니메이션 삭제" 설정을 읽는 방법.
- 팔레트 트랙 색을 라이트/다크에 맞게 읽는 내부 경로(§6.5).
- 미리보기와 미리보기 앱에서 쓸 샘플 아이콘. `core:designsystem`에 아이콘 라이브러리 의존을 더하지 않는다.
- 글자 크기 200%에서 버튼 라벨을 줄바꿈할지 말줄임할지. 플로팅 버튼은 말줄임으로 정해져 있다.
- 터치 영역을 넓히는 구현 방법(§6.6 기준은 정해져 있다).
