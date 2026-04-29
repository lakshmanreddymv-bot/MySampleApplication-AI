package com.example.mysampleapplication.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository
import com.example.mysampleapplication.domain.usecase.GetItemDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// S: Single Responsibility — manages UI state for the detail screen only
// D: Dependency Inversion — depends on GetItemDetailUseCase and ItemRepository abstractions

/**
 * Sealed class representing every possible state of the detail screen.
 */
sealed class DetailUiState {
    /**
     * The detail is loading. The item title is available early so the top bar can
     * render the item name while the AI description is being fetched.
     *
     * @property item The item being loaded, or null before the lookup completes.
     */
    data class Loading(val item: MyListItem? = null) : DetailUiState()

    /**
     * Description loaded successfully.
     *
     * @property item The catalogue item.
     * @property description AI-generated description text.
     */
    data class Success(val item: MyListItem, val description: String) : DetailUiState()

    /**
     * Loading failed.
     *
     * @property item The item, if it was found before the error occurred (null if ID was invalid).
     * @property message Human-readable error description.
     */
    data class Error(val item: MyListItem?, val message: String) : DetailUiState()
}

/**
 * ViewModel for the item detail screen.
 *
 * Follows Unidirectional Data Flow (UDF):
 * - Events flow UP from UI via [loadDetail]
 * - State flows DOWN to UI via [uiState] StateFlow
 * - No direct state mutation from the UI layer
 *
 * @property getItemDetailUseCase Fetches an AI-generated description for an item.
 * @property repository Used to look up an item by ID before fetching its detail.
 */
class DetailViewModel(
    private val getItemDetailUseCase: GetItemDetailUseCase,
    private val repository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading())

    /** The current screen state. Collect this in the UI to drive recomposition. */
    val uiState: StateFlow<DetailUiState> = _uiState

    /**
     * Loads the AI-generated detail for the item identified by [itemId].
     *
     * Transitions through [DetailUiState.Loading] → [DetailUiState.Success] on success,
     * or [DetailUiState.Loading] → [DetailUiState.Error] if the item is not found or
     * the API call fails.
     *
     * @param itemId The [MyListItem.id] to look up.
     */
    fun loadDetail(itemId: Int) {
        val item = repository.getAllItems().find { it.id == itemId }
        if (item == null) {
            _uiState.value = DetailUiState.Error(null, "Item not found")
            return
        }
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading(item)
            try {
                _uiState.value = DetailUiState.Success(item, getItemDetailUseCase(item))
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(item, e.message ?: "Unknown error")
            }
        }
    }
}

/**
 * Factory for constructing [DetailViewModel] with its dependencies.
 *
 * @property repository Provides both the item lookup and the detail use case's repository.
 */
class DetailViewModelFactory(private val repository: ItemRepository) : ViewModelProvider.Factory {
    /** Creates a [DetailViewModel] wired to [repository]. */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DetailViewModel(GetItemDetailUseCase(repository), repository) as T
    }
}
