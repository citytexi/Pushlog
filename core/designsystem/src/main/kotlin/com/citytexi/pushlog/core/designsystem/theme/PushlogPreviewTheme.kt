package com.citytexi.pushlog.core.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

/**
 * `@PushlogPreview` 본문을 감싸는 입구. [PushlogTheme] 아래에 화면 배경(`bg.layerDefault`)과 기본 글자색(`fg.neutral`)을 깐다.
 * `@Preview`의 `showBackground`는 테마와 무관하게 흰색이라 이것 없이는 다크 변형이 흰 바탕에 그려진다(`PreviewThemeRulesTest`).
 * 미리보기 전용이다. 화면·컴포넌트 코드는 `PushlogTheme`을 쓴다(`PreviewThemeRulesTest`가 막는다).
 */
@Composable
fun PushlogPreviewTheme(content: @Composable () -> Unit) {
    PushlogTheme {
        CompositionLocalProvider(LocalContentColor provides PushlogTheme.color.fg.neutral) {
            Box(Modifier.background(PushlogTheme.color.bg.layerDefault)) {
                content()
            }
        }
    }
}
