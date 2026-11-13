package com.citytexi.pushlog.konsist

import org.junit.Test
import kotlin.test.assertTrue

class DesignTokenRulesTest {
    private fun screenFiles() = productionFiles("feature") + productionFiles("app")

    @Test
    fun `feature and app do not import MaterialTheme`() {
        val files = screenFiles()
        assertTrue(files.isNotEmpty(), "no feature or app sources found, scope is wrong")
        // An aliased import keeps its full name in KoImportDeclaration.name, so aliases are caught too.
        val violations =
            files.flatMap { file ->
                file.imports
                    .filter { it.name == "androidx.compose.material3.MaterialTheme" }
                    .map { "${file.projectPath}: ${it.name}" }
            }
        assertTrue(violations.isEmpty(), "use PushlogTheme instead of MaterialTheme:\n${violations.joinToString("\n")}")
    }

    @Test
    fun `feature and app do not create raw colors`() {
        val files = screenFiles()
        assertTrue(files.isNotEmpty(), "no feature or app sources found, scope is wrong")
        val rawColor = Regex("""\bColor\(\s*(0x|red\s*=)""")
        val violations = files.filter { rawColor.containsMatchIn(it.text) }.map { it.projectPath }
        assertTrue(violations.isEmpty(), "use color tokens, not Color(...) literals:\n${violations.joinToString("\n")}")
    }
}
