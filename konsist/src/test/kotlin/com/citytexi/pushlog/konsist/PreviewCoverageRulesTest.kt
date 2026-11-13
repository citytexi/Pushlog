package com.citytexi.pushlog.konsist

import org.junit.Test
import kotlin.test.assertTrue

class PreviewCoverageRulesTest {
    private fun previewFiles() = allProductionFiles().filter { isInPackage(it.packagee?.name.orEmpty(), PREVIEW_PACKAGE) }

    @Test
    fun `every public design system component has a preview screen`() {
        val components =
            allProductionFiles()
                .filter { it.packagee?.name == COMPONENT_PACKAGE }
                .flatMap { it.functions() }
                .filter { it.isTopLevel && it.hasAnnotationWithName("Composable") && it.hasPublicOrDefaultModifier }
                .map { it.name }
                .distinct()
        assertTrue(components.isNotEmpty(), "no design system components found, scope is wrong")
        val screens = previewFiles()
        assertTrue(screens.isNotEmpty(), "no app-preview sources found, scope is wrong")
        val screenNames = screens.map { it.name }.toSet()
        val missing = components.filter { "${it}PreviewScreen" !in screenNames }
        assertTrue(missing.isEmpty(), "components without a preview screen:\n${missing.joinToString("\n")}")
        val keyNames =
            screens
                .flatMap { it.objects(includeNested = true) + it.classes(includeNested = true) }
                .map { it.name }
                .toSet()
        val keyless = components.filter { "${it}PreviewKey" !in keyNames }
        assertTrue(keyless.isEmpty(), "components without a preview key declaration:\n${keyless.joinToString("\n")}")
    }

    @Test
    fun `every preview key is listed in the catalog`() {
        val files = previewFiles()
        assertTrue(files.isNotEmpty(), "no app-preview sources found, scope is wrong")
        val catalog = files.filter { it.name == "PreviewCatalog" }
        assertTrue(catalog.size == 1, "PreviewCatalog.kt not found exactly once")
        val catalogText =
            catalog
                .single()
                .text
                .replace(Regex("/\\*[\\s\\S]*?\\*/"), "")
                .replace(Regex("//[^\\n]*"), "")
                .lines()
                .filterNot { it.trimStart().startsWith("import ") }
                .joinToString("\n")
        val keys =
            files
                .flatMap { it.objects(includeNested = true) + it.classes(includeNested = true) }
                .filter { decl -> decl.parents().any { it.name == "NavKey" } }
                .map { it.name }
                .filter { it != "PreviewHomeKey" }
        assertTrue(keys.isNotEmpty(), "no preview keys found, scope is wrong")
        val orphans = keys.filter { !Regex("\\b${Regex.escape(it)}\\b").containsMatchIn(catalogText) }
        assertTrue(orphans.isEmpty(), "preview keys missing from PreviewCatalog.kt:\n${orphans.joinToString("\n")}")
    }

    private companion object {
        const val PREVIEW_PACKAGE = "com.citytexi.pushlog.preview"
        const val COMPONENT_PACKAGE = "com.citytexi.pushlog.core.designsystem.component"
    }
}
