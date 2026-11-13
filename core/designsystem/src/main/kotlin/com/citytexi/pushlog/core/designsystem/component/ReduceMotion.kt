package com.citytexi.pushlog.core.designsystem.component

import android.content.ContentResolver
import android.provider.Settings

/** 시스템 "애니메이션 삭제"(애니메이터 배율 0)가 켜져 있으면 true. */
internal fun ContentResolver.isReduceMotion(): Boolean = Settings.Global.getFloat(this, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
