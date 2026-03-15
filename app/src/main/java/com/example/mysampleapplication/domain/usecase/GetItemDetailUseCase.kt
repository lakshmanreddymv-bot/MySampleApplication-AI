package com.example.mysampleapplication.domain.usecase

import com.example.mysampleapplication.domain.model.MyListItem
import com.example.mysampleapplication.domain.repository.ItemRepository

class GetItemDetailUseCase(private val repository: ItemRepository) {
    suspend operator fun invoke(item: MyListItem): String =
        repository.getItemDetail(item)
}