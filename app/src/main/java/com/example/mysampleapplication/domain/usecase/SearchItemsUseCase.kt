package com.example.mysampleapplication.domain.usecase

import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository

class SearchItemsUseCase(private val repository: ItemRepository) {
    suspend operator fun invoke(query: String): List<MyListItem> =
        repository.searchItems(query)
}