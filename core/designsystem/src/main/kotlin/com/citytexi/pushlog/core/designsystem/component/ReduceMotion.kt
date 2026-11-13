package com.citytexi.pushlog.core.designsystem.component

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings

/** 시스템 "애니메이션 삭제"(애니메이터 배율 0)가 켜져 있으면 true. */
internal fun ContentResolver.isReduceMotion(): Boolean = Settings.Global.getFloat(this, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f

/**
 * 애니메이터 배율이 바뀔 때마다 [listener]에 새 값을 준다. 화면을 연 채 설정을 바꿔도 반영하려고 쓴다.
 * 돌려받은 observer는 [ContentResolver.unregisterContentObserver]로 해제한다.
 */
internal fun ContentResolver.observeReduceMotion(listener: (Boolean) -> Unit): ContentObserver {
    val resolver = this
    val observer =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) = listener(resolver.isReduceMotion())
        }
    registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
    return observer
}
