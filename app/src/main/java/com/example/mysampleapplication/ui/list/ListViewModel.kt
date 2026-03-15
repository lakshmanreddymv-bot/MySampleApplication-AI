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

sealed class ListUiState {
    object Idle : ListUiState()
    object Loading : ListUiState()
    data class Success(val items: List<MyListItem>) : ListUiState()
    data class Error(val message: String) : ListUiState()
}

class ListViewModel(
    private val searchItemsUseCase: SearchItemsUseCase,
    val allItems: List<MyListItem>
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Idle)
    val uiState: StateFlow<ListUiState> = _uiState

    fun onQueryChange(query: String) {
        _query.value = query
    }

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

    fun clear() {
        _query.value = ""
        _uiState.value = ListUiState.Idle
    }
}

class ListViewModelFactory(private val repository: ItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ListViewModel(SearchItemsUseCase(repository), repository.getAllItems()) as T
    }
}