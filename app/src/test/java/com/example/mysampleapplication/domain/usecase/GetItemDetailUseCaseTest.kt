package com.example.mysampleapplication.domain.usecase

import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetItemDetailUseCaseTest {

    private lateinit var repository: ItemRepository
    private lateinit var useCase: GetItemDetailUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetItemDetailUseCase(repository)
    }

    @Test
    fun `invoke delegates item to repository`() = runTest {
        val item = MyListItem(1, "Pizza Margherita")
        coEvery { repository.getItemDetail(item) } returns "A classic Neapolitan pizza."

        useCase(item)

        coVerify(exactly = 1) { repository.getItemDetail(item) }
    }

    @Test
    fun `invoke returns description from repository`() = runTest {
        val item = MyListItem(42, "Bengal Tiger")
        val expected = "The Bengal tiger is the most common tiger subspecies."
        coEvery { repository.getItemDetail(item) } returns expected

        val result = useCase(item)

        assertEquals(expected, result)
    }

    @Test
    fun `invoke propagates exception from repository`() = runTest {
        val item = MyListItem(1, "Pizza Margherita")
        coEvery { repository.getItemDetail(item) } throws RuntimeException("Network error")

        var thrown: Exception? = null
        try {
            useCase(item)
        } catch (e: Exception) {
            thrown = e
        }

        assertEquals("Network error", thrown?.message)
    }
}
