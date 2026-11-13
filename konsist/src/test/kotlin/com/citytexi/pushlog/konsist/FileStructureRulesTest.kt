package com.citytexi.pushlog.konsist

import org.junit.Test
import kotlin.test.assertTrue

class FileStructureRulesTest {
    @Test
    fun `production file declares at most one top-level type`() {
        val files = allProductionFiles()
        assertTrue(files.isNotEmpty(), "no production sources found, scope is wrong")
        val violations =
            files.mapNotNull { file ->
                val types =
                    file.classes(includeNested = false).map { it.name } +
                        file.interfaces(includeNested = false).map { it.name } +
                        file.objects(includeNested = false).map { it.name }
                if (types.size > 1) "${file.projectPath}: ${types.joinToString()}" else null
            }
        assertTrue(
            violations.isEmpty(),
            "one top-level class, interface or object per file; split these:\n${violations.joinToString("\n")}",
        )
    }
}
