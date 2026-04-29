package com.example.mysampleapplication.data.repository

import com.example.mysampleapplication.data.api.GeminiApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ItemRepositoryImplTest {

    private lateinit var geminiApi: GeminiApi
    private lateinit var repository: ItemRepositoryImpl

    @Before
    fun setUp() {
        geminiApi = mockk()
        repository = ItemRepositoryImpl(geminiApi)
    }

    @Test
    fun `getAllItems returns exactly 90 items`() {
        assertEquals(90, repository.getAllItems().size)
    }

    @Test
    fun `getAllItems first item is Pizza Margherita with id 1`() {
        val first = repository.getAllItems().first()
        assertEquals(1, first.id)
        assertEquals("Pizza Margherita", first.text)
    }

    @Test
    fun `getAllItems last item is Hydration Tracking with id 90`() {
        val last = repository.getAllItems().last()
        assertEquals(90, last.id)
        assertEquals("Hydration Tracking", last.text)
    }

    @Test
    fun `getAllItems IDs are sequential from 1 to 90`() {
        val ids = repository.getAllItems().map { it.id }
        assertEquals((1..90).toList(), ids)
    }

    @Test
    fun `searchItems returns items matching IDs from Gemini JSON response`() = runTest {
        coEvery { geminiApi.generateContent(any()) } returns "[1, 6]"

        val results = repository.searchItems("food")

        assertEquals(2, results.size)
        assertEquals("Pizza Margherita", results[0].text)
        assertEquals("Pad Thai", results[1].text)
    }

    @Test
    fun `searchItems returns empty list when Gemini returns empty array`() = runTest {
        coEvery { geminiApi.generateContent(any()) } returns "[]"

        val results = repository.searchItems("nonexistent thing")

        assertTrue(results.isEmpty())
    }

    @Test
    fun `searchItems handles Gemini response wrapped in markdown code block`() = runTest {
        coEvery { geminiApi.generateContent(any()) } returns "```json\n[11, 12]\n```"

        val results = repository.searchItems("phones")

        assertEquals(2, results.size)
        assertEquals("iPhone 16", results[0].text)
        assertEquals("Samsung Galaxy S25", results[1].text)
    }

    @Test
    fun `searchItems returns empty list when response has no JSON array`() = runTest {
        coEvery { geminiApi.generateContent(any()) } returns "No matching items found."

        val results = repository.searchItems("query")

        assertTrue(results.isEmpty())
    }

    @Test
    fun `getItemDetail sends prompt containing item text`() = runTest {
        val item = repository.getAllItems().first()
        coEvery { geminiApi.generateContent(any()) } returns "A delicious pizza."

        repository.getItemDetail(item)

        coVerify {
            geminiApi.generateContent(match { it.contains("Pizza Margherita") })
        }
    }

    @Test
    fun `getItemDetail returns description from Gemini API`() = runTest {
        val item = repository.getAllItems().first()
        val expected = "Pizza Margherita is a classic Neapolitan pizza."
        coEvery { geminiApi.generateContent(any()) } returns expected

        val result = repository.getItemDetail(item)

        assertEquals(expected, result)
    }
}
