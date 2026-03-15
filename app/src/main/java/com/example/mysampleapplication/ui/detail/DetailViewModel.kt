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

sealed class DetailUiState {
    data class Loading(val item: MyListItem? = null) : DetailUiState()
    data class Success(val item: MyListItem, val description: String) : DetailUiState()
    data class Error(val item: MyListItem?, val message: String) : DetailUiState()
}

class DetailViewModel(
    private val getItemDetailUseCase: GetItemDetailUseCase,
    private val repository: ItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading())
    val uiState: StateFlow<DetailUiState> = _uiState

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

class DetailViewModelFactory(private val repository: ItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DetailViewModel(GetItemDetailUseCase(repository), repository) as T
    }
}