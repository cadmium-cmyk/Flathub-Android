package com.example

import com.example.data.api.AppSummaryHit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testStringMainCategoriesParsing() {
        val jsonWithStringCategory = """
            {
                "app_id": "io.github.riyaniii.Kaori",
                "name": "Kaori",
                "summary": "Discover and read comics",
                "main_categories": "utility"
            }
        """.trimIndent()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter = moshi.adapter(AppSummaryHit::class.java)
        val hit = adapter.fromJson(jsonWithStringCategory)

        assertNotNull(hit)
        assertEquals("Kaori", hit?.name)
        assertEquals(listOf("utility"), hit?.mainCategories)
    }

    @Test
    fun testArrayMainCategoriesParsing() {
        val jsonWithArrayCategory = """
            {
                "app_id": "re.sonny.Tangram",
                "name": "Tangram",
                "summary": "Browser for your pinned tabs",
                "main_categories": ["network", "web"]
            }
        """.trimIndent()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter = moshi.adapter(AppSummaryHit::class.java)
        val hit = adapter.fromJson(jsonWithArrayCategory)

        assertNotNull(hit)
        assertEquals("Tangram", hit?.name)
        assertEquals(listOf("network", "web"), hit?.mainCategories)
    }
}
