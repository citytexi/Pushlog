package com.citytexi.pushlog.konsist

import org.junit.Test
import kotlin.test.assertTrue

class ComponentRulesTest {
    private fun componentFiles() = allProductionFiles().filter { isInPackage(it.packagee?.name.orEmpty(), COMPONENT_PACKAGE) }

    @Test
    fun `components live in a kind subpackage`() {
        val files = componentFiles()
        assertTrue(files.isNotEmpty(), "no design system components found, scope is wrong")
        val violations = files.filter { it.packagee?.name == COMPONENT_PACKAGE }.map { it.projectPath }
        assertTrue(
            violations.isEmpty(),
            "put components in $COMPONENT_PACKAGE.<kind>, not the component package itself:\n${violations.joinToString("\n")}",
        )
    }

    @Test
    fun `only the pressable package handles taps`() {
        val files = componentFiles().filterNot { isInPackage(it.packagee?.name.orEmpty(), PRESSABLE_PACKAGE) }
        assertTrue(files.isNotEmpty(), "no design system components outside pressable found, scope is wrong")
        // A qualified call skips the import, so the call text is checked too.
        val call = Regex("""\b(clickable|combinedClickable|toggleable|triStateToggleable|selectable|indication|ripple)\s*[({]""")
        val violations =
            files.flatMap { file ->
                file.imports.filter { it.name in TAP_IMPORTS }.map { "${file.projectPath}: ${it.name}" } +
                    call.findAll(file.text).map { "${file.projectPath}: ${it.value}" }
            }
        assertTrue(
            violations.isEmpty(),
            "build taps on PushlogPressableSurface instead of handling them directly:\n${violations.joinToString("\n")}",
        )
    }

    private companion object {
        const val COMPONENT_PACKAGE = "com.citytexi.pushlog.core.designsystem.component"
        const val PRESSABLE_PACKAGE = "$COMPONENT_PACKAGE.pressable"
        val TAP_IMPORTS =
            setOf(
                "androidx.compose.foundation.clickable",
                "androidx.compose.foundation.combinedClickable",
                "androidx.compose.foundation.indication",
                "androidx.compose.foundation.selection.toggleable",
                "androidx.compose.foundation.selection.triStateToggleable",
                "androidx.compose.foundation.selection.selectable",
                "androidx.compose.material3.ripple",
                "androidx.compose.material.ripple.rememberRipple",
            )
    }
}
