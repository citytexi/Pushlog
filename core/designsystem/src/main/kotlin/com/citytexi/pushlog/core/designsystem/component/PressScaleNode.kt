package com.citytexi.pushlog.core.designsystem.component

import android.content.ContentResolver
import android.database.ContentObserver
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Constraints
import com.citytexi.pushlog.core.designsystem.theme.LocalPushlogMotion
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 눌리면 뒤따르는 내용을 [pressedScaleFactor]만큼 줄이는 층. 레이아웃 크기는 그대로다.
 * 비활성·로딩·애니메이션 삭제 중에는 줄이지 않는다. 애니메이션 삭제 설정은 붙어 있는 동안 계속 지켜본다.
 */
internal class PressScaleNode(
    private var interactionSource: InteractionSource,
    private var enabled: Boolean,
    private var loading: Boolean,
) : Modifier.Node(),
    LayoutModifierNode,
    CompositionLocalConsumerModifierNode {
    private val shrink = Animatable(0f)
    private var pressed = false
    private var reduceMotion = false
    private var pressJob: Job? = null
    private var resolver: ContentResolver? = null
    private var reduceMotionObserver: ContentObserver? = null

    // 층 블록이 shrink.value를 읽으므로 애니메이션 중에는 재배치 없이 층만 다시 그린다.
    private val layerBlock: GraphicsLayerScope.() -> Unit = {
        val scale = 1f + (pressedScaleFactor(size.width, size.height, density) - 1f) * shrink.value
        scaleX = scale
        scaleY = scale
    }

    override fun onAttach() {
        val resolver = currentValueOf(LocalContext).contentResolver
        reduceMotion = resolver.isReduceMotion()
        reduceMotionObserver =
            resolver.observeReduceMotion {
                reduceMotion = it
                // 해제 직전에 올라간 알림이 떼어진 뒤에 도착할 수 있다.
                if (isAttached) animateToTarget()
            }
        this.resolver = resolver
        pressed = false
        if (shrink.value != 0f) coroutineScope.launch { shrink.snapTo(0f) }
        pressJob = coroutineScope.launchPressTracking(interactionSource, ::onPressedChange)
    }

    override fun onDetach() {
        reduceMotionObserver?.let { resolver?.unregisterContentObserver(it) }
        reduceMotionObserver = null
        resolver = null
    }

    fun update(
        interactionSource: InteractionSource,
        enabled: Boolean,
        loading: Boolean,
    ) {
        this.enabled = enabled
        this.loading = loading
        if (interactionSource != this.interactionSource) {
            this.interactionSource = interactionSource
            pressJob?.cancel()
            pressed = false
            if (isAttached) pressJob = coroutineScope.launchPressTracking(interactionSource, ::onPressedChange)
        }
        if (isAttached) animateToTarget()
    }

    private fun onPressedChange(pressed: Boolean) {
        this.pressed = pressed
        animateToTarget()
    }

    private fun animateToTarget() {
        val canShrink = enabled && !loading && !reduceMotion
        val target = if (canShrink && pressed) 1f else 0f
        if (shrink.targetValue == target) return
        val motion = currentValueOf(LocalPushlogMotion)
        coroutineScope.launch {
            shrink.animateTo(target, tween(motion.duration.pressedScale, easing = motion.timingFunction.pressedScale))
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) { placeable.placeWithLayer(0, 0, layerBlock = layerBlock) }
    }

    data class Element(
        val interactionSource: InteractionSource,
        val enabled: Boolean,
        val loading: Boolean,
    ) : ModifierNodeElement<PressScaleNode>() {
        override fun create() = PressScaleNode(interactionSource, enabled, loading)

        override fun update(node: PressScaleNode) = node.update(interactionSource, enabled, loading)
    }
}
