# 미결 항목 (구현)

구현 쪽 미결의 단일 추적 장소다. 기획 미결은 [`wiki/open-questions.md`](../../wiki/open-questions.md)에 있다.

## 미해결

### [2026-11-13] 추출 규칙을 Remote Config로 옮길 시점

- 현재: 추출 규칙은 앱 내장(데이터 형식 + 버전). 설계: [`2026-11-13-pushlog-design.md`](../superpowers/specs/2026-11-13-pushlog-design.md) §6.7
- 남은 것: 문구 변경 빈도를 보고 Remote Config 이전 여부를 정한다.

### [2026-11-13] 카카오톡 대화방 이름(`shortLabel`) 실제 값 확인

- `dumpsys`에서는 라벨이 `***`로 가려져 확인하지 못했다. 수집기 단계에서 리스너로 실제 값을 찍어 본다.

### [2026-11-13] 메신저 원본 저장 용량 실측

- 갱신마다 누적 메시지가 다시 실려 원본이 커진다. 수집기 단계에서 증가량을 재고 압축 저장 결정을 재검토한다.

### [2026-11-13] 앱 안 오픈소스 라이선스 고지

- Pretendard(OFL)와 SEED(Apache-2.0 §4(d) NOTICE)의 고지가 APK에 들어가야 한다. `core/designsystem/licenses/`는 APK에 들어가지 않는다.
- 남은 것: 앱 안 라이선스 화면 또는 assets 번들 방식을 정한다.

### [2026-11-13] M3 NavigationBar 선택 표시가 라이트에서 거의 안 보임

- `secondaryContainer`(`bg.neutralWeak`, gray200 `#F3F4F5`)와 NavigationBar 컨테이너 `surfaceContainer`(`bg.neutralMuted`, gray100 `#F7F8F9`)가 거의 구분되지 않는다. 설계: [`design-system/foundation.md`](../architecture/design-system/foundation.md#m3-연결)
- 남은 것: 내비게이션 컴포넌트(#22)를 만들 때 정한다.

### [2026-11-17] 글자 입력 상태를 화면 어디에 둘지

- `PushlogTextInput`은 `TextFieldState`, segmented 입력은 상태 클래스를 받는다. 둘 다 변경 가능한 객체라 불변 `UiState`에 넣기 어렵다. 설계: [`2026-11-17-design-system-text-inputs-design.md`](../superpowers/specs/2026-11-17-design-system-text-inputs-design.md) §4.1
- 남은 것: 첫 입력 화면에서 Route가 상태를 들고 `snapshotFlow`로 `UiIntent`를 보낼지, ViewModel이 들지 정하고 `state-management.md`에 적는다.

### [2026-11-18] select-box footer가 박스·그룹 enabled를 따라야 하는가

- 현재: footer 안 text-input은 field 상태를 끊어 활성으로 남고, footer에 바로 둔 체크박스·라디오는 그룹의 `LocalSelectionGroupEnabled`를 물려받는다. 설계: [`design-system/components/input.md`](../architecture/design-system/components/input.md)
- 남은 것: footer 전체가 select-box의 `enabled`를 따를지 정한다.

### [2026-11-18] 읽기 전용 input-button의 TalkBack 안내

- 현재: `PushlogInputButton`은 읽기 전용이면 `PushlogPressableSurface`에 `enabled = false`를 넘겨 TalkBack이 "사용 중지됨"으로 읽을 가능성이 크다. 스펙은 읽기 전용이 눌리지 않는다고만 적었다. 설계: [`design-system/components/input.md`](../architecture/design-system/components/input.md)
- 남은 것: 다른 안내가 필요하면 `PushlogPressableSurface` API를 바꿔야 한다. 실기기 TalkBack에서 확인한 뒤 정한다.

### [2026-11-18] search-input 지우기 뒤 접근성 포커스 이동

- 현재: 스펙 §6.4는 지우기 버튼이 사라지며 접근성 포커스를 잃으면 입력으로 옮긴다(SEED 문서)고 적었지만 구현하지 않았다. Compose에는 접근성 포커스만 옮기는 API가 없고, `requestFocus`는 입력 포커스까지 줘서 "포커스가 없었으면 새로 주지 않는다"를 깬다. 설계: [`design-system/components/input.md`](../architecture/design-system/components/input.md)
- 남은 것: 실기기 TalkBack에서 지운 뒤 포커스가 어디로 가는지 확인하고, 필요하면 방법을 정한다.

### [2026-11-19] segmented 넘친 입력: 상태 값 전달이 다시 돌지 않은 원인

- 현재: 칸 자리 수를 넘친 입력은 `Channel`로 다음 칸에 넘긴다. 상태 값과 `LaunchedEffect` 키로 넘기는 방식은 테스트에서 효과가 다시 돌지 않았고 원인은 찾지 못했다. 설계: [`design-system/components/input.md`](../architecture/design-system/components/input.md)
- 남은 것: 원인을 찾아 상태 방식이 더 나은지 다시 본다. 찾지 못해도 `Channel` 방식은 그대로 쓴다.
