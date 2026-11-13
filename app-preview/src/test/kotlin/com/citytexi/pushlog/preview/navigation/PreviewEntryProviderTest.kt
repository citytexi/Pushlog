package com.citytexi.pushlog.preview.navigation

import com.citytexi.pushlog.preview.catalog.previewCatalog
import kotlinx.serialization.serializer
import org.junit.Test
import kotlin.test.assertEquals

class PreviewEntryProviderTest {
    private val keys = previewCatalog.map { it.key } + PreviewHomeKey

    @Test
    fun `every catalog key and the home key have an entry`() {
        val provider = previewEntryProvider(onOpen = {}, onBack = {})
        // entryProvider's fallback throws for a key that was never registered.
        keys.forEach { key -> assertEquals(key.toString(), provider(key).contentKey) }
    }

    @Test
    fun `every key has a generated serializer for back stack restore`() {
        // rememberNavBackStack saves keys across process death; a key without @Serializable throws here.
        keys.forEach { key -> serializer(key::class.java) }
    }
}
