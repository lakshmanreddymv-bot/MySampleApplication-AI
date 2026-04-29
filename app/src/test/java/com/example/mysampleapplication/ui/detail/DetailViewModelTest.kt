package com.example.mysampleapplication.ui.detail

import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository
import com.example.mysampleapplication.domain.usecase.GetItemDetailUseCase
import com.example.mysampleapplication.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ItemRepository
    private lateinit var getItemDetailUseCase: GetItemDetailUseCase
    private lateinit var viewModel: DetailViewModel

    private val testItems = listOf(
        MyListItem(1, "Pizza Margherita"),
        MyListItem(42, "Bengal Tiger")
    )

    @Before
    fun setUp() {
        repository = mockk()
        getItemDetailUseCase = mockk()
        coEvery { repository.getAllItems() } returns testItems
        viewModel = DetailViewModel(getItemDetailUseCase, repository)
    }

    @Test
    fun `initial uiState is Loading with null item`() {
        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Loading)
        assertNull((state as DetailUiState.Loading).item)
    }

    @Test
    fun `loadDetail with invalid id sets Error state with null item`() = runTest {
        viewModel.loadDetail(999)

        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Error)
        assertNull((state as DetailUiState.Error).item)
        assertEquals("Item not found", state.message)
    }

    @Test
    fun `loadDetail with valid id transitions to Success state`() = runTest {
        val item = testItems[0]
        val description = "A classic Neapolitan pizza with tomato and mozzarella."
        coEvery { getItemDetailUseCase(item) } returns description

        viewModel.loadDetail(1)

        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Success)
        assertEquals(item, (state as DetailUiState.Success).item)
        assertEquals(description, state.description)
    }

    @Test
    fun `loadDetail with valid id sets correct item name in Success`() = runTest {
        val item = testItems[1]
        coEvery { getItemDetailUseCase(item) } returns "The Bengal tiger is the most common subspecies."

        viewModel.loadDetail(42)

        val state = viewModel.uiState.value as DetailUiState.Success
        assertEquals("Bengal Tiger", state.item.text)
    }

    @Test
    fun `loadDetail error transitions to Error state with item`() = runTest {
        val item = testItems[0]
        coEvery { getItemDetailUseCase(item) } throws RuntimeException("Network unavailable")

        viewModel.loadDetail(1)

        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Error)
        assertEquals(item, (state as DetailUiState.Error).item)
        assertEquals("Network unavailable", state.message)
    }
}
