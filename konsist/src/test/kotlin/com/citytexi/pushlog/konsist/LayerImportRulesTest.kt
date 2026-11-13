package com.citytexi.pushlog.konsist

import org.junit.Test
import kotlin.test.assertTrue

class LayerImportRulesTest {
    @Test
    fun `feature does not import data or core network database datastore packages`() {
        val files = productionFiles("feature")
        assertTrue(files.isNotEmpty(), "no feature sources found, scope is wrong")
        val forbidden =
            listOf(
                "com.citytexi.pushlog.data",
                "com.citytexi.pushlog.core.network",
                "com.citytexi.pushlog.core.database",
                "com.citytexi.pushlog.core.datastore",
            )
        val violations =
            files.flatMap { file ->
                file.imports
                    .filter { import -> forbidden.any { isInPackage(import.name, it) } }
                    .map { "${file.projectPath}: ${it.name}" }
            }
        assertTrue(violations.isEmpty(), "feature imports forbidden layers:\n${violations.joinToString("\n")}")
    }

    @Test
    fun `domain does not import android or androidx`() {
        val files = productionFiles("domain")
        assertTrue(files.isNotEmpty(), "no domain sources found, scope is wrong")
        val violations =
            files.flatMap { file ->
                file.imports
                    .filter { isInPackage(it.name, "android") || isInPackage(it.name, "androidx") }
                    .map { "${file.projectPath}: ${it.name}" }
            }
        assertTrue(violations.isEmpty(), "domain must stay Android-free:\n${violations.joinToString("\n")}")
    }

    @Test
    fun `core ui does not import designsystem or material3`() {
        val files =
            productionFiles("core").filter {
                isInPackage(it.packagee?.name.orEmpty(), "com.citytexi.pushlog.core.ui")
            }
        assertTrue(files.isNotEmpty(), "no core:ui sources found, scope is wrong")
        val forbidden = listOf("com.citytexi.pushlog.core.designsystem", "androidx.compose.material3")
        val violations =
            files.flatMap { file ->
                file.imports
                    .filter { import -> forbidden.any { isInPackage(import.name, it) } }
                    .map { "${file.projectPath}: ${it.name}" }
            }
        assertTrue(violations.isEmpty(), "core:ui must stay below designsystem and material3:\n${violations.joinToString("\n")}")
    }
}
