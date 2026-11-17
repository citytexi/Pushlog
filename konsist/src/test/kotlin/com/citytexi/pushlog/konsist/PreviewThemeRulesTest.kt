package com.citytexi.pushlog.konsist

import org.junit.Test
import kotlin.test.assertTrue

class PreviewThemeRulesTest {
    @Test
    fun `preview files wrap their body in PushlogPreviewTheme`() {
        val files =
            allProductionFiles().filter { file ->
                file.functions().any { fn -> PREVIEW_ANNOTATIONS.any { fn.hasAnnotationWithName(it) } }
            }
        assertTrue(files.isNotEmpty(), "no preview functions found, scope is wrong")
        // A bare PushlogTheme paints no background, so the Dark preview shows light text on showBackground's white.
        val bareTheme = Regex("""\bPushlogTheme\s*[({]""")
        val violations =
            files
                .filter { bareTheme.containsMatchIn(it.text) || !it.text.contains("PushlogPreviewTheme") }
                .map { it.projectPath }
        assertTrue(
            violations.isEmpty(),
            "wrap preview bodies in PushlogPreviewTheme { }, not PushlogTheme { }:\n${violations.joinToString("\n")}",
        )
    }

    private companion object {
        val PREVIEW_ANNOTATIONS = listOf("PushlogPreview", "Preview")
    }
}
