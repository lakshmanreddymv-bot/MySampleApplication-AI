package com.example.mysampleapplication.domain.usecase

import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository

// S: Single Responsibility — handles only the "get item detail" business operation
// D: Dependency Inversion — depends on ItemRepository interface, not the concrete impl

/**
 * Business logic for generating an AI-powered description of a catalogue item.
 *
 * Delegates to [ItemRepository.getItemDetail] and encapsulates that intent so
 * ViewModels have a clear, named entry point for the detail feature.
 *
 * @property repository Source of AI-generated item descriptions.
 */
class GetItemDetailUseCase(private val repository: ItemRepository) {

    /**
     * Fetches an AI-generated description for [item].
     *
     * @param item The item to describe.
     * @return A short, engaging description produced by the Gemini API.
     */
    suspend operator fun invoke(item: MyListItem): String =
        repository.getItemDetail(item)
}
