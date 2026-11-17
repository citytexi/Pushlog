package com.citytexi.pushlog.konsist

import com.lemonappdev.konsist.api.declaration.KoAnnotationDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import org.junit.Test
import kotlin.test.assertTrue

class PreviewThemeRulesTest {
    /** A preview function and the same-file functions it calls directly (the `XxxPreviewContent` helper pattern). */
    private class Preview(
        val function: KoFunctionDeclaration,
        val helpers: List<KoFunctionDeclaration>,
    ) {
        val where get() = "${function.containingFile.projectPath}: ${function.name}"
    }

    private fun previews(): List<Preview> {
        val multiPreviews = projectMultiPreviews()
        return allProductionFiles().flatMap { file ->
            val functions = file.functions(includeNested = true, includeLocal = false)
            functions
                .filter { fn -> fn.annotations.any { it.isPreview(multiPreviews) } }
                .map { fn ->
                    val body = fn.text.withoutComments()
                    val helpers =
                        functions.filter { other ->
                            other !== fn && Regex("""\b${Regex.escape(other.name)}\s*[({]""").containsMatchIn(body)
                        }
                    Preview(fn, helpers)
                }
        }
    }

    @Test
    fun `every preview wraps its body in PushlogPreviewTheme`() {
        val previews = previews()
        assertTrue(previews.isNotEmpty(), "no preview functions found, scope is wrong")
        // A bare PushlogTheme paints no background, so the Dark preview shows light text on showBackground's white.
        val violations =
            previews
                .filter { preview ->
                    val texts = (listOf(preview.function) + preview.helpers).map { it.text.withoutComments() }
                    BARE_THEME.containsMatchIn(texts.first()) || texts.none { PREVIEW_THEME_CALL.containsMatchIn(it) }
                }.map { it.where }
        assertTrue(
            violations.isEmpty(),
            "wrap each preview body in PushlogPreviewTheme { }, not PushlogTheme { }:\n${violations.joinToString("\n")}",
        )
    }

    @Test
    fun `only previews use PushlogPreviewTheme`() {
        val allowedByFile = previews().groupBy { it.function.containingFile.projectPath }
        val files = allProductionFiles()
        val definition = files.filter { file -> file.functions().any { it.name == PREVIEW_THEME } }
        assertTrue(definition.size == 1, "$PREVIEW_THEME must be declared exactly once, scope is wrong")
        val reference = Regex("""\b$PREVIEW_THEME\b""")
        val violations =
            (files - definition.toSet())
                .filter { file ->
                    val allowed =
                        allowedByFile[file.projectPath]
                            .orEmpty()
                            .flatMap { listOf(it.function) + it.helpers }
                            .map { it.text }
                    val rest = allowed.fold(file.text) { text, fn -> text.replace(fn, "") }.withoutComments().withoutImports()
                    reference.containsMatchIn(rest)
                }.map { it.projectPath }
        assertTrue(
            violations.isEmpty(),
            "$PREVIEW_THEME is for @Preview bodies only; screens use PushlogTheme:\n${violations.joinToString("\n")}",
        )
    }

    /** Project annotation classes that carry a preview annotation (multipreviews such as `@PushlogPreview`), by fully qualified name. */
    private fun projectMultiPreviews(): Set<String> {
        val annotationClasses = allProductionFiles().flatMap { it.classes(includeNested = true) }.filter { it.hasAnnotationModifier }
        val found = mutableSetOf<String>()
        do {
            val before = found.size
            annotationClasses
                .filter { cls -> cls.annotations.any { it.isPreview(found) } }
                .mapNotNullTo(found) { it.fullyQualifiedName }
        } while (found.size != before)
        return found
    }

    private fun KoAnnotationDeclaration.isPreview(multiPreviews: Set<String>): Boolean {
        val fqn = fullyQualifiedName ?: name
        // Konsist resolves names through imports only; a qualified or same-package use comes back as the simple name.
        val unresolved = fqn == name
        // Preview, PreviewLightDark, PreviewScreenSizes, PreviewFontScale, PreviewDynamicColors, ...
        val tooling =
            name.startsWith("Preview") &&
                name != "PreviewParameter" &&
                (unresolved || fqn.startsWith("$TOOLING_PACKAGE."))
        val multi = fqn in multiPreviews || (unresolved && multiPreviews.any { it.endsWith(".$name") })
        return tooling || multi
    }

    private fun String.withoutComments(): String = replace(Regex("""/\*[\s\S]*?\*/"""), "").replace(Regex("""//[^\n]*"""), "")

    private fun String.withoutImports(): String = lines().filterNot { it.trimStart().startsWith("import ") }.joinToString("\n")

    private companion object {
        const val TOOLING_PACKAGE = "androidx.compose.ui.tooling.preview"
        const val PREVIEW_THEME = "PushlogPreviewTheme"
        val PREVIEW_THEME_CALL = Regex("""\b$PREVIEW_THEME\s*[({]""")
        val BARE_THEME = Regex("""\bPushlogTheme\s*[({]""")
    }
}
