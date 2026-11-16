package com.citytexi.pushlog.core.designsystem.component.pressable

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 시스템 "애니메이션 삭제"(애니메이터 배율 0) 설정을 앱 전체에서 하나의 observer로 지켜본다.
 * 누르는 요소마다 observer를 등록하면 목록을 스크롤할 때 등록·해제 binder 호출이 메인 스레드에서 반복되므로, 요소는 [flow]만 읽는다.
 * observer는 처음 쓸 때 한 번만 등록하고 해제하지 않는다. 앱 프로세스와 수명이 같다.
 */
internal object ReduceMotion {
    private val state = MutableStateFlow(false)
    private var resolver: ContentResolver? = null
    private var observer: ContentObserver? = null

    fun flow(context: Context): StateFlow<Boolean> {
        val appResolver = context.applicationContext.contentResolver
        if (appResolver !== resolver) register(appResolver)
        return state
    }

    // 운영에서는 resolver가 한 번만 정해진다. 테스트처럼 앱 컨텍스트가 바뀌면 새 resolver에 다시 건다.
    private fun register(appResolver: ContentResolver) {
        val newObserver =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    state.value = appResolver.isReduceMotion()
                }
            }
        observer?.let { resolver?.unregisterContentObserver(it) }
        appResolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, newObserver)
        resolver = appResolver
        observer = newObserver
        state.value = appResolver.isReduceMotion()
    }

    private fun ContentResolver.isReduceMotion(): Boolean =
        Settings.Global.getFloat(this, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
}
