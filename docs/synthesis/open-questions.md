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

- `secondaryContainer`(`bg.neutralWeak`, gray200 `#F3F4F5`)와 NavigationBar 컨테이너 `surfaceContainer`(`bg.neutralMuted`, gray100 `#F7F8F9`)가 거의 구분되지 않는다. 설계: [`design-system.md`](../architecture/design-system.md)
- 남은 것: 내비게이션 컴포넌트(#22)를 만들 때 정한다.
