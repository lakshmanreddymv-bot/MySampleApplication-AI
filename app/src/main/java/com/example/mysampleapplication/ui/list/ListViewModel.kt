package com.example.mysampleapplication.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository
import com.example.mysampleapplication.domain.usecase.SearchItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// S: Single Responsibility — manages UI state for the list/search screen only
// D: Dependency Inversion — depends on SearchItemsUseCase abstraction, not the repository directly

/**
 * Sealed class representing every possible state of the list/search screen.
 *
 * Using a sealed hierarchy ensures the UI handles all states exhaustively via a `when` expression.
 */
sealed class ListUiState {
    /** The screen has loaded but no search has been performed yet. Shows the full catalogue. */
    object Idle : ListUiState()

    /** A search is in progress. The previous list is shown dimmed behind a loading overlay. */
    object Loading : ListUiState()

    /**
     * Search completed successfully.
     * @property items The filtered list of matching items. May be empty.
     */
    data class Success(val items: List<MyListItem>) : ListUiState()

    /**
     * Search failed with an error.
     * @property message Human-readable description of what went wrong.
     */
    data class Error(val message: String) : ListUiState()
}

/**
 * ViewModel for the list/search screen.
 *
 * Follows Unidirectional Data Flow (UDF):
 * - Events flow UP from UI via public functions ([search], [onQueryChange], [clear])
 * - State flows DOWN to UI via [uiState] and [query] StateFlows
 * - No direct state mutation from the UI layer
 *
 * @property searchItemsUseCase Business logic for AI-powered item search.
 * @property allItems The full catalogue, pre-loaded for display in the Idle state.
 */
class ListViewModel(
    private val searchItemsUseCase: SearchItemsUseCase,
    val allItems: List<MyListItem>
) : ViewModel() {

    private val _query = MutableStateFlow("")

    /** The current search query string, bound two-way to the search field. */
    val query: StateFlow<String> = _query

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Idle)

    /** The current screen state. Collect this in the UI to drive recomposition. */
    val uiState: StateFlow<ListUiState> = _uiState

    /**
     * Updates the search query as the user types.
     *
     * @param query The latest value from the text field.
     */
    fun onQueryChange(query: String) {
        _query.value = query
    }

    /**
     * Executes an AI search for [query].
     *
     * Transitions to [ListUiState.Loading] immediately, then emits [ListUiState.Success]
     * or [ListUiState.Error] when the operation completes. Calling with a blank [query]
     * is equivalent to calling [clear].
     *
     * @param query The search string to send to the Gemini API.
     */
    fun search(query: String) {
        if (query.isBlank()) { clear(); return }
        viewModelScope.launch {
            _uiState.value = ListUiState.Loading
            try {
                _uiState.value = ListUiState.Success(searchItemsUseCase(query))
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Clears the search query and resets the screen to the [ListUiState.Idle] state,
     * showing the full catalogue again.
     */
    fun clear() {
        _query.value = ""
        _uiState.value = ListUiState.Idle
    }
}

/**
 * Factory for constructing [ListViewModel] with its dependencies.
 *
 * Wires together [SearchItemsUseCase] and the pre-loaded item list from the repository.
 *
 * @property repository The item repository used to build the use case and load all items.
 */
class ListViewModelFactory(private val repository: ItemRepository) : ViewModelProvider.Factory {
    /** Creates a [ListViewModel] with a [SearchItemsUseCase] backed by [repository]. */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ListViewModel(SearchItemsUseCase(repository), repository.getAllItems()) as T
    }
}
