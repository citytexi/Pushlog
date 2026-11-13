---
id: state-management
title: 화면 상태: MVI 컨테이너
category: architecture
status: living
platforms: android
verified: 2026-11-13
related_spec:
related_adr:
related_architecture:
related_code: [MutableMviContainer, MviContainer, SampleViewModel]
tags: [architecture, mvi]
---

# 화면 상태: MVI 컨테이너

화면 하나는 `UiState`, `UiIntent`, `UiSideEffect`(`core.ui.mvi`)로 표현한다. BaseViewModel은 없다. ViewModel이 컨테이너를 가지고(조합), 화면에는 읽기 전용 뷰만 연다(`StateFlow`/`MutableStateFlow`와 같은 방식).

```kotlin
// <Name>UiState.kt
data class <Name>UiState(...) : UiState
sealed interface <Name>Intent : UiIntent { data object Refresh : <Name>Intent }
sealed interface <Name>SideEffect : UiSideEffect { data class ShowError(val error: <Name>Error) : <Name>SideEffect }

// <Name>ViewModel.kt
private val _container = MutableMviContainer<<Name>UiState, <Name>SideEffect>(viewModelScope, <Name>UiState())
val container: MviContainer<<Name>UiState, <Name>SideEffect> = _container
fun onIntent(intent: <Name>Intent) { when (intent) { ... } }

// <Name>Route.kt
val state by viewModel.container.collectAsStateWithLifecycle()
viewModel.container.collectSideEffect { effect -> ... }
<Name>Screen(state = state, onIntent = viewModel::onIntent)
```

- 사용자 동작은 `onIntent(<Name>Intent)` 하나로 받는다. 화면(`*Screen`)은 `state`와 `onIntent`만 받는 stateless composable이다.
- 상태 변경은 `_container.updateState { copy(...) }`로만 한다.
- 한 번만 처리할 일(스낵바, 화면 이동)은 `UiSideEffect`로 보낸다(`_container.postSideEffect(...)`). 화면에 계속 보여야 하는 것은 state에 둔다. `sideEffect`는 Route 한 곳에서만 `collectSideEffect`로 수집한다(채널이라 두 곳에서 받으면 나뉜다).
- 작업은 `_container.launch(key = ...) { }`로 실행한다. 같은 key가 실행 중이면 무시한다(연타 방지). 예상 못 한 예외를 effect로 바꾸려면 `onError`를 넘긴다.
- 데이터 관찰(UseCase의 `Flow`)은 `_container.launchWhileSubscribed(source = { ... }) { updateState }`로 한다. 화면이 보이는 동안(Route가 state를 수집하는 동안)만 수집한다.
- 테스트는 `viewModel.container.state.value`와 `viewModel.container.sideEffect.test { }`(Turbine)로 확인한다(`SampleViewModelTest`). 데이터 관찰은 state 수집자가 있어야 시작하므로 테스트에서도 state를 수집한다.
