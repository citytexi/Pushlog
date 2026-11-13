package com.citytexi.pushlog.preview.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.shadow.PushlogShadowToken
import com.citytexi.pushlog.core.designsystem.theme.shadow.pushlogShadow
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private val SAMPLE_BORDER_WIDTH = 1.dp

/** Registers the shape screen for [ShapePreviewKey]. */
fun EntryProviderScope<NavKey>.shapePreviewEntry(onBack: () -> Unit) {
    entry<ShapePreviewKey> { PreviewScaffold(title = "Shape", onBack = onBack) { ShapePreviewContent() } }
}

/** Spacing, radius and shadow tokens drawn at their real size; draws no background of its own. */
@Composable
fun ShapePreviewContent() {
    PreviewBody {
        val d = PushlogTheme.dimension
        PreviewSectionTitle("spacing")
        listOf(
            "x0Half" to d.x0Half,
            "x1" to d.x1,
            "x1Half" to d.x1Half,
            "x2" to d.x2,
            "x2Half" to d.x2Half,
            "x3" to d.x3,
            "x3Half" to d.x3Half,
            "x4" to d.x4,
            "x4Half" to d.x4Half,
            "x5" to d.x5,
            "x6" to d.x6,
            "x7" to d.x7,
            "x8" to d.x8,
            "x9" to d.x9,
            "x10" to d.x10,
            "x12" to d.x12,
            "x13" to d.x13,
            "x14" to d.x14,
            "x16" to d.x16,
            "spacingX.globalGutter" to d.spacingX.globalGutter,
            "spacingX.betweenChips" to d.spacingX.betweenChips,
            "spacingY.betweenText" to d.spacingY.betweenText,
            "spacingY.componentDefault" to d.spacingY.componentDefault,
            "spacingY.navToTitle" to d.spacingY.navToTitle,
            "spacingY.screenBottom" to d.spacingY.screenBottom,
        ).forEach { (name, size) -> SpacingBar(name, size) }

        PreviewSectionTitle("radius")
        val r = PushlogTheme.radius
        listOf(
            "r0Half" to RoundedCornerShape(r.r0Half),
            "r1" to RoundedCornerShape(r.r1),
            "r1Half" to RoundedCornerShape(r.r1Half),
            "r2" to RoundedCornerShape(r.r2),
            "r2Half" to RoundedCornerShape(r.r2Half),
            "r3" to RoundedCornerShape(r.r3),
            "r3Half" to RoundedCornerShape(r.r3Half),
            "r4" to RoundedCornerShape(r.r4),
            "r5" to RoundedCornerShape(r.r5),
            "r6" to RoundedCornerShape(r.r6),
            "full" to r.full,
        ).forEach { (name, shape) -> RadiusBox(name, shape) }

        PreviewSectionTitle("shadow")
        val s = PushlogTheme.shadow
        listOf("s1" to s.s1, "s2" to s.s2, "s3" to s.s3).forEach { (name, token) -> ShadowCard(name, token) }
    }
}

@Composable
private fun SpacingBar(
    name: String,
    size: Dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3),
    ) {
        Box(
            Modifier
                .width(size)
                .height(PushlogTheme.dimension.x3)
                .background(PushlogTheme.color.bg.brandSolid),
        )
        Text("$name ${size.value.toInt()}dp", style = PushlogTheme.typography.t2Regular, color = PushlogTheme.color.fg.neutral)
    }
}

@Composable
private fun RadiusBox(
    name: String,
    shape: Shape,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3),
    ) {
        Box(
            Modifier
                .size(PushlogTheme.dimension.x12)
                .background(PushlogTheme.color.bg.brandWeak, shape)
                .border(SAMPLE_BORDER_WIDTH, PushlogTheme.color.stroke.brandSolid, shape),
        )
        Text(name, style = PushlogTheme.typography.t3Regular, color = PushlogTheme.color.fg.neutral)
    }
}

@Composable
private fun ShadowCard(
    name: String,
    token: PushlogShadowToken,
) {
    val shape = RoundedCornerShape(PushlogTheme.radius.r3)
    Box(
        Modifier
            .padding(PushlogTheme.dimension.x3)
            .fillMaxWidth()
            .height(PushlogTheme.dimension.x16)
            .pushlogShadow(token, shape)
            .background(PushlogTheme.color.bg.layerFloating, shape)
            .padding(PushlogTheme.dimension.x4),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(name, style = PushlogTheme.typography.t4Medium, color = PushlogTheme.color.fg.neutral)
    }
}

@PushlogPreview
@Composable
private fun ShapePreviewContentPreview() {
    PushlogTheme {
        ShapePreviewContent()
    }
}
