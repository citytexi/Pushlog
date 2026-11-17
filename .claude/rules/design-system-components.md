---
paths:
  - "core/designsystem/**"
  - "app-preview/**"
---

# 디자인 시스템 컴포넌트 규칙

`core:designsystem` 컴포넌트를 추가·수정하기 전에 [`docs/architecture/design-system.md`](../../docs/architecture/design-system.md)의 "동작 컴포넌트"와 "미리보기 앱" 절을 읽는다. 규칙의 정본은 그 문서다. 여기에는 놓치기 쉬운 것만 적는다.

- 컴포넌트는 `component.<종류>` 하위 패키지(`button`, `chip`, `loading`, `pressable` …)에 둔다. `component` 바로 아래는 Konsist가 막는다.
- 눌리는 컴포넌트는 `PushlogPressableSurface`에 내용을 채운다. 줄 전체가 탭이고 마크만 줄어드는 컨트롤은 `pressable`의 탭 영역 modifier + 눌림 표시 modifier를 같은 `interactionSource`로 쓴다. `pressable` 밖에서 `clickable`·`toggleable`·`pointerInput`·ripple, 눌리는 Material 3 컴포넌트를 쓰지 않는다(Konsist가 막는다).
- 크기별 값은 `PushlogXxxSize.metrics()`가 `XxxMetrics`(예: `ChipMetrics`)로, 색은 `PushlogXxxVariant`의 `internal` 멤버가 돌려준다. 멤버는 `else` 없이 `when (this)`로 쓴다. 컴포넌트 파일에는 그리는 코드와 거기서만 쓰는 `private` 상수만 둔다.
- `XxxDefaults`는 호출하는 쪽에 값을 열 때만 만든다. 내부 값은 `private` 상수다.
- SEED px 값은 같은 크기의 토큰으로 옮기고, 토큰이 없을 때만 `private` 상수에 SEED 출처 주석을 단다.
- 크기·레이아웃 스크린샷은 `app-preview`의 섹션별 `@PushlogPreview`가 찍는다. 새 크기나 레이아웃을 추가하면 그 `@PushlogPreview`를 하나 더 만든다.
- 공개 `@Composable`을 추가하면 `app-preview`에 `<함수>PreviewKey`·`<함수>PreviewScreen.kt`를 만든다(Konsist가 막는다).
- 레이아웃을 옮기는 리팩터링은 바꾸기 전 커밋에서 `recordRoborazziDebug`, 바꾼 뒤 `verifyRoborazziDebug`로 픽셀 차이가 없는지 확인한다. 새 파일이 있으면 `git stash -u`로 되돌린다.
- 주석은 [`docs/code-conventions.md`](../../docs/code-conventions.md)를 따른다. 코드가 이미 말하는 것은 쓰지 않는다.
