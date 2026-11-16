package com.citytexi.pushlog.core.designsystem.component.pressable

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import com.citytexi.pushlog.core.designsystem.theme.LocalPushlogMotion
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** 표면 배경. 눌리면 [pressedColor]로 바뀌고, 로딩 중에는 눌림과 상관없이 [pressedColor]에 머문다. 글자·아이콘 색은 건드리지 않는다. */
internal class PressColorNode(
    private var interactionSource: InteractionSource,
    private var color: Color,
    private var pressedColor: Color,
    private var enabled: Boolean,
    private var loading: Boolean,
) : Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {
    private var pressed = false
    private val background = Animatable(target())
    private var pressJob: Job? = null

    override fun onAttach() {
        pressed = false
        val initial = target()
        if (background.value != initial) coroutineScope.launch { background.snapTo(initial) }
        pressJob = coroutineScope.launchPressTracking(interactionSource, ::onPressedChange)
    }

    fun update(
        interactionSource: InteractionSource,
        color: Color,
        pressedColor: Color,
        enabled: Boolean,
        loading: Boolean,
    ) {
        this.color = color
        this.pressedColor = pressedColor
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

    private fun target(): Color = if (loading || (enabled && pressed)) pressedColor else color

    private fun onPressedChange(pressed: Boolean) {
        this.pressed = pressed
        animateToTarget()
    }

    private fun animateToTarget() {
        val target = target()
        if (background.targetValue == target) return
        val motion = currentValueOf(LocalPushlogMotion)
        coroutineScope.launch {
            background.animateTo(target, tween(motion.duration.colorTransition, easing = motion.timingFunction.easing))
        }
    }

    override fun ContentDrawScope.draw() {
        drawRect(background.value)
        drawContent()
    }

    data class Element(
        val interactionSource: InteractionSource,
        val color: Color,
        val pressedColor: Color,
        val enabled: Boolean,
        val loading: Boolean,
    ) : ModifierNodeElement<PressColorNode>() {
        override fun create() = PressColorNode(interactionSource, color, pressedColor, enabled, loading)

        override fun update(node: PressColorNode) = node.update(interactionSource, color, pressedColor, enabled, loading)
    }
}
