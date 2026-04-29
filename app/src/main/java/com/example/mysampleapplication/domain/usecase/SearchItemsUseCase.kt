package com.example.mysampleapplication.domain.usecase

import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository

// S: Single Responsibility — handles only the "search items" business operation
// D: Dependency Inversion — depends on ItemRepository interface, not the concrete impl

/**
 * Business logic for searching the item catalogue using natural language.
 *
 * Delegates to [ItemRepository.searchItems] and acts as the single entry point for
 * the search feature. ViewModels call this use case rather than the repository directly,
 * keeping the domain rule in one place.
 *
 * @property repository Source of item data and AI-powered search.
 */
class SearchItemsUseCase(private val repository: ItemRepository) {

    /**
     * Executes a natural language search against the item catalogue.
     *
     * @param query The user's search string.
     * @return Items from the catalogue that match the query.
     */
    suspend operator fun invoke(query: String): List<MyListItem> =
        repository.searchItems(query)
}
