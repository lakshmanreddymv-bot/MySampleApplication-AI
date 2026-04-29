package com.example.mysampleapplication.ui.list

import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.usecase.SearchItemsUseCase
import com.example.mysampleapplication.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var searchItemsUseCase: SearchItemsUseCase
    private lateinit var allItems: List<MyListItem>
    private lateinit var viewModel: ListViewModel

    @Before
    fun setUp() {
        searchItemsUseCase = mockk()
        allItems = listOf(
            MyListItem(1, "Pizza Margherita"),
            MyListItem(2, "Sushi Roll"),
            MyListItem(3, "Burger and Fries")
        )
        viewModel = ListViewModel(searchItemsUseCase, allItems)
    }

    @Test
    fun `initial uiState is Idle`() {
        assertTrue(viewModel.uiState.value is ListUiState.Idle)
    }

    @Test
    fun `initial query is empty string`() {
        assertEquals("", viewModel.query.value)
    }

    @Test
    fun `allItems is populated from constructor`() {
        assertEquals(3, viewModel.allItems.size)
        assertEquals("Pizza Margherita", viewModel.allItems[0].text)
    }

    @Test
    fun `onQueryChange updates query state`() {
        viewModel.onQueryChange("sushi")

        assertEquals("sushi", viewModel.query.value)
    }

    @Test
    fun `search with blank query calls clear and resets state`() {
        viewModel.onQueryChange("something")
        viewModel.search("   ")

        assertEquals("", viewModel.query.value)
        assertTrue(viewModel.uiState.value is ListUiState.Idle)
    }

    @Test
    fun `search success transitions to Success state with results`() = runTest {
        val results = listOf(MyListItem(2, "Sushi Roll"))
        coEvery { searchItemsUseCase("sushi") } returns results

        viewModel.search("sushi")

        val state = viewModel.uiState.value
        assertTrue(state is ListUiState.Success)
        assertEquals(results, (state as ListUiState.Success).items)
    }

    @Test
    fun `search error transitions to Error state with message`() = runTest {
        coEvery { searchItemsUseCase("fail") } throws RuntimeException("API down")

        viewModel.search("fail")

        val state = viewModel.uiState.value
        assertTrue(state is ListUiState.Error)
        assertEquals("API down", (state as ListUiState.Error).message)
    }

    @Test
    fun `clear resets query to empty and uiState to Idle`() = runTest {
        coEvery { searchItemsUseCase(any()) } returns listOf(MyListItem(1, "Pizza Margherita"))
        viewModel.search("pizza")

        viewModel.clear()

        assertEquals("", viewModel.query.value)
        assertTrue(viewModel.uiState.value is ListUiState.Idle)
    }

    @Test
    fun `search invokes use case with correct query`() = runTest {
        coEvery { searchItemsUseCase("burger") } returns emptyList()

        viewModel.search("burger")

        coVerify(exactly = 1) { searchItemsUseCase("burger") }
    }
}
