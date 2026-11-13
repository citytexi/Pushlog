package com.citytexi.pushlog.core.designsystem.theme.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.citytexi.pushlog.core.designsystem.R

/** 앱 전체 글꼴 Pretendard(400/500/700). 라이선스는 `core/designsystem/licenses/`. */
internal val PretendardFontFamily =
    FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal),
        Font(R.font.pretendard_medium, FontWeight.Medium),
        Font(R.font.pretendard_bold, FontWeight.Bold),
    )
