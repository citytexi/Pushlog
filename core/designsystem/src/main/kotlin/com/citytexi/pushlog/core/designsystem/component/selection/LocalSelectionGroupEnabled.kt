package com.citytexi.pushlog.core.designsystem.component.selection

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

/** 선택 컨트롤 그룹이 자식의 `enabled` 기본값으로 내려주는 값. 그룹 밖이면 true다. */
internal val LocalSelectionGroupEnabled: ProvidableCompositionLocal<Boolean> = compositionLocalOf { true }
