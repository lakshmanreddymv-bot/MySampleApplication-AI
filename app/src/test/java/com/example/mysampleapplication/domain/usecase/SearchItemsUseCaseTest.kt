package com.example.mysampleapplication.domain.usecase

import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchItemsUseCaseTest {

    private lateinit var repository: ItemRepository
    private lateinit var useCase: SearchItemsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SearchItemsUseCase(repository)
    }

    @Test
    fun `invoke delegates search query to repository`() = runTest {
        coEvery { repository.searchItems("pizza") } returns listOf(MyListItem(1, "Pizza Margherita"))

        useCase("pizza")

        coVerify(exactly = 1) { repository.searchItems("pizza") }
    }

    @Test
    fun `invoke returns items from repository`() = runTest {
        val expected = listOf(MyListItem(1, "Pizza Margherita"), MyListItem(6, "Pad Thai"))
        coEvery { repository.searchItems("food") } returns expected

        val result = useCase("food")

        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns empty list when repository finds no matches`() = runTest {
        coEvery { repository.searchItems("xyzzy") } returns emptyList()

        val result = useCase("xyzzy")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke propagates exception thrown by repository`() = runTest {
        coEvery { repository.searchItems(any()) } throws RuntimeException("API error")

        var thrown: Exception? = null
        try {
            useCase("query")
        } catch (e: Exception) {
            thrown = e
        }

        assertEquals("API error", thrown?.message)
    }

    @Test
    fun `invoke passes query string verbatim to repository`() = runTest {
        val query = "natural language search query with spaces"
        coEvery { repository.searchItems(query) } returns emptyList()

        useCase(query)

        coVerify { repository.searchItems(query) }
    }
}
